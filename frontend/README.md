# Frontend - BaaS Fin Core Console

React + Vite banking UI with user and admin experiences.

## Features
- Authentication (login/signup)
- User dashboard (current balance, account states)
- Transfer form with idempotency key support
- Past transactions (statement lookup)
- Admin analytics (metrics + audit trail)

## Commands
```bash
npm install
npm run dev
npm run build
```

If your backend gateway is not on `http://localhost:8080`, set:

```bash
VITE_API_BASE_URL=http://localhost:8080 npm run dev
```
