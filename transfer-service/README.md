# transfer-service

Handles **user money transfers** (P2P) as an orchestration service.

## Key points
- Checks sender balance via **gRPC** to ledger-service.
- Writes transfer + **outbox** event in the same DB transaction.
- Outbox publisher publishes to Kafka topic `transfer.commands.v1`.

## Endpoint
- `POST /api/v1/transfers` (requires header `Idempotency-Key`)

## Status
- INITIATED → COMPLETED/FAILED (based on `ledger.events.v1`)
