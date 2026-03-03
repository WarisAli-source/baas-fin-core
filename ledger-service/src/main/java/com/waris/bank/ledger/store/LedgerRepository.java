package com.waris.bank.ledger.store;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class LedgerRepository {

  private final JdbcTemplate jdbc;

  public LedgerRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public void ensureAccount(UUID accountId) {
    jdbc.update("INSERT INTO ledger_account(account_id,status) VALUES (?, 'ACTIVE') ON CONFLICT (account_id) DO NOTHING", accountId);
    jdbc.update("INSERT INTO balance_snapshot(account_id) VALUES (?) ON CONFLICT (account_id) DO NOTHING", accountId);
  }

  public long getAvailable(UUID accountId) {
    ensureAccount(accountId);
    Long v = jdbc.queryForObject("SELECT available_paise FROM balance_snapshot WHERE account_id=?", Long.class, accountId);
    return v == null ? 0 : v;
  }

  public boolean txnExists(String txnId) {
    Integer c = jdbc.queryForObject("SELECT COUNT(1) FROM ledger_transaction WHERE txn_id=?", Integer.class, txnId);
    return c != null && c > 0;
  }

  public void createTxn(String txnId, String transferId, String type, String status) {
    jdbc.update("INSERT INTO ledger_transaction(txn_id, transfer_id, txn_type, status) VALUES (?,?,?,?)",
        txnId, transferId, type, status);
  }

  public void postEntry(String txnId, UUID accountId, String direction, long amountPaise, String narrative) {
    ensureAccount(accountId);
    jdbc.update("INSERT INTO ledger_entry(txn_id, account_id, direction, amount_paise, narrative) VALUES (?,?,?,?,?) " +
        "ON CONFLICT (txn_id, account_id, direction) DO NOTHING",
        txnId, accountId, direction, amountPaise, narrative);

    // Update snapshots (simplified: available=current)
    if ("CREDIT".equals(direction)) {
      jdbc.update("UPDATE balance_snapshot SET available_paise = available_paise + ?, current_paise=current_paise+?, updated_at=now() WHERE account_id=?",
          amountPaise, amountPaise, accountId);
    } else {
      jdbc.update("UPDATE balance_snapshot SET available_paise = available_paise - ?, current_paise=current_paise-?, updated_at=now() WHERE account_id=?",
          amountPaise, amountPaise, accountId);
    }
  }

  public void markTxn(String txnId, String status) {
    jdbc.update("UPDATE ledger_transaction SET status=? WHERE txn_id=?", status, txnId);
  }
}
