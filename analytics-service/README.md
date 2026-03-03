# analytics-service

Live Kafka counters for demo analytics.

## What it does
- Consumes `transfer.commands.v1` and `ledger.events.v1`
- Updates counters in SQL
- Exposes `GET /api/v1/metrics/overview`

## Next iteration
- Windowed TPS, latency, top accounts
- WebSocket/gRPC streaming to Angular dashboard
