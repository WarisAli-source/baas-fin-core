# statement-service

Builds **query/read models** for statements and admin search.

## MVP
- Provides endpoint to fetch last 200 statement lines for an account.
- Consumes `ledger.events.v1` (placeholder projection in this first cut).

## Next iteration (planned)
- Consume ledger **entry-level** events and build proper DEBIT/CREDIT lines.
- Add ownership checks by integrating with account-service.
