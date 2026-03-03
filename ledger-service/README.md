# ledger-service (Core Bank)

**Source of truth** for money. Implements **double-entry ledger**.

## Interfaces
- gRPC on `:9090` (internal)
- Kafka consumer for `transfer.commands.v1`
- Kafka producer for `ledger.events.v1`

## What it guarantees
- Every transfer is posted as **DEBIT(from)** + **CREDIT(to)**
- `txnId` is idempotent

## Run
Use docker compose from root.
