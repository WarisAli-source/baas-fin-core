# BaaS Fin Core

A modular **Banking-as-a-Service (BaaS)** backend built with Java/Spring Boot, event-driven messaging, and gRPC.

The platform demonstrates how a transfer request flows through an API gateway, orchestration service, ledger source-of-truth, and downstream read/analytics/audit consumers.

## Tech Stack
- Java 21 + Spring Boot 3
- Spring Cloud Gateway
- PostgreSQL (database per service)
- Apache Kafka (async commands/events)
- gRPC (low-latency internal calls)
- Docker Compose for local orchestration

## Services
| Service | Port | Responsibility |
|---|---:|---|
| `api-gateway` | 8080 | Single entrypoint and JWT validation for non-auth routes |
| `auth-service` | 8081 | Signup/login and JWT token issuing |
| `account-service` | 8082 | Account metadata (ACTIVE/FROZEN/CLOSED), ownership metadata |
| `transfer-service` | 8083 | Transfer orchestration, pre-checks, idempotency/outbox |
| `statement-service` | 8084 | Statement read model (MVP projection) |
| `analytics-service` | 8085 | Live metrics based on Kafka streams |
| `audit-service` | 8086 | Immutable audit log consumer + retrieval API |
| `ledger-service` | gRPC 9090 | Double-entry posting and balance source of truth |

> Currency is currently **INR only**, represented in **paise** (`1 INR = 100 paise`).

## Architecture at a Glance
1. Client calls APIs through `api-gateway`.
2. Authenticated user creates/uses accounts.
3. Transfer request goes to `transfer-service` with an `Idempotency-Key`.
4. `transfer-service` checks available balance from `ledger-service` over gRPC.
5. Transfer row + outbox message are written atomically.
6. Outbox publisher emits command to Kafka topic `transfer.commands.v1`.
7. `ledger-service` consumes command and posts DEBIT/CREDIT entries.
8. `ledger-service` publishes result event to `ledger.events.v1`.
9. Read-side services (`statement`, `analytics`, `audit`) consume and update their own stores.

## Run Locally
```bash
docker compose up --build
```

## Quick API Smoke Flow

### 1) Signup users
```bash
curl -s -X POST http://localhost:8080/api/v1/auth/signup \
  -H 'Content-Type: application/json' \
  -d '{"email":"a@test.com","password":"Pass@123"}'

curl -s -X POST http://localhost:8080/api/v1/auth/signup \
  -H 'Content-Type: application/json' \
  -d '{"email":"b@test.com","password":"Pass@123"}'
```

### 2) Create accounts
```bash
export TOKEN_A='PASTE_USER_A_TOKEN'
export TOKEN_B='PASTE_USER_B_TOKEN'

curl -s -X POST http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer $TOKEN_A"

curl -s -X POST http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer $TOKEN_B"
```

### 3) Seed sender balance (temporary bootstrap)
```bash
docker exec -it bank-postgres psql -U bank -d ledgerdb \
  -c "INSERT INTO ledger_account(account_id,status) VALUES ('ACCOUNT_UUID_FROM_STEP2','ACTIVE') ON CONFLICT DO NOTHING;" \
  -c "INSERT INTO balance_snapshot(account_id,available_paise,current_paise) VALUES ('ACCOUNT_UUID_FROM_STEP2',500000,500000) ON CONFLICT (account_id) DO UPDATE SET available_paise=500000,current_paise=500000;"
```

### 4) Create transfer A → B
```bash
export A_ACCOUNT='FROM_ACCOUNT_UUID'
export B_ACCOUNT='TO_ACCOUNT_UUID'

curl -s -X POST http://localhost:8080/api/v1/transfers \
  -H "Authorization: Bearer $TOKEN_A" \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: tx-demo-001' \
  -d '{"fromAccountId":"'"$A_ACCOUNT"'","toAccountId":"'"$B_ACCOUNT"'","amountPaise":12500,"narrative":"Dinner"}'
```

### 5) Read metrics
```bash
curl -s http://localhost:8080/api/v1/metrics/overview
```

## Current Functional Coverage
- Auth: signup/login/admin helper JWT issuance.
- Accounts: create/list/freeze account metadata.
- Transfers: idempotent transfer initiation + async completion/failure updates.
- Ledger: idempotent double-entry posting and balance lookup.
- Statements: last 200 statement lines (MVP projection quality).
- Analytics: aggregate counters from transfer/ledger streams.
- Audit: event persistence and recent records API.

## Pending / Planned Functionality
The following capabilities are still pending across services:

### Core business features
- Proper statement projection from **entry-level ledger events** (accurate debit/credit lines per account, not placeholder mapping).
- Account ownership validation enforcement across transfer and statement access flows.
- Admin operations for ledger/account lifecycle (adjustments, freeze/unfreeze workflows with full audit emission).

### Security and governance
- Admin-only authorization controls on audit endpoints.
- CorrelationId propagation across command/event chain for traceability.

### Analytics and observability
- Windowed TPS and latency analytics.
- Top-account/activity leaderboards.
- Real-time push (WebSocket/gRPC streaming) for dashboards.

### Developer & product experience
- Better seed/bootstrap workflows (remove manual SQL seeding).
- Frontend application (Angular, user + admin views).

## Notes
- Service-level details and endpoints are documented in each service `README.md`.
- This repository currently optimizes for demonstrating architecture and domain flow over production-grade hardening.
