CREATE TABLE IF NOT EXISTS ledger_account (
  account_id UUID PRIMARY KEY,
  user_id UUID,
  status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS ledger_transaction (
  txn_id VARCHAR(64) PRIMARY KEY,
  transfer_id VARCHAR(64),
  txn_type VARCHAR(32) NOT NULL, -- TRANSFER/ADJUSTMENT/SEED
  status VARCHAR(16) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS ledger_entry (
  entry_id BIGSERIAL PRIMARY KEY,
  txn_id VARCHAR(64) NOT NULL REFERENCES ledger_transaction(txn_id),
  account_id UUID NOT NULL,
  direction VARCHAR(6) NOT NULL CHECK (direction IN ('DEBIT','CREDIT')),
  amount_paise BIGINT NOT NULL CHECK (amount_paise > 0),
  narrative TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (txn_id, account_id, direction)
);

CREATE TABLE IF NOT EXISTS balance_snapshot (
  account_id UUID PRIMARY KEY,
  available_paise BIGINT NOT NULL DEFAULT 0,
  current_paise BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
