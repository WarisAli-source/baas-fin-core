# api-gateway

Single entry point for the platform.

## Responsibilities
- Route requests to services
- Validate JWT (except `/api/v1/auth/**`)

## Run
Use docker compose from repo root.

## Notes
- Uses Spring Cloud Gateway (WebFlux).
- JWT secret is shared with auth-service via `JWT_SECRET`.
