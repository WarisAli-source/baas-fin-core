CREATE TABLE IF NOT EXISTS bank_account (
  account_id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  status VARCHAR(16) NOT NULL, -- ACTIVE/FROZEN/CLOSED
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_bank_account_user ON bank_account(user_id);
