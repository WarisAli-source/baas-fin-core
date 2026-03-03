# account-service

Manages bank account **metadata** (ACTIVE/FROZEN/CLOSED). Balances are NOT stored here.

## Endpoints
- `POST /api/v1/accounts` (create an account for the logged-in user)
- `GET /api/v1/accounts` (list my accounts)
- `POST /api/v1/accounts/{id}/freeze` (admin; simplified)

## Auth
All endpoints require JWT (validated at API Gateway and again here).
