# BaaS Bank Platform (Single currency INR)

This repository is a **mini Banking-as-a-Service** platform built with:
- **Java 21 + Spring Boot**
- **Kafka** (event-driven communication)
- **gRPC** (fast internal calls)
- **PostgreSQL** (separate DB per service)
- **API Gateway** (single entrypoint)

## Services
- `api-gateway` (port 8080) - entrypoint + JWT validation
- `auth-service` (8081) - signup/login and JWT issuing
- `account-service` (8082) - account metadata (ACTIVE/FROZEN)
- `transfer-service` (8083) - user transfers + outbox
- `ledger-service` (gRPC 9090) - double-entry ledger (source of truth)
- `statement-service` (8084) - statements/read model (MVP projection)
- `analytics-service` (8085) - live Kafka counters
- `audit-service` (8086) - audit log consumer

## Run everything (Docker)

```bash
docker compose up --build
```

## Layman flow (How money moves)
1) User **signs up** and **logs in** → gets a token.
2) User **creates an account**.
3) User sends money using **Transfer** API.
4) Transfer service checks balance from Ledger via **gRPC**.
5) Transfer service saves transfer + writes an **Outbox** event.
6) Outbox publishes a command to Kafka topic `transfer.commands.v1`.
7) Ledger consumes it and posts **DEBIT** (sender) + **CREDIT** (receiver).
8) Ledger publishes `ledger.events.v1` for other services.
9) Analytics counts messages live; Audit stores audit events (future actions).

> Currency is INR only. Amounts are stored in **paise** (₹1 = 100 paise).

## Quick test using curl

### 1) Signup (User A)
```bash
curl -s -X POST http://localhost:8080/api/v1/auth/signup   -H 'Content-Type: application/json'   -d '{"email":"a@test.com","password":"Pass@123"}'
```

### 2) Signup (User B)
```bash
curl -s -X POST http://localhost:8080/api/v1/auth/signup   -H 'Content-Type: application/json'   -d '{"email":"b@test.com","password":"Pass@123"}'
```

Copy the `accessToken` from the response.

### 3) Create accounts
```bash
export TOKEN_A='PASTE_USER_A_TOKEN'
export TOKEN_B='PASTE_USER_B_TOKEN'

curl -s -X POST http://localhost:8080/api/v1/accounts   -H "Authorization: Bearer $TOKEN_A"

curl -s -X POST http://localhost:8080/api/v1/accounts   -H "Authorization: Bearer $TOKEN_B"
```

### 4) Seed money (temporary approach)
In this first cut, seed by calling ledger gRPC `PostAdjustment` or insert a CREDIT directly.
SQL quick seed (credit A account):
```bash
docker exec -it bank-postgres psql -U bank -d ledgerdb   -c "INSERT INTO ledger_account(account_id,status) VALUES ('ACCOUNT_UUID_FROM_STEP3','ACTIVE') ON CONFLICT DO NOTHING;"   -c "INSERT INTO balance_snapshot(account_id,available_paise,current_paise) VALUES ('ACCOUNT_UUID_FROM_STEP3',500000,500000) ON CONFLICT (account_id) DO UPDATE SET available_paise=500000,current_paise=500000;"
```

### 5) Create transfer A → B
```bash
export A_ACCOUNT='FROM_ACCOUNT_UUID'
export B_ACCOUNT='TO_ACCOUNT_UUID'

curl -s -X POST http://localhost:8080/api/v1/transfers   -H "Authorization: Bearer $TOKEN_A"   -H 'Content-Type: application/json'   -H 'Idempotency-Key: tx-demo-001'   -d '{"fromAccountId":"'$A_ACCOUNT'","toAccountId":"'$B_ACCOUNT'","amountPaise":12500,"narrative":"Dinner"}'
```

### 6) View metrics
```bash
curl -s http://localhost:8080/api/v1/metrics/overview
```

## What will be improved next
- Proper statement projection (per account debit/credit lines)
- Ownership validation via account-service
- Admin APIs: adjustments, freeze/unfreeze with audit events
- Angular front end (User + Admin)

