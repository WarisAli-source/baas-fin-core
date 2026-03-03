CREATE TABLE IF NOT EXISTS transfer (
  transfer_id UUID PRIMARY KEY,
  txn_id VARCHAR(64) NOT NULL UNIQUE,
  from_account_id UUID NOT NULL,
  to_account_id UUID NOT NULL,
  amount_paise BIGINT NOT NULL CHECK (amount_paise > 0),
  status VARCHAR(16) NOT NULL,
  initiated_by UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS outbox_event (
  event_id UUID PRIMARY KEY,
  aggregate_id UUID NOT NULL,
  event_type VARCHAR(64) NOT NULL,
  payload JSONB NOT NULL,
  status VARCHAR(16) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  sent_at TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS idx_outbox_status ON outbox_event(status, created_at);
