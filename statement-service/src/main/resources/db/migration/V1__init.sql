CREATE TABLE IF NOT EXISTS statement_line (
  id BIGSERIAL PRIMARY KEY,
  account_id UUID NOT NULL,
  txn_id VARCHAR(64) NOT NULL,
  transfer_id UUID,
  direction VARCHAR(6) NOT NULL,
  amount_paise BIGINT NOT NULL,
  narrative TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_statement_account_time ON statement_line(account_id, created_at);
