# VidyaSetu

VidyaSetu is a B2B SaaS platform for managing schools and coaching institutes — built for Tier 2/3 institutions in India that currently run on registers and spreadsheets. It covers the day-to-day operational workflow of a school: students, classes, attendance, fees, exams, staff, and academic years, all under one multi-tenant roof.

Each school that signs up is a tenant, isolated from every other school's data at the database level. A school owner can add staff, configure an academic year and classes, enroll students, and run the entire term — attendance, fee collection, exams — without leaving the portal.

## Features

- **Students** — admission records, profiles, CSV bulk import/export
- **Academic years & classes** — class/section setup, promotions between years
- **Attendance** — daily marking, summaries, working-day calculations (holidays excluded)
- **Fees** — fee structures, concessions, payment collection, receipts, defaulter tracking
- **Exams** — exam setup, subjects, marks entry and reporting
- **Staff** — staff records and role management
- **Audit log** — tracks who changed what, visible to the school owner
- **In-app notifications** — bell icon with real-time updates
- **Reports** — dashboard stats and operational reports
- **PWA** — installable on mobile/desktop, with offline and install banners

### Gated / Coming Soon

These are scaffolded end-to-end (entities, services, controllers, frontend) but disabled until live credentials are supplied — the product shows them as "Coming Soon" rather than faking the integration:

- **WhatsApp messaging** (Business API) — needs `WHATSAPP_*` env vars
- **SMS** (Fast2SMS) — needs `FAST2SMS_API_KEY`
- **Billing / subscriptions** (Razorpay) — needs `RAZORPAY_*` env vars

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.3, Spring Security (JWT in HTTP-only cookies), Spring Data JPA, PostgreSQL, Flyway
- **Frontend**: React, TypeScript, Vite, Tailwind CSS, TanStack Query, React Router

## Project Structure

```
VidyaSetu/
├── backend/                 Spring Boot API (port 8081)
│   ├── src/main/java/in/vidyasetu/
│   │   ├── controller/      REST endpoints
│   │   ├── service/         Business logic
│   │   ├── entity/          JPA entities
│   │   ├── repository/      Spring Data repositories
│   │   ├── security/        JWT auth, filters
│   │   └── integration/     WhatsApp / Fast2SMS / Razorpay clients (gated)
│   └── src/main/resources/db/migration/   Flyway SQL migrations
└── frontend/                React + Vite app (port 5173)
    └── src/
        ├── pages/           Route-level views
        ├── components/      Reusable UI (layout, shared, ui primitives)
        ├── hooks/           TanStack Query hooks per domain
        └── services/        API client functions
```

## Getting Started

### Prerequisites

- Java 21+, Maven
- Node.js 18+
- Docker (for local PostgreSQL)

### 1. Database

```bash
cd backend
docker compose up -d postgres
```

### 2. Backend

```bash
cd backend
cp .env.example .env   # fill in JWT_ACCESS_SECRET / JWT_REFRESH_SECRET at minimum
mvn spring-boot:run
```

API runs on `http://localhost:8081`. Flyway migrations apply automatically on startup.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

App runs on `http://localhost:5173` and proxies `/v1/*` API calls to the backend.

## Environment Variables

See [`backend/.env.example`](backend/.env.example) for the full list. Nothing is hardcoded — WhatsApp, SMS, and Razorpay integrations stay in "Coming Soon" mode until their respective env vars are set.

## API Conventions

- All responses are wrapped in a shared `ApiResponse` envelope (`{ success, data, error }`).
- Auth uses short-lived access tokens + long-lived refresh tokens, both in HTTP-only cookies.
- A 401 on any request triggers the frontend's auth entry point, which redirects to login.

---

## License

Licensed under the [GNU Affero General Public License v3.0 (AGPL-3.0)](LICENSE).
