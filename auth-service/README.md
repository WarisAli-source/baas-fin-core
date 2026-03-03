# auth-service

Provides **signup/login** and issues **JWT** tokens.

## Endpoints
- `POST /api/v1/auth/signup`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/admin` (DEV helper to create an admin)

## Run
- Via docker compose (recommended): see root README.

## Notes
- Passwords stored as BCrypt hashes.
- JWT is HMAC signed using `JWT_SECRET`.
