# VidyaSetu

VidyaSetu is a B2B SaaS platform for managing schools and coaching institutes — built for Tier 2/3 institutions in India that currently run on registers and spreadsheets. It covers the day-to-day operational workflow of a school: students, classes, attendance, fees, exams, staff, and academic years, all under one multi-tenant roof.

## Features

- **Students** — admission records, profiles, CSV bulk import/export
- **Academic years & classes** — class/section setup, promotions between years
- **Attendance** — daily marking, summaries, working-day calculations
- **Fees** — fee structures, concessions, payment collection, receipts, defaulter tracking
- **Exams** — exam setup, subjects, marks entry and reporting
- **Staff** — staff records and role management
- **Audit log** — tracks who changed what, for the school owner
- **In-app notifications** — bell icon with real-time updates
- **Reports** — dashboard stats and operational reports

### Gated / Coming Soon

These are scaffolded end-to-end (entities, services, controllers, frontend) but disabled until live credentials are supplied:

- **WhatsApp messaging** (Business API) — needs `WHATSAPP_*` env vars
- **SMS** (Fast2SMS) — needs `FAST2SMS_API_KEY`
- **Billing / subscriptions** (Razorpay) — needs `RAZORPAY_*` env vars

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.3, Spring Security (JWT), Spring Data JPA, PostgreSQL, Flyway
- **Frontend**: React, TypeScript, Vite, Tailwind CSS, TanStack Query, React Router

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
