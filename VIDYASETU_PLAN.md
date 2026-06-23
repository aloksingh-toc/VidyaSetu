# 🏫 VIDYASETU — Master Application Plan v2.0
> **"Aapke school ka digital saathi"**
> School & Coaching Center Management SaaS Platform for Small Institutions in India
> *Document Version: 2.0 | Last Updated: June 2026*

---

## 📋 TABLE OF CONTENTS

1. [Product Overview](#1-product-overview)
2. [Vision, Mission & Core Values](#2-vision-mission--core-values)
3. [Target Market & ICP](#3-target-market--icp)
4. [Competitor Analysis & SWOT](#4-competitor-analysis--swot)
5. [Core Features & Modules](#5-core-features--modules)
6. [Feature Roadmap & Release Plan](#6-feature-roadmap--release-plan)
7. [Pricing Architecture & Policies](#7-pricing-architecture--policies)
8. [Revenue Model & Financial Projections](#8-revenue-model--financial-projections)
9. [Technical Architecture](#9-technical-architecture)
10. [Database Schema (Complete)](#10-database-schema-complete)
11. [API Design (Complete)](#11-api-design-complete)
12. [Frontend Design & UX](#12-frontend-design--ux)
13. [Academic Year & Calendar Management](#13-academic-year--calendar-management)
14. [Notification & Scheduler System](#14-notification--scheduler-system)
15. [Data Import & Export System](#15-data-import--export-system)
16. [Subscription & Billing Management](#16-subscription--billing-management)
17. [School Settings & Configuration](#17-school-settings--configuration)
18. [Testing Strategy](#18-testing-strategy)
19. [Build Timeline (Phase-by-Phase)](#19-build-timeline-phase-by-phase)
20. [Development Environment Setup](#20-development-environment-setup)
21. [Deployment & Infrastructure](#21-deployment--infrastructure)
22. [Marketing & Go-To-Market Strategy](#22-marketing--go-to-market-strategy)
23. [Non-Functional Requirements](#23-non-functional-requirements)
24. [Legal & Compliance](#24-legal--compliance)
25. [Risk Register](#25-risk-register)
26. [Support & Operations](#26-support--operations)
27. [Glossary](#27-glossary)

---

## 1. PRODUCT OVERVIEW

**Product Name:** Vidyasetu
**Tagline:** "Aapke school ka digital saathi" (Your institution's digital companion)
**Type:** B2B SaaS (Software as a Service)
**Category:** EdTech / School & Coaching Center Management
**Primary Users:** Owners/principals of small private schools and coaching centers
**Data Policy:** Your data belongs to you. Export everything, anytime, no questions asked.

### What is Vidyasetu?

Vidyasetu is a Hindi-English bilingual, mobile-first institution management platform that helps small private schools and coaching centers (50–500 students) digitize their operations — starting with fee collection, attendance, and parent communication — via WhatsApp-first workflows that require zero technical knowledge.

### The Problem Being Solved

Small private schools and coaching centers in India (especially Tier 2/3 cities and towns) still run entirely on:
- Paper registers for attendance and fees
- Manual WhatsApp messages typed one-by-one to each parent
- No record of who paid, who defaulted, or how much was collected monthly
- No digital receipts or reports — just handwritten notes
- No visibility into financial health at the end of each month

School owners lose money to fee defaulters, waste hours on manual communication, and have no system for year-end student promotions, exam results, or institutional reporting. Vidyasetu solves all of this in one affordable, simple platform.

### Institution Type Support

Vidyasetu serves two parallel institution types using the same codebase — one `institution_type` flag in settings changes the label language:

| Label | School Mode | Coaching Center Mode |
|-------|-------------|---------------------|
| Classes | Classes | Batches |
| Subjects | Subjects | Courses |
| Academic Year | Academic Year | Session |
| Principal | Principal | Director |

This makes Vidyasetu usable for both school and coaching center owners with zero UI confusion.

---

## 2. VISION, MISSION & CORE VALUES

### Vision
To become the go-to digital backbone for every small private school and coaching center in India — starting from Ballia, UP and expanding across all Tier 2/3 cities.

### Mission
Make institution management so simple that a school owner with a basic Android phone and no tech background can run their entire school digitally within one week of signing up.

### Core Values
- **Simplicity first** — if a feature needs training, it's too complex. Redesign it.
- **Hindi-first** — language should never be a barrier to digital adoption
- **Mobile-first** — built for cheap Android phones (2GB RAM, 4G internet)
- **Trust through transparency** — parents get instant receipts and real-time updates
- **Data ownership** — every school owns its data and can export it at any time
- **WhatsApp-native** — meet users where they already live, not in a new app

---

## 3. TARGET MARKET & ICP

### Primary Segments

**Segment A: Small Private Schools**
- School type: Unaided private schools (self-financed, no government aid)
- Size: 50–500 students
- Location: Tier 2 and Tier 3 cities, towns, semi-urban areas
- Starting geography: Ballia, Uttar Pradesh
- Language: Hindi primary, English secondary

**Segment B: Coaching Centers (Added)**
- Type: Private tuition centers, competitive exam coaching, subject-specific centers
- Size: 50–300 students
- Same geography and language profile
- Same pain points: fee defaulters, attendance, parent communication
- Note: 2–3x more coaching centers than schools in UP/Bihar

### Market Data

| Metric | Number |
|--------|--------|
| Total private schools in India | ~5 lakh |
| Small private schools (<500 students) | ~3.5 lakh |
| Coaching/tuition centers in India | ~10 lakh (est.) |
| Schools + coaching centers with zero digital tools | ~85% |
| Combined addressable market (TAM) | ₹6,000+ crore/year |
| Serviceable market starting Ballia | ~800 schools + ~1,500 coaching centers within 50km radius |

### Ideal Customer Profile (ICP)

**For Schools:**
- Owner/principal aged 35–60
- Runs a school with 80–300 students
- Has a smartphone (Android, basic model — Redmi, Realme)
- Uses WhatsApp daily to message parents individually
- Currently manages fees via paper register or basic Excel
- Pain points: fee defaulters, month-end accounting, parent communication
- Budget: ₹300–1,000/month (same cost as buying registers for one year)

**For Coaching Centers:**
- Owner/director aged 25–50 (often younger, more tech-comfortable)
- Runs a center with 60–250 students in 5–15 batches
- Same WhatsApp and phone profile
- Additional pain point: batch scheduling, multiple fee installments
- Budget: ₹300–800/month

---

## 4. COMPETITOR ANALYSIS & SWOT

### Full Competitor Landscape

| Competitor | Pricing | Strength | Weakness vs Vidyasetu |
|------------|---------|----------|----------------------|
| SchoolEasy | ₹2,000+/month | Full-featured | Too expensive, complex UI, English-only |
| MySchoolPage | ₹1,500+/month | Parent mobile app | English-only, requires significant setup |
| Teachmint | Free (limited) | VC-backed, big brand | Overkill, English UI, app-heavy |
| EduSys | ₹3,000+/month | Enterprise grade | Not built for small schools |
| ClassPlus | ₹2,000+/month | VC-backed, coaching-focused | Complex, requires parent app install, English-first |
| Fedena | Open source/free | Free | Requires self-hosting, technical setup, no support |
| AMS | ₹1,000+/month | Coaching center focus | English-only, no WhatsApp native |
| Local Excel + WhatsApp | Free | Familiar, zero learning curve | Fully manual, no automation, no receipts |

### Vidyasetu's Competitive Advantages
1. **Hindi-first UI** — the only platform in the ₹499 segment with full Hindi support
2. **WhatsApp-native** — fee reminders and receipts go directly on WhatsApp without a separate app
3. **Price** — ₹499/month vs ₹2,000–3,000/month for comparable tools
4. **Serves both schools and coaching centers** — double the addressable market
5. **Built for low-end Android** — optimized for cheap phones with slow internet
6. **Local trust (Ballia)** — personal community connections give unfair early advantage
7. **No app install for parents** — parents receive everything on WhatsApp they already use

### Counter-Positioning Against ClassPlus (Direct Threat)
ClassPlus requires parents to install an app, costs ₹2,000+/month, and has a steep setup curve. Vidyasetu wins on three points: "No app for parents, half the price, works in Hindi."

### SWOT Analysis

| | Positive | Negative |
|---|---|---|
| **Internal** | **Strengths:** Price, Hindi-first, WhatsApp-native, dual segment (school + coaching), local trust, proven tech stack (Java/Spring Boot/React) | **Weaknesses:** Solo founder, no brand recognition, no capital buffer, limited marketing budget, learning-while-building |
| **External** | **Opportunities:** 3+ lakh unserved small schools, 10 lakh coaching centers, no dominant Hindi player in ₹499 segment, WhatsApp Business API improving | **Threats:** ClassPlus drops price, Teachmint adds Hindi, Google/Meta enter Indian edtech, WhatsApp policy change affecting messaging |

---

## 5. CORE FEATURES & MODULES

### Module 1: Student Management
- Add, edit, soft-delete student profiles
- Student photo upload (stored in Cloudflare R2)
- Class/batch and section assignment
- Parent/guardian contact: name, mobile number, WhatsApp number (can differ)
- Admission date, roll number, date of birth, gender
- Search by name, class, roll number — full-text search
- View individual student profile: tabs for fees, attendance, marks, communication history
- Bulk CSV import of students (from Excel/Google Sheets)
- Export student list to PDF or Excel

### Module 2: Fee Management (MVP Core)
- Define fee types: Tuition, Transport, Uniform, Exam, Library, etc.
- Set fee amounts per class/batch with frequency: Monthly, Quarterly, Annual, One-time
- Set due dates per fee type (e.g., fees due on 10th of each month)
- Record payments: Cash, UPI, Cheque, Online
- Partial payment support with balance tracking
- **Fee concession management**: fixed amount, percentage, or full waiver per student per fee type
- Concession-aware defaulter calculation (concession students never appear as defaulters)
- Payment voiding: ROLE_OWNER can void incorrect payments with reason
- Auto-generate receipt number (SCHOOL_CODE-YEAR-SEQUENCE)
- Fee receipt PDF generation (auto-numbered, includes school logo)
- Fee defaulter list: who hasn't paid, how much pending, how many days overdue
- Carry-forward pending fees between academic years
- Monthly and annual collection summary

### Module 3: Attendance Management
- Teacher marks daily attendance per class: Present, Absent, Late, On-Leave
- Mobile-optimized one-tap interface
- Attendance calculated against working days (excludes holidays and weekly offs)
- View attendance history per student
- Monthly attendance summary per student (present/total working days)
- Consecutive absence alert (3+ days absent → alert owner)
- Automatic absent notification to parent via WhatsApp

### Module 4: Academic Year & Calendar Management
- Create and manage academic years (e.g., 2025-2026: April 1 – March 31)
- Set current active academic year
- Define weekly off days (Sunday / Saturday+Sunday / custom)
- Holiday calendar: mark school holidays with name and type
- Pre-load public holidays (Republic Day, Independence Day, Gandhi Jayanti, Diwali, Holi, etc.)
- Year-end student promotion: bulk promote all students to next class
- Handle detained students (failed year — stay in same class)
- Handle passed-out students (Class 12 graduates — marked as alumni)
- Archive old year data (becomes read-only, still viewable)

### Module 5: Parent Communication (WhatsApp-First)
- Fee reminder: "Dear Parent, ₹X fee is pending. Please pay by [date]."
- Fee receipt: auto-sent on every payment recording
- Absent notification: auto-sent same day attendance is marked
- Broadcast: school-wide or class-wise announcements
- Parent opt-out: reply STOP to stop all automated messages
- SMS fallback for all WhatsApp messages (Fast2SMS)
- Message delivery logs: sent, delivered, failed per message
- Message throttling: max 3 reminders per student per month

### Module 6: Staff & Teacher Management
- Add teacher profiles: name, subject(s), phone, email
- Role assignment: ROLE_TEACHER (attendance + marks) or ROLE_ADMIN (fee collection + reports)
- Assign teachers to specific classes/batches
- Teacher attendance tracking (separate from student attendance)
- Teacher payroll tracking (Standard plan and above)

### Module 7: Exam & Report Cards (Standard Plan)
- Create exam structure: name, type (Unit Test / Half Yearly / Annual), dates
- Define subjects and max marks per class per exam
- Bulk marks entry by teacher (one screen for entire class)
- Auto-calculate: total marks, percentage, grade (A+/A/B/C/D/F), pass/fail
- Configurable grading scale per school
- Generate report card PDF per student (school branding)
- Publish results: lock marks after publishing (no further edits)
- Send report card PDF to parent via WhatsApp

### Module 8: Expense Tracking (Standard Plan)
- Record school expenses by category: Salary, Utilities, Maintenance, Supplies, Other
- Monthly income vs expense view
- Categorized expense report (month/year filter)
- Simple P&L summary: total fees collected - total expenses = net position

### Module 9: Reports & Analytics
- Daily fee collection report (by payment method: cash/UPI/cheque breakdown)
- Monthly fee collection report (class-wise, student-wise)
- Fee defaulter report with filter by class and days-overdue
- Attendance report per student per month
- Class-wise attendance summary
- Year-end financial summary
- Expense report
- All reports: viewable in-app + exportable as PDF
- Dashboard charts: fee collection trend (6-month bar chart), attendance trend

### Module 10: Owner Dashboard
- Total enrolled students (active count)
- This month's fee collection (₹ amount, vs last month)
- Total pending fees (₹ amount, days breakdown)
- Today's attendance % (vs yesterday)
- Recent payments (last 10 transactions)
- In-app alerts: defaulters over 30 days, plan expiry warning, students near limit
- Quick action buttons: Record Payment, Mark Attendance, Send Reminder

### Module 11: In-App Notification Center
- Bell icon with unread count in navigation
- Notification types:
  - "15 students have fee pending for 30+ days"
  - "Attendance not marked today for Class 5"
  - "WhatsApp message failed for 3 parents — check numbers"
  - "Plan expires in 7 days — upgrade to continue"
  - "New academic year setup required"
- Mark all as read / clear notifications

### Module 12: Audit Log (Financial Security)
- Immutable record of all critical actions
- Viewable by ROLE_OWNER only
- Captures: who did what, when, from which device (IP)
- Logged events: fee collected, fee voided, fee structure changed, student added/deleted, user login/logout, plan changed
- Filter by date, user, action type
- Cannot be edited or deleted — ever

### Module 13: Data Import & Export
- CSV template download for student import
- CSV/Excel bulk student import with field mapping and validation report
- Historical fee payment import (for schools switching from Excel)
- Export: students (CSV), fee payments (CSV), full school data (ZIP)
- Data portability guarantee: export everything in 2 clicks

### Module 14: Subscription & Billing
- Razorpay payment gateway integration
- Auto-renewal via Razorpay subscriptions
- GST invoice auto-generation (18% GST, HSN 998314)
- Invoice sent to school owner via WhatsApp and email after payment
- Plan upgrade: immediate, pro-rated charge
- Plan downgrade: effective next billing cycle
- Payment failure: retry 3 times over 3 days, then notify
- 7-day grace period after expiry before feature restriction
- Data retained 90 days after cancellation (exportable)

---

## 6. FEATURE ROADMAP & RELEASE PLAN

### Dependency Tree (Build in This Order)
```
academic_years → holidays → classes → students → fee_types
→ fee_structures → fee_concessions → fee_payments
→ attendance (depends on holidays for working day calc)
→ exam_subjects → exam_marks
→ message_logs (depends on fee_payments + attendance)
```

### v1.0 — MVP Launch (Weeks 1–16)
> Goal: First paying school within 30 days of launch

| Feature | Plan | Priority |
|---------|------|----------|
| Academic year setup | All paid | P0 |
| Student add/edit/search + CSV import | All paid | P0 |
| Fee types + fee structure per class | All paid | P0 |
| Fee concession management | All paid | P0 |
| Record fee payment (cash/UPI/cheque) | All paid | P0 |
| Payment voiding (ROLE_OWNER) | All paid | P0 |
| Fee defaulter list (concession-aware) | All paid | P0 |
| Fee receipt PDF | All paid | P0 |
| WhatsApp fee reminder (manual trigger) | All paid | P0 |
| Owner dashboard | All paid | P0 |
| Holiday calendar | All paid | P0 |
| Audit log viewer | All paid | P0 |
| In-app notification center | All paid | P1 |
| Parent broadcast message | Basic+ | P1 |
| Monthly fee collection report PDF | All paid | P1 |
| SMS fallback for WhatsApp | All paid | P1 |

### v1.1 — Attendance & Teachers (Weeks 17–22)
> Goal: Daily active usage — teachers use it every morning

| Feature | Plan | Priority |
|---------|------|----------|
| Teacher accounts + RBAC | Basic+ | P0 |
| Daily attendance marking (mobile UI) | Basic+ | P0 |
| Absent parent notification (auto WhatsApp) | Basic+ | P0 |
| Working day calendar (holiday-aware %) | Basic+ | P0 |
| Monthly attendance report per student | Basic+ | P0 |
| Staff attendance tracking | Standard+ | P1 |

### v1.2 — Reports, Exams & Year-End (Weeks 23–28)
> Goal: End-of-year functionality — schools renew because they need year-end features

| Feature | Plan | Priority |
|---------|------|----------|
| Exam creation + subject structure | Standard+ | P0 |
| Bulk marks entry by teacher | Standard+ | P0 |
| Report card PDF generation | Standard+ | P0 |
| Year-end student promotion workflow | All paid | P0 |
| Expense tracking | Standard+ | P1 |
| Timetable / class schedule | Standard+ | P2 |
| Student ID card generator | Basic+ | P2 |

### v1.3 — Add-ons & Billing (Weeks 29–32)
> Goal: Revenue expansion — same schools pay more

| Feature | Plan | Priority |
|---------|------|----------|
| Full subscription + Razorpay billing | All | P0 |
| GST invoice generation | All | P0 |
| Online fee collection (UPI link per student) | Add-on ₹299 | P0 |
| Parent portal (web) | Add-on ₹499 | P1 |
| Teacher payroll | Add-on ₹399 | P1 |
| Custom report cards (school branding) | Add-on ₹299 | P2 |

### v2.0 — Scale (Post Month 12)
| Feature | Plan | Priority |
|---------|------|----------|
| Transport route management | Add-on ₹399 | P1 |
| Library management | Add-on ₹199 | P2 |
| School website builder | Add-on ₹199 | P2 |
| Multi-school trust management | Trust ₹5,000–8,000 | P1 |
| Offline attendance (IndexedDB sync) | All | P1 |
| Android PWA installable app | All | P1 |

### Parent Portal Mini-Spec
Login: OTP to registered mobile number (no password required)
Can view: fee dues, payment history with receipts, attendance summary, exam marks
Can do: pay fees online (if online collection add-on is enabled by school)
Can receive: push notifications (web push, in addition to WhatsApp)
Cannot: change any data (100% read-only for parents)
Access: web browser on phone (no separate app install required)

---

## 7. PRICING ARCHITECTURE & POLICIES

### Plan Tiers

| Plan | Monthly | Annual | Students | Key Features | Target |
|------|---------|--------|----------|--------------|--------|
| **Free** | ₹0 | ₹0 | 30 | Fee tracking only, no WhatsApp | Trial |
| **Starter** | ₹299 | ₹2,990 | 75 | Layer v1.0 features | Tiny schools/coaching |
| **Basic** | ₹499 | ₹4,990 | 150 | v1.0 + attendance + reports | **Ballia sweet spot** |
| **Standard** | ₹899 | ₹8,990 | 350 | Basic + exams + expense + payroll | Growing schools |
| **Pro** | ₹1,699 | ₹16,990 | Unlimited | All features except enterprise | Large schools |
| **Complete** | ₹2,999 | ₹29,990 | Unlimited | Everything included | Premium schools |
| **Trust/Chain** | ₹5,000–8,000 | Custom | Multi-school | Custom build-out | Education trusts |

Annual plan = 10 months price (2 months free = 16.7% discount).

### Overage Policy (Student Limit)
When a school reaches their plan limit:
- **0–10 students over limit:** Grace period — warning shown, can still add students
- **10+ students over limit:** Mandatory upgrade prompt — cannot add more students until upgrade
- No auto-charge — always requires owner confirmation before plan change

### Plan Policies
- **Upgrade:** Immediate. Pro-rated charge for remaining days in current month.
- **Downgrade:** Effective at next billing cycle. Current features continue until cycle end.
- **Cancellation:** Effective at next billing cycle. Data retained 90 days post-cancellation (exportable).
- **Annual refund:** Pro-rated refund within 30 days of annual payment. No refund after 30 days.
- **Grace period:** 7 days after plan expiry before features are restricted. WhatsApp notification sent.
- **Pricing floor:** Never below ₹249/month. Below that, server costs are not recoverable.

### GST Pricing (India)
All prices above are GST-exclusive. 18% GST added at checkout.
- Basic ₹499 + ₹89.82 GST = ₹588.82 billed total
- GST invoice automatically generated and sent after every payment
- HSN code for SaaS: 998314

---

## 8. REVENUE MODEL & FINANCIAL PROJECTIONS

### Revenue Streams
1. **Monthly subscriptions** — primary, predictable
2. **Annual plan payments** — cash flow boost (2 months free = collected upfront)
3. **Add-on modules** — revenue expansion without new customer acquisition
4. **One-time setup fee** — optional ₹999 for white-glove onboarding
5. **Transaction revenue** — 0.5–1% of fees collected via online payment add-on
6. **GST collected** — passed through to government (not revenue)

### CAC & LTV Analysis (New)

| Channel | CAC | Method |
|---------|-----|--------|
| Referral (school → school) | ₹200 | ₹200 commission per conversion |
| Newspaper ad (Ballia) | ₹1,000 | ₹5,000 ad spend / 5 signups avg |
| Direct sales (local contact) | ₹1,500 | ₹500 visit cost, 1 in 3 converts |
| WhatsApp word-of-mouth | ₹50 | Organic, nearly free |
| **Target blended CAC** | **< ₹800** | Mix of channels |

**LTV calculation:**
- Average plan: ₹499/month (Basic)
- Average retention: 24 months
- Average add-on uptake: ₹300/month additional by month 12
- LTV = (₹499 × 12) + (₹799 × 12) = ₹5,988 + ₹9,588 = **₹15,576**
- LTV:CAC ratio = 15,576 / 800 = **~19x** (healthy SaaS = anything above 3x)

### Add-on Revenue Math (100 schools on ₹499 Basic)

| Source | Calculation | Revenue |
|--------|-------------|---------|
| Base subscriptions | 100 × ₹499 | ₹49,900 |
| Online fee collection add-on | 40 × ₹299 | ₹11,960 |
| Custom report cards add-on | 30 × ₹299 | ₹8,970 |
| Transport management add-on | 20 × ₹399 | ₹7,980 |
| Parent portal add-on | 15 × ₹499 | ₹7,485 |
| Teacher payroll add-on | 25 × ₹399 | ₹9,975 |
| **Total MRR** | | **₹96,270** |

Effective ARPU with add-ons: ₹963/school vs ₹499 base — nearly double.

### 3-Year Revenue Projections

**Annual Churn Rate Assumption: 15%** (1 in 7 schools leave per year)
**Net MRR Growth = New MRR − Churned MRR + Expansion MRR**

#### Year 1

| Scenario | Schools (Month 12) | MRR | ARR |
|----------|-------------------|-----|-----|
| Worst case | 20 | ₹9,980 | ₹1.2 lakh |
| Realistic | 60 | ₹29,940 | ₹3.6 lakh |
| Best case | 120 | ₹59,880 | ₹7.2 lakh |

#### Year 2

| Scenario | Schools | MRR (with add-ons) | ARR |
|----------|---------|-------------------|-----|
| Worst case | 80 | ₹55,000 | ₹6.6 lakh |
| Realistic | 250 | ₹1,75,000 | ₹21 lakh |
| Best case | 500 | ₹3,50,000 | ₹42 lakh |

#### Year 3

| Scenario | Schools | MRR | ARR |
|----------|---------|-----|-----|
| Realistic | 1,000 | ₹7,00,000 | ₹84 lakh |
| Best case | 2,500 | ₹17,50,000 | ₹2.1 crore |

### Operating Costs (Monthly)

| Cost Item | Monthly |
|-----------|---------|
| Hosting (Railway backend + Neon PostgreSQL) | ₹2,500–4,000 |
| Vercel (frontend hosting) | ₹0 (free tier) |
| WhatsApp Business API (Meta) | ₹0.25–0.50/msg (~₹2,500 for 100 schools) |
| SMS fallback (Fast2SMS) | ₹500–1,000 |
| Cloudflare R2 (file storage) | ₹200–500 |
| Domain (vidyasetu.in) | ₹100 (amortized) |
| Upstash Redis (caching) | ₹0–500 |
| Sentry (error tracking) | ₹0 (free tier) |
| **Total fixed monthly costs** | **₹5,800–8,600** |

### Break-Even Analysis (Updated for GST)
- Actual revenue per school (₹499 GST-inclusive): ₹499 / 1.18 = ₹423/school
- Fixed monthly costs: ~₹7,000
- Break-even: **17 paying schools**

### Upsell Journey (One School, 20 Months)

| Month | Event | Monthly Revenue |
|-------|-------|----------------|
| 1–3 | Free trial | ₹0 |
| 4 | Converts to Basic | ₹499 |
| 6 | Adds online fee collection | ₹798 |
| 9 | Upgrades to Standard | ₹899 |
| 11 | Adds custom report cards | ₹1,198 |
| 14 | Adds transport management | ₹1,597 |
| 16 | Upgrades to Pro | ₹1,699 |
| 18 | Adds parent portal | ₹2,198 |
| 20 | Upgrades to Complete | ₹2,999 |

### Personal Sustainability Milestones

| Milestone | Schools | MRR | Status |
|-----------|---------|-----|--------|
| Cover all costs | 17 | ₹8,483 | Break-even |
| Cover basic living expenses | 40 | ₹19,960 | Sustainable |
| Full-time viable income | 100 | ₹49,900 | Quit-job level |
| Hire one part-time person | 200 | ₹99,800 | Growth phase |
| Hire one full-time person | 400 | ₹1,99,600 | Scale phase |

---

## 9. TECHNICAL ARCHITECTURE

### Tech Stack (Finalized)

| Layer | Technology | Version | Notes |
|-------|-----------|---------|-------|
| Language | Java | 21 | LTS, virtual threads available |
| Backend framework | Spring Boot | 3.3.x | Latest stable |
| Security | Spring Security + JWT | Latest | HttpOnly cookies, not localStorage |
| ORM | Spring Data JPA + Hibernate | Latest | With MapStruct for DTO mapping |
| Database | PostgreSQL | 16 | Neon (production), local Docker (dev) |
| Caching | Spring Cache + Caffeine | Latest | In-memory; upgrade to Redis (Upstash) at 100+ schools |
| Async jobs | Spring @Async + @Scheduled | Built-in | For PDF generation, WhatsApp, scheduler |
| Rate limiting | Bucket4j | Latest | Per-endpoint rate limiting |
| PDF generation | iText 7 Community Edition | 7.x | Free, programmatic PDF |
| API documentation | SpringDoc OpenAPI 3 | Latest | Swagger UI auto-generated |
| Error tracking | Sentry Java SDK | Latest | Free tier sufficient for v1 |
| Frontend | React.js | 18.x | Vite build tool |
| Styling | Tailwind CSS | 3.x | Utility-first |
| Component library | shadcn/ui | Latest | Radix UI + Tailwind, copy-paste components |
| Form validation | react-hook-form + zod | Latest | Type-safe validation |
| Data tables | @tanstack/react-table | Latest | Sortable, paginated |
| HTTP client | Axios | Latest | With interceptors for auth |
| State management | TanStack Query (React Query) | Latest | Server state, caching |
| WhatsApp | Meta WhatsApp Business Cloud API | v18.0+ | Template messages |
| SMS fallback | Fast2SMS API | Latest | 10 paise/SMS |
| Containerization | Docker + Docker Compose | Latest | Dev environment |
| CI/CD | GitHub Actions | Latest | Build + test on every push |
| Frontend hosting | Vercel | Latest | Free tier |
| Backend hosting | Railway | Latest | Auto-deploy from GitHub |
| Database hosting | Neon PostgreSQL | Latest | Serverless, PITR, Mumbai region |
| File storage | Cloudflare R2 | Latest | PDFs, photos |

### Architecture Diagram (Multi-Tenant, Row-Level Isolation)

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENTS                                   │
│   [Browser/Mobile: React PWA]  [WhatsApp Webhook: Meta]     │
└────────────────────┬────────────────────┬───────────────────┘
                     │ HTTPS              │ HTTPS POST
                     ▼                    ▼
┌─────────────────────────────────────────────────────────────┐
│              SPRING BOOT BACKEND (Railway)                   │
│                                                              │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐   │
│  │ JWT Filter  │  │  Rate Limiter│  │  Audit Interceptor│   │
│  │ (HttpOnly   │  │  (Bucket4j)  │  │  (AOP — logs all  │   │
│  │  cookie)    │  │              │  │   write ops)      │   │
│  └──────┬──────┘  └──────┬───────┘  └─────────┬────────┘   │
│         │                │                      │            │
│  ┌──────▼──────────────────────────────────────▼──────┐     │
│  │              Controllers + Services                  │     │
│  │  AuthController  | StudentService  | FeeService     │     │
│  │  AttendanceCtrl  | ExamService     | ReportService  │     │
│  │  WhatsAppService | SchedulerService| AuditService   │     │
│  └─────────┬──────────────────────────────────────────┘     │
│            │                                                  │
│  ┌─────────▼──────┐    ┌─────────────┐   ┌───────────────┐  │
│  │  Caffeine Cache│    │  @Async Pool │   │  @Scheduled   │  │
│  │  (dashboard    │    │  (PDF gen,   │   │  (monthly fees,│  │
│  │   stats, etc.) │    │   WhatsApp   │   │   reminders)  │  │
│  └────────────────┘    └─────────────┘   └───────────────┘  │
└─────────────────────────┬───────────────────────────────────┘
                           │
        ┌──────────────────┼────────────────────┐
        ▼                  ▼                    ▼
┌──────────────┐  ┌──────────────────┐  ┌──────────────┐
│  PostgreSQL  │  │  Cloudflare R2   │  │  External    │
│  (Neon)      │  │  (PDFs, photos)  │  │  APIs:       │
│              │  │                  │  │  - Meta WA   │
│  Row-level   │  │  Scoped by       │  │  - Fast2SMS  │
│  tenant      │  │  school_id/      │  │  - Razorpay  │
│  isolation   │  │  prefix in path  │  │  - Sentry    │
└──────────────┘  └──────────────────┘  └──────────────┘
```

### Multi-Tenancy Strategy
- **Pattern:** Single database, single schema, row-level isolation
- Every table has a `school_id UUID` column (foreign key to `schools` table)
- Every JWT contains `schoolId` claim (set at login time)
- `TenantContext` ThreadLocal stores `schoolId` for the duration of each request
- All JPA Repository queries automatically include `WHERE school_id = ?` via Spring Data Specifications or `@Query`
- No school can ever access another school's data — guaranteed by application layer + DB layer
- Supports growth to 10,000+ schools without re-architecture

### Security Architecture
- **Authentication:** JWT tokens stored in `HttpOnly`, `SameSite=Strict` cookies
- **Token lifetime:** Access token = 15 minutes; Refresh token = 30 days (in separate HttpOnly cookie)
- **Token rotation:** New access token issued on every refresh
- **RBAC roles:**
  - `ROLE_OWNER` — full access including settings, billing, audit logs, voiding payments
  - `ROLE_ADMIN` — fee collection, reports, student management; no school settings or audit logs
  - `ROLE_TEACHER` — attendance marking and marks entry for their assigned classes only
- **Password hashing:** BCrypt, strength 12
- **Rate limiting (Bucket4j):**
  - `/auth/login` — 5 attempts per 15 minutes per IP
  - `/auth/register` — 3 per hour per IP
  - `/messages/*` — 10 per minute per school
  - `/reports/*` — 5 per minute per school
  - `/import/*` — 2 per hour per school
- **WhatsApp webhook:** Validated via Meta's X-Hub-Signature-256 HMAC header
- **Sensitive data:** `whatsapp_access_token` encrypted at rest using JPA AttributeConverter (AES-256)
- **Input validation:** `@Valid` + Zod (frontend) on all endpoints — phone numbers, amounts, dates
- **HTTPS:** Enforced everywhere; HTTP redirects to HTTPS

### Async Job Architecture
Operations that run asynchronously (never block HTTP request):

| Operation | Method | Pattern |
|-----------|--------|---------|
| Fee receipt PDF generation | `@Async` | Fire-and-forget; receipt URL saved to DB when ready |
| Single WhatsApp message | `@Async` | Immediate, non-blocking |
| Bulk WhatsApp reminders (50+ parents) | `@Async` job queue | Returns `jobId`, client polls `/jobs/{jobId}` |
| Monthly report PDF | `@Async` | Background; notification sent when ready |
| CSV student import | `@Async` | Returns `jobId`; processes in background |
| Attendance absent notifications | `@Async` | Batch-sent after teacher submits |

All external API calls (WhatsApp, SMS, Razorpay) have:
- 5-second connection timeout
- 10-second read timeout
- 3 automatic retries with exponential backoff
- Dead letter logging to `message_logs` with status `FAILED`

### Caching Strategy (Caffeine → Upstash Redis at scale)

| Cache Key | TTL | Invalidated When |
|-----------|-----|-----------------|
| `dashboard:{schoolId}` | 5 minutes | Any payment recorded or student added |
| `defaulters:{schoolId}` | 15 minutes | Any payment recorded or concession changed |
| `plan:{schoolId}` | 1 hour | Subscription updated |
| `holidays:{schoolId}:{year}` | 24 hours | Holiday added/removed |

### Backup & Disaster Recovery
- **Provider:** Neon PostgreSQL (automatic daily backups, PITR for last 7 days)
- **RTO (Recovery Time Objective):** 4 hours (restore from Neon backup)
- **RPO (Recovery Point Objective):** 24 hours (daily backup = max 24h data loss)
- **File storage:** Cloudflare R2 (geo-redundant by default)
- **Restoration test:** Test restoration quarterly on staging environment
- **Monitoring:** UptimeRobot pings production every 5 minutes; Sentry captures all errors

---

## 10. DATABASE SCHEMA (COMPLETE)

### Complete Table List
1. `schools` — one row per institution (tenant)
2. `users` — owners, teachers, admins
3. `academic_years` — session management
4. `classes` — class/batch definitions
5. `students` — student profiles
6. `parents` — guardian contacts linked to students
7. `holidays` — school holiday calendar
8. `fee_types` — fee category definitions
9. `fee_structures` — amount per class per fee type
10. `fee_concessions` — individual student concessions
11. `fee_payments` — actual payment records
12. `attendance` — daily attendance records
13. `message_logs` — WhatsApp/SMS communication log
14. `exams` — exam definitions
15. `exam_subjects` — subject+max marks per exam per class
16. `exam_marks` — individual student marks
17. `expenses` — school expense records
18. `subscriptions` — billing records
19. `student_promotions` — year-end promotion history
20. `audit_logs` — immutable activity trail
21. `notification_jobs` — async job tracking
22. `app_notifications` — in-app notification center records

### Full SQL Schema

```sql
-- ============================================================
-- TABLE 1: SCHOOLS (one row = one paying institution / tenant)
-- ============================================================
CREATE TABLE schools (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                     VARCHAR(255) NOT NULL,
    institution_type         VARCHAR(20) DEFAULT 'SCHOOL',   -- SCHOOL, COACHING_CENTER
    address                  TEXT,
    city                     VARCHAR(100),
    state                    VARCHAR(100) DEFAULT 'Uttar Pradesh',
    phone                    VARCHAR(15),
    email                    VARCHAR(255) UNIQUE,
    logo_url                 VARCHAR(500),
    plan_type                VARCHAR(50) DEFAULT 'FREE',      -- FREE, STARTER, BASIC, STANDARD, PRO, COMPLETE, TRUST
    plan_expires_at          TIMESTAMP,
    weekly_off_days          VARCHAR(30) DEFAULT 'SUNDAY',   -- SUNDAY, SATURDAY_SUNDAY, NONE
    grading_scale            VARCHAR(20) DEFAULT 'STANDARD', -- STANDARD, CGPA, PERCENTAGE_ONLY
    language_preference      VARCHAR(10) DEFAULT 'hi',        -- hi, en
    whatsapp_phone_number_id VARCHAR(100),
    whatsapp_access_token    TEXT,                            -- AES-256 encrypted at rest
    whatsapp_enabled         BOOLEAN DEFAULT FALSE,
    sms_enabled              BOOLEAN DEFAULT TRUE,
    late_fee_enabled         BOOLEAN DEFAULT FALSE,
    late_fee_amount          DECIMAL(10,2),
    fee_due_day              INTEGER DEFAULT 10,              -- day of month fees are due
    support_phone            VARCHAR(15),
    gstin                    VARCHAR(20),                     -- school's GST number if any
    is_active                BOOLEAN DEFAULT TRUE,
    created_at               TIMESTAMP DEFAULT NOW(),
    updated_at               TIMESTAMP DEFAULT NOW()
);

-- ============================================================
-- TABLE 2: USERS
-- ============================================================
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID REFERENCES schools(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(15) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(20) NOT NULL,     -- OWNER, ADMIN, TEACHER
    is_active       BOOLEAN DEFAULT TRUE,
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_users_school_id ON users(school_id);
CREATE INDEX idx_users_phone ON users(phone);

-- ============================================================
-- TABLE 3: ACADEMIC YEARS
-- ============================================================
CREATE TABLE academic_years (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id    UUID REFERENCES schools(id) ON DELETE CASCADE,
    name         VARCHAR(20) NOT NULL,   -- "2025-2026"
    start_date   DATE NOT NULL,          -- 2025-04-01
    end_date     DATE NOT NULL,          -- 2026-03-31
    is_current   BOOLEAN DEFAULT FALSE,
    is_archived  BOOLEAN DEFAULT FALSE,
    created_at   TIMESTAMP DEFAULT NOW(),
    UNIQUE(school_id, name)
);

CREATE INDEX idx_academic_years_school ON academic_years(school_id);

-- ============================================================
-- TABLE 4: CLASSES
-- ============================================================
CREATE TABLE classes (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id           UUID REFERENCES schools(id) ON DELETE CASCADE,
    academic_year_id    UUID REFERENCES academic_years(id),
    name                VARCHAR(100) NOT NULL,  -- "Class 5", "Nursery", "Batch A"
    section             VARCHAR(10),            -- "A", "B", "C"
    class_teacher_id    UUID REFERENCES users(id),
    display_order       INTEGER DEFAULT 0,       -- for sorting in UI
    created_at          TIMESTAMP DEFAULT NOW(),
    UNIQUE(school_id, academic_year_id, name, section)
);

CREATE INDEX idx_classes_school_year ON classes(school_id, academic_year_id);

-- ============================================================
-- TABLE 5: STUDENTS
-- ============================================================
CREATE TABLE students (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID REFERENCES schools(id) ON DELETE CASCADE,
    class_id         UUID REFERENCES classes(id),
    roll_number      VARCHAR(20),
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100),
    date_of_birth    DATE,
    gender           VARCHAR(10),              -- MALE, FEMALE, OTHER
    photo_url        VARCHAR(500),
    admission_date   DATE DEFAULT CURRENT_DATE,
    admission_number VARCHAR(50),
    blood_group      VARCHAR(5),
    address          TEXT,
    is_active        BOOLEAN DEFAULT TRUE,
    created_at       TIMESTAMP DEFAULT NOW(),
    updated_at       TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_students_school_id ON students(school_id);
CREATE INDEX idx_students_class_id ON students(class_id);
CREATE INDEX idx_students_name ON students(school_id, first_name, last_name);

-- ============================================================
-- TABLE 6: PARENTS
-- ============================================================
CREATE TABLE parents (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID REFERENCES schools(id) ON DELETE CASCADE,  -- for tenant isolation
    student_id       UUID REFERENCES students(id) ON DELETE CASCADE,
    name             VARCHAR(255) NOT NULL,
    relation         VARCHAR(30),   -- FATHER, MOTHER, GUARDIAN
    phone            VARCHAR(15) NOT NULL,
    whatsapp_number  VARCHAR(15),   -- may differ from phone
    is_primary       BOOLEAN DEFAULT FALSE,
    whatsapp_opt_out BOOLEAN DEFAULT FALSE,  -- true if parent replied STOP
    created_at       TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_parents_student_id ON parents(student_id);
CREATE INDEX idx_parents_school_id ON parents(school_id);

-- ============================================================
-- TABLE 7: HOLIDAYS
-- ============================================================
CREATE TABLE holidays (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID REFERENCES schools(id) ON DELETE CASCADE,
    academic_year_id UUID REFERENCES academic_years(id),
    date             DATE NOT NULL,
    name             VARCHAR(100),    -- "Diwali", "Republic Day", "Sports Day"
    type             VARCHAR(20),     -- PUBLIC, SCHOOL, HALF_DAY
    created_at       TIMESTAMP DEFAULT NOW(),
    UNIQUE(school_id, date)
);

CREATE INDEX idx_holidays_school_year ON holidays(school_id, academic_year_id);

-- ============================================================
-- TABLE 8: FEE TYPES
-- ============================================================
CREATE TABLE fee_types (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id    UUID REFERENCES schools(id) ON DELETE CASCADE,
    name         VARCHAR(100) NOT NULL,    -- "Tuition Fee", "Transport Fee"
    description  TEXT,
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT NOW()
);

-- ============================================================
-- TABLE 9: FEE STRUCTURES
-- ============================================================
CREATE TABLE fee_structures (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID REFERENCES schools(id) ON DELETE CASCADE,
    class_id         UUID REFERENCES classes(id),
    fee_type_id      UUID REFERENCES fee_types(id),
    academic_year_id UUID REFERENCES academic_years(id),
    amount           DECIMAL(10, 2) NOT NULL,
    frequency        VARCHAR(20) NOT NULL,  -- MONTHLY, QUARTERLY, ANNUAL, ONE_TIME
    due_day          INTEGER,               -- day of month fee is due
    created_at       TIMESTAMP DEFAULT NOW(),
    UNIQUE(class_id, fee_type_id, academic_year_id)
);

CREATE INDEX idx_fee_structures_class ON fee_structures(class_id);

-- ============================================================
-- TABLE 10: FEE CONCESSIONS
-- ============================================================
CREATE TABLE fee_concessions (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID REFERENCES schools(id) ON DELETE CASCADE,
    student_id       UUID REFERENCES students(id) ON DELETE CASCADE,
    fee_type_id      UUID REFERENCES fee_types(id),
    academic_year_id UUID REFERENCES academic_years(id),
    concession_type  VARCHAR(20) NOT NULL,   -- PERCENTAGE, FIXED_AMOUNT, FULL_WAIVER
    concession_value DECIMAL(10, 2),         -- 50.00 for 50% or ₹50.00
    reason           VARCHAR(200),           -- "Staff child", "Merit scholarship", "Orphan"
    approved_by      UUID REFERENCES users(id),
    is_active        BOOLEAN DEFAULT TRUE,
    created_at       TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_fee_concessions_student ON fee_concessions(student_id, academic_year_id);

-- ============================================================
-- TABLE 11: FEE PAYMENTS
-- ============================================================
CREATE TABLE fee_payments (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID REFERENCES schools(id) ON DELETE CASCADE,
    student_id       UUID REFERENCES students(id),
    fee_type_id      UUID REFERENCES fee_types(id),
    academic_year_id UUID REFERENCES academic_years(id),
    amount_paid      DECIMAL(10, 2) NOT NULL,
    amount_due       DECIMAL(10, 2) NOT NULL,   -- amount_due = fee_structure.amount - concession
    amount_waived    DECIMAL(10, 2) DEFAULT 0,   -- concession amount waived this payment
    payment_method   VARCHAR(20) NOT NULL,        -- CASH, UPI, CHEQUE, ONLINE
    payment_date     DATE NOT NULL DEFAULT CURRENT_DATE,
    for_month        VARCHAR(7),                  -- "2025-06" for monthly fees
    receipt_number   VARCHAR(50) UNIQUE,
    receipt_url      VARCHAR(500),                -- Cloudflare R2 URL
    status           VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, VOIDED
    void_reason      TEXT,
    voided_by        UUID REFERENCES users(id),
    voided_at        TIMESTAMP,
    collected_by     UUID REFERENCES users(id),
    notes            TEXT,
    transaction_ref  VARCHAR(100),               -- UPI transaction ID or cheque number
    created_at       TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_fee_payments_school_student ON fee_payments(school_id, student_id);
CREATE INDEX idx_fee_payments_date ON fee_payments(payment_date);
CREATE INDEX idx_fee_payments_academic_year ON fee_payments(academic_year_id);
CREATE INDEX idx_fee_payments_status ON fee_payments(school_id, status);

-- ============================================================
-- TABLE 12: ATTENDANCE
-- ============================================================
CREATE TABLE attendance (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id             UUID REFERENCES schools(id) ON DELETE CASCADE,
    student_id            UUID REFERENCES students(id),
    class_id              UUID REFERENCES classes(id),
    academic_year_id      UUID REFERENCES academic_years(id),
    date                  DATE NOT NULL,
    status                VARCHAR(10) NOT NULL,  -- PRESENT, ABSENT, LATE, LEAVE
    marked_by             UUID REFERENCES users(id),
    notification_sent     BOOLEAN DEFAULT FALSE,
    notification_sent_at  TIMESTAMP,
    created_at            TIMESTAMP DEFAULT NOW(),
    UNIQUE(student_id, date)
);

CREATE INDEX idx_attendance_school_date ON attendance(school_id, date);
CREATE INDEX idx_attendance_student_date ON attendance(student_id, date);
CREATE INDEX idx_attendance_class_date ON attendance(class_id, date);

-- ============================================================
-- TABLE 13: MESSAGE LOGS
-- ============================================================
CREATE TABLE message_logs (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id           UUID REFERENCES schools(id) ON DELETE CASCADE,
    student_id          UUID REFERENCES students(id),
    recipient_phone     VARCHAR(15) NOT NULL,
    channel             VARCHAR(10) DEFAULT 'WHATSAPP',  -- WHATSAPP, SMS
    message_type        VARCHAR(50),  -- FEE_REMINDER, RECEIPT, ABSENT_NOTIF, BROADCAST, REPORT_CARD
    template_name       VARCHAR(100),
    message_body        TEXT,
    status              VARCHAR(20) DEFAULT 'PENDING',  -- PENDING, SENT, DELIVERED, READ, FAILED
    whatsapp_message_id VARCHAR(200),
    failure_reason      TEXT,
    sent_at             TIMESTAMP,
    delivered_at        TIMESTAMP,
    read_at             TIMESTAMP,
    created_at          TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_message_logs_school_status ON message_logs(school_id, status);
CREATE INDEX idx_message_logs_student ON message_logs(student_id);

-- ============================================================
-- TABLE 14: EXAMS
-- ============================================================
CREATE TABLE exams (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID REFERENCES schools(id) ON DELETE CASCADE,
    academic_year_id UUID REFERENCES academic_years(id),
    name             VARCHAR(100) NOT NULL,  -- "Half Yearly 2025", "Unit Test 1"
    exam_type        VARCHAR(30),            -- UNIT_TEST, HALF_YEARLY, ANNUAL, PRACTICAL, INTERNAL
    start_date       DATE,
    end_date         DATE,
    result_published BOOLEAN DEFAULT FALSE,
    published_at     TIMESTAMP,
    created_at       TIMESTAMP DEFAULT NOW()
);

-- ============================================================
-- TABLE 15: EXAM SUBJECTS
-- ============================================================
CREATE TABLE exam_subjects (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exam_id        UUID REFERENCES exams(id) ON DELETE CASCADE,
    class_id       UUID REFERENCES classes(id),
    subject        VARCHAR(100) NOT NULL,
    max_marks      DECIMAL(5, 2) NOT NULL,
    passing_marks  DECIMAL(5, 2),
    exam_date      DATE,
    created_at     TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_exam_subjects_exam ON exam_subjects(exam_id);

-- ============================================================
-- TABLE 16: EXAM MARKS
-- ============================================================
CREATE TABLE exam_marks (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID REFERENCES schools(id) ON DELETE CASCADE,
    student_id       UUID REFERENCES students(id),
    exam_subject_id  UUID REFERENCES exam_subjects(id),
    marks_obtained   DECIMAL(5, 2),
    is_absent        BOOLEAN DEFAULT FALSE,
    remarks          VARCHAR(200),
    entered_by       UUID REFERENCES users(id),
    created_at       TIMESTAMP DEFAULT NOW(),
    updated_at       TIMESTAMP DEFAULT NOW(),
    UNIQUE(student_id, exam_subject_id)
);

CREATE INDEX idx_exam_marks_student ON exam_marks(student_id);

-- ============================================================
-- TABLE 17: EXPENSES
-- ============================================================
CREATE TABLE expenses (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID REFERENCES schools(id) ON DELETE CASCADE,
    academic_year_id UUID REFERENCES academic_years(id),
    category         VARCHAR(50),    -- SALARY, UTILITIES, MAINTENANCE, SUPPLIES, RENT, OTHER
    description      VARCHAR(255),
    amount           DECIMAL(10, 2) NOT NULL,
    expense_date     DATE NOT NULL DEFAULT CURRENT_DATE,
    payment_method   VARCHAR(20),    -- CASH, UPI, BANK_TRANSFER, CHEQUE
    recorded_by      UUID REFERENCES users(id),
    receipt_url      VARCHAR(500),
    created_at       TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_expenses_school_date ON expenses(school_id, expense_date);

-- ============================================================
-- TABLE 18: SUBSCRIPTIONS (BILLING RECORDS)
-- ============================================================
CREATE TABLE subscriptions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id           UUID REFERENCES schools(id) ON DELETE CASCADE,
    plan_type           VARCHAR(50) NOT NULL,
    billing_cycle       VARCHAR(10) NOT NULL,         -- MONTHLY, ANNUAL
    base_amount         DECIMAL(10, 2) NOT NULL,
    gst_amount          DECIMAL(10, 2) NOT NULL,
    total_amount        DECIMAL(10, 2) NOT NULL,       -- base + GST
    currency            VARCHAR(5) DEFAULT 'INR',
    starts_at           TIMESTAMP NOT NULL,
    ends_at             TIMESTAMP,
    next_billing_date   DATE,
    auto_renew          BOOLEAN DEFAULT TRUE,
    payment_method      VARCHAR(30),                   -- RAZORPAY, BANK_TRANSFER, CASH, PROMO
    payment_reference   VARCHAR(200),                  -- Razorpay payment_id
    razorpay_sub_id     VARCHAR(100),                  -- Razorpay subscription_id
    invoice_number      VARCHAR(50) UNIQUE,
    invoice_url         VARCHAR(500),
    status              VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, EXPIRED, CANCELLED, FAILED
    failure_reason      TEXT,
    created_at          TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_subscriptions_school ON subscriptions(school_id);

-- ============================================================
-- TABLE 19: STUDENT PROMOTIONS (YEAR-END HISTORY)
-- ============================================================
CREATE TABLE student_promotions (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id              UUID REFERENCES schools(id) ON DELETE CASCADE,
    student_id             UUID REFERENCES students(id),
    from_class_id          UUID REFERENCES classes(id),
    to_class_id            UUID REFERENCES classes(id),
    from_academic_year_id  UUID REFERENCES academic_years(id),
    to_academic_year_id    UUID REFERENCES academic_years(id),
    status                 VARCHAR(20),  -- PROMOTED, DETAINED, LEFT_SCHOOL, PASSED_OUT, TRANSFERRED
    remarks                TEXT,
    promoted_by            UUID REFERENCES users(id),
    promoted_at            TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_student_promotions_student ON student_promotions(student_id);

-- ============================================================
-- TABLE 20: AUDIT LOGS (IMMUTABLE)
-- ============================================================
CREATE TABLE audit_logs (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id    UUID REFERENCES schools(id) ON DELETE SET NULL,
    user_id      UUID REFERENCES users(id) ON DELETE SET NULL,
    user_name    VARCHAR(255),           -- denormalized — preserved even if user is deleted
    action       VARCHAR(100) NOT NULL,  -- FEE_COLLECTED, FEE_VOIDED, STUDENT_ADDED, etc.
    entity_type  VARCHAR(50),            -- FeePayment, Student, FeeStructure, User
    entity_id    UUID,
    old_value    JSONB,                  -- previous state snapshot
    new_value    JSONB,                  -- new state snapshot
    ip_address   VARCHAR(45),
    user_agent   TEXT,
    created_at   TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_school ON audit_logs(school_id, created_at DESC);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
-- NOTE: NO UPDATE or DELETE permissions on this table in application.
-- Only INSERT is allowed. Enforce at DB user permissions level.

-- ============================================================
-- TABLE 21: NOTIFICATION JOBS (ASYNC JOB TRACKER)
-- ============================================================
CREATE TABLE notification_jobs (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id    UUID REFERENCES schools(id) ON DELETE CASCADE,
    job_type     VARCHAR(50),    -- BULK_REMINDER, PDF_REPORT, CSV_IMPORT, BULK_MARKS
    status       VARCHAR(20) DEFAULT 'PENDING',  -- PENDING, RUNNING, DONE, FAILED
    total        INTEGER DEFAULT 0,
    processed    INTEGER DEFAULT 0,
    failed       INTEGER DEFAULT 0,
    error_log    TEXT,
    result_url   VARCHAR(500),   -- download URL when complete
    created_by   UUID REFERENCES users(id),
    started_at   TIMESTAMP,
    completed_at TIMESTAMP,
    created_at   TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_notif_jobs_school ON notification_jobs(school_id, created_at DESC);

-- ============================================================
-- TABLE 22: APP NOTIFICATIONS (IN-APP NOTIFICATION CENTER)
-- ============================================================
CREATE TABLE app_notifications (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id   UUID REFERENCES schools(id) ON DELETE CASCADE,
    user_id     UUID REFERENCES users(id) ON DELETE CASCADE,
    type        VARCHAR(50),    -- FEE_OVERDUE_ALERT, PLAN_EXPIRY, ATTENDANCE_MISSING, etc.
    title       VARCHAR(255),
    body        TEXT,
    action_url  VARCHAR(255),   -- frontend route to navigate to on click
    is_read     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_app_notifications_user ON app_notifications(user_id, is_read, created_at DESC);
```

### Additional Indexes for Performance
```sql
-- Composite indexes for common query patterns
CREATE INDEX idx_fee_payments_month ON fee_payments(school_id, for_month) WHERE status = 'ACTIVE';
CREATE INDEX idx_attendance_status ON attendance(school_id, status, date);
CREATE INDEX idx_students_active ON students(school_id, is_active, class_id);
```

---

## 11. API DESIGN (COMPLETE)

### Base URL & Versioning
```
Production:  https://api.vidyasetu.in/v1
Staging:     https://staging-api.vidyasetu.in/v1
Local:       http://localhost:8080/v1
Swagger UI:  https://api.vidyasetu.in/swagger-ui.html
```

API Version policy: v1 will be supported for minimum 18 months after any v2 launch.

### Authentication
All protected endpoints require a valid session cookie (HttpOnly JWT cookie set at login).
No Authorization header needed — cookie is sent automatically by browser.
```
Cookie: access_token=<JWT>; HttpOnly; SameSite=Strict; Secure
```

### Standard Response Format
```json
{
  "success": true,
  "data": {
    "items": [...],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 145,
      "totalPages": 8
    }
  },
  "message": "Students fetched successfully",
  "timestamp": "2025-06-01T10:30:00Z"
}
```

### Standard Error Format
```json
{
  "success": false,
  "error": {
    "code": "FEE_PAYMENT_NOT_FOUND",
    "message": "No payment found with ID xyz",
    "field": null,
    "details": null
  },
  "timestamp": "2025-06-01T10:30:00Z"
}
```

### Complete Endpoint Reference

#### AUTH
```
POST   /auth/register               # New school registration (returns school + owner user)
POST   /auth/login                  # Phone + password → sets HttpOnly cookie
POST   /auth/logout                 # Clears cookie
POST   /auth/refresh                # Rotate access token using refresh token cookie
POST   /auth/forgot-password        # Send OTP to registered phone
POST   /auth/verify-otp             # Verify OTP + issue reset token
POST   /auth/reset-password         # Reset password with reset token
GET    /auth/me                     # Get current logged-in user info
```

#### ACADEMIC YEARS
```
GET    /academic-years              # List all academic years for this school
POST   /academic-years              # Create new academic year
GET    /academic-years/{id}         # Get single academic year
PUT    /academic-years/{id}         # Update academic year
POST   /academic-years/{id}/set-current   # Make this the active year
POST   /academic-years/{id}/archive        # Archive old year (makes read-only)
POST   /academic-years/{id}/promote-students  # Year-end bulk promotion wizard
```

#### HOLIDAYS
```
GET    /holidays?academicYearId=    # List holidays for a year
POST   /holidays                    # Add single holiday
DELETE /holidays/{id}               # Remove holiday
POST   /holidays/bulk               # Bulk import holidays (JSON array)
GET    /holidays/public             # Get pre-loaded public holidays for India
```

#### CLASSES
```
GET    /classes?academicYearId=     # List classes for current/specific year
POST   /classes                     # Create class
GET    /classes/{id}                # Get class details
PUT    /classes/{id}                # Update class
PATCH  /classes/{id}                # Partial update (e.g., assign teacher)
DELETE /classes/{id}                # Delete class (only if no students)
GET    /classes/{id}/students       # All students in a class
```

#### STUDENTS
```
GET    /students                    # List students (pagination, search, class filter)
POST   /students                    # Add single student
GET    /students/{id}               # Student profile
PUT    /students/{id}               # Full update
PATCH  /students/{id}               # Partial update (e.g., update phone number)
DELETE /students/{id}               # Soft delete (is_active = false)
GET    /students/{id}/fees          # Fee history
GET    /students/{id}/attendance    # Attendance history
GET    /students/{id}/marks         # Exam marks history
GET    /students/{id}/messages      # Communication log
POST   /students/export             # Export student list (CSV/PDF)
```

#### PARENTS
```
GET    /students/{id}/parents       # List parents for a student
POST   /students/{id}/parents       # Add parent
PUT    /parents/{id}                # Update parent details
DELETE /parents/{id}                # Remove parent contact
POST   /parents/{id}/opt-out        # Mark as WhatsApp opt-out
```

#### FEE TYPES
```
GET    /fee-types                   # List all fee types
POST   /fee-types                   # Create fee type
PUT    /fee-types/{id}              # Update
DELETE /fee-types/{id}              # Soft delete (is_active = false)
```

#### FEE STRUCTURES
```
GET    /fee-structures?classId=&academicYearId=   # List structures
POST   /fee-structures              # Create fee structure
PUT    /fee-structures/{id}         # Update amount / frequency
DELETE /fee-structures/{id}         # Remove structure
```

#### FEE CONCESSIONS
```
GET    /fee-concessions?studentId=&academicYearId=  # List concessions
POST   /fee-concessions             # Grant concession
PUT    /fee-concessions/{id}        # Update concession
DELETE /fee-concessions/{id}        # Remove concession
```

#### FEE PAYMENTS
```
GET    /fee-payments                # List payments (date, class, fee-type, status filters)
POST   /fee-payments                # Record new payment
GET    /fee-payments/{id}           # Payment detail
GET    /fee-payments/{id}/receipt   # Download PDF receipt
POST   /fee-payments/{id}/void      # Void payment (ROLE_OWNER only, requires reason)
GET    /fees/defaulters             # List defaulters (concession-aware, class/days filter)
GET    /fees/summary                # Dashboard summary: collected, pending, waived
GET    /fees/student/{id}/balance   # Outstanding balance for one student
```

#### ATTENDANCE
```
POST   /attendance/bulk             # Mark attendance for entire class (array of {studentId, status})
GET    /attendance?date=&classId=   # View attendance for a specific day and class
PATCH  /attendance/{id}             # Correct an attendance entry (ROLE_OWNER or ROLE_ADMIN)
GET    /attendance/summary?studentId=&month=  # Monthly summary per student
GET    /attendance/class-summary?classId=&month=  # Class-wise monthly summary
```

#### STAFF/USERS
```
GET    /users                       # List all staff (teachers, admins)
POST   /users                       # Add staff member
GET    /users/{id}                  # Get user profile
PUT    /users/{id}                  # Update user
PATCH  /users/{id}                  # Partial update (e.g., assign class)
DELETE /users/{id}                  # Deactivate user
POST   /users/{id}/reset-password   # Owner resets a staff member's password
```

#### MESSAGING
```
POST   /messages/fee-reminder       # Send WhatsApp reminders to selected defaulters → returns jobId
POST   /messages/broadcast          # Broadcast to all/class parents
GET    /messages/logs               # Message history with filters
GET    /messages/logs/stats         # Sent/delivered/failed counts for last 30 days
```

#### EXAMS
```
GET    /exams?academicYearId=       # List exams
POST   /exams                       # Create exam
GET    /exams/{id}                  # Exam details
PUT    /exams/{id}                  # Update exam
GET    /exams/{id}/subjects         # List exam subjects
POST   /exams/{id}/subjects/bulk    # Bulk create subjects for all classes
GET    /exams/{id}/marks?classId=   # Marks entry sheet for a class
POST   /exams/{id}/marks/bulk       # Bulk save marks for entire class
POST   /exams/{id}/publish          # Publish results (locks marks)
GET    /exams/{id}/report-cards     # Generate report cards → returns jobId
```

#### EXPENSES
```
GET    /expenses?month=&year=       # List expenses
POST   /expenses                    # Record expense
PUT    /expenses/{id}               # Update
DELETE /expenses/{id}               # Delete
GET    /expenses/summary?year=      # Annual expense summary by category
```

#### REPORTS
```
GET    /reports/fee-collection?month=&classId=  # Monthly collection PDF
GET    /reports/defaulters?classId=&daysOverdue=  # Defaulter report PDF
GET    /reports/attendance?studentId=&month=    # Student attendance report PDF
GET    /reports/financial-summary?academicYearId=  # Annual P&L summary PDF
GET    /reports/exam?examId=&classId=           # Class exam result sheet PDF
```

#### DATA IMPORT/EXPORT
```
GET    /import/template/students    # Download CSV template for student import
POST   /import/students             # Upload CSV → validate → returns jobId
POST   /import/fee-payments         # Upload historical payments CSV → returns jobId
GET    /import/jobs/{jobId}         # Poll import job status and results
POST   /export/students             # Export students as CSV
POST   /export/fee-payments         # Export payments as CSV
POST   /export/full                 # Export full school data as ZIP → returns jobId
GET    /export/jobs/{jobId}         # Poll export job status and download URL
```

#### ASYNC JOBS
```
GET    /jobs/{jobId}                # Get status of any async job
GET    /jobs?type=&status=          # List recent jobs
```

#### DASHBOARD
```
GET    /dashboard                   # All stats: students, fees, attendance, alerts
```

#### AUDIT LOGS
```
GET    /audit-logs                  # ROLE_OWNER only. Filter by date, user, action
```

#### IN-APP NOTIFICATIONS
```
GET    /notifications               # Unread + recent notifications for current user
POST   /notifications/read-all     # Mark all as read
PATCH  /notifications/{id}/read    # Mark one as read
```

#### SUBSCRIPTION & BILLING
```
GET    /billing/current-plan        # Current plan details and usage stats
GET    /billing/invoices            # Billing history
POST   /billing/upgrade             # Create Razorpay order for plan upgrade
POST   /billing/webhook             # Razorpay webhook for payment confirmation (no auth)
```

#### SCHOOL SETTINGS
```
GET    /settings                    # Get all school settings
PUT    /settings/profile            # Update school name, address, logo
PUT    /settings/academic           # Update academic preferences
PUT    /settings/fees               # Update fee preferences
PUT    /settings/whatsapp           # Connect/update WhatsApp Business API
PUT    /settings/notifications      # Toggle automated notification types
```

---

## 12. FRONTEND DESIGN & UX

### Design Principles
- **Mobile-first** — every screen designed for 360px width first, then desktop
- **Hindi + English** — toggle per school preference, all strings in i18n files
- **Minimal taps** — most common actions reachable within 2 taps from dashboard
- **High contrast** — readable in sunlight on cheap screens
- **Skeleton loaders** — never blank screens; always show loading state
- **Empty states with action** — empty screen = invitation to act, not a dead end
- **Error states with recovery** — every error tells the user what to do next
- **Offline indicator** — banner shown when network is unavailable

### Component Library
- **shadcn/ui** — accessible, Radix-based, Tailwind-styled, copy-paste into codebase
- Key components used: Dialog, Sheet, DropdownMenu, Table, DatePicker, Toast, Select, Badge, Card

### All Screens (Complete List)

#### Navigation Structure
```
/                    → Redirect to /dashboard
/login               → Login page (phone + password)
/dashboard           → Owner Dashboard
/students            → Student List
/students/new        → Add Student
/students/:id        → Student Profile (tabs: Fees, Attendance, Marks, Messages)
/fees                → Fee Overview (defaulter list, collection)
/fees/collect        → Record Payment
/fees/structures     → Fee Structure Setup
/fees/concessions    → Concession Management
/attendance          → Attendance (teacher: mark today; owner: view any day)
/attendance/report   → Attendance Reports
/exams               → Exam List
/exams/new           → Create Exam
/exams/:id/marks     → Marks Entry
/reports             → Reports Hub
/messages            → Message Logs + Broadcast
/staff               → Staff/Teacher Management
/academic-years      → Academic Year Management
/settings            → School Settings
/billing             → Subscription & Billing
/audit-logs          → Audit Log Viewer (OWNER only)
/notifications       → Notification Center
```

### Color Palette
| Token | Hex | Usage |
|-------|-----|-------|
| `--primary` | #1A73E8 | Buttons, links, active states |
| `--success` | #34A853 | Paid, present, positive |
| `--warning` | #FBBC04 | Partial, late, caution |
| `--danger` | #EA4335 | Defaulter, absent, error |
| `--surface` | #F8F9FA | Page background |
| `--card` | #FFFFFF | Card backgrounds |
| `--text-primary` | #202124 | Body text |
| `--text-secondary` | #5F6368 | Labels, hints |
| `--border` | #DADCE0 | Card borders, dividers |

### Hindi / English i18n
- All UI strings in `/src/i18n/hi.json` and `/src/i18n/en.json`
- Language toggle in header — persisted in `localStorage` as user preference
- Default: school's `language_preference` field from DB
- Numbers: always English numerals (not Devanagari)
- Date format: DD/MM/YYYY (Indian standard)
- Currency: ₹ symbol, Indian comma formatting (₹1,00,000 not ₹100,000)

### Key Screen Specifications

**Dashboard:** Stats cards (students, collected, pending, attendance%), 6-month fee collection bar chart (Recharts), Recent payments table, Alert badges, Quick action buttons (Record Payment, Mark Attendance, Send Reminders).

**Student List:** Search bar + class filter + active/inactive toggle. @tanstack/react-table with pagination. Student card shows: photo, name, class, fee status badge (PAID / PARTIAL / DUE / OVERDUE). Tap → Student Profile.

**Fee Collection Form:** Step 1 — Search/select student. Step 2 — Select fee type (shows amount due including any concession). Step 3 — Enter amount paid. Step 4 — Select method (Cash/UPI/Cheque). Step 5 — Confirm → receipt auto-generated → WhatsApp sent. All in one page, no multi-step navigation.

**Defaulter List:** Class filter, days-overdue filter. Table: student name, class, fee type, amount due, days overdue. Checkbox selection for bulk reminders. "Send Reminder to Selected (N)" button.

**Attendance Page (Teacher):** Date picker (default today). Class selector. Student list with one-tap P/A/L/Leave buttons (full-width, minimum 56px height). Submit button → sends absent notifications async.

**Marks Entry:** Class + exam selector. Grid table: rows = students, columns = subjects. Input cells for marks. Validation: marks ≤ max_marks. Bulk save in one API call.

**Empty States (Every List Page):**
- Students: "No students added yet. [Add First Student]"
- Fee payments: "No payments recorded this month."
- Defaulters: "🎉 All fees are paid! No defaulters."
- Notifications: "You're all caught up."

**Error States:** Network error → "Check your internet and try again. [Retry]". Server error → "Something went wrong on our end. Try again in a moment." Auth expired → redirect to login with "Your session expired. Please login again."

### Frontend Dependencies (Complete)
```json
{
  "dependencies": {
    "react": "^18.x",
    "react-dom": "^18.x",
    "react-router-dom": "^6.x",
    "axios": "^1.x",
    "@tanstack/react-query": "^5.x",
    "@tanstack/react-table": "^8.x",
    "react-hook-form": "^7.x",
    "zod": "^3.x",
    "@hookform/resolvers": "^3.x",
    "recharts": "^2.x",
    "date-fns": "^3.x",
    "sonner": "^1.x",
    "lucide-react": "^0.x",
    "clsx": "^2.x",
    "tailwind-merge": "^2.x"
  },
  "devDependencies": {
    "vite": "^5.x",
    "tailwindcss": "^3.x",
    "postcss": "^8.x",
    "autoprefixer": "^10.x",
    "@vitejs/plugin-react": "^4.x"
  }
}
```

---

## 13. ACADEMIC YEAR & CALENDAR MANAGEMENT

### Academic Year Lifecycle

```
CREATE (start_date, end_date, name)
    ↓
SET AS CURRENT (marks is_current = true, unsets previous)
    ↓
[School operates for April–March]
    ↓
YEAR-END: Bulk Promote Students
    ↓
CREATE NEXT YEAR (2026-2027)
    ↓
SET NEW YEAR AS CURRENT
    ↓
ARCHIVE OLD YEAR (is_archived = true — data visible, not editable)
```

### Student Promotion Workflow (Year-End)
Triggered via `POST /academic-years/{id}/promote-students`:

1. System fetches all active students for the current year
2. Shows owner a class-wise promotion grid:
   - Class 1A → Class 2A (auto-suggested next class)
   - Override: owner can assign any target class
   - Status per student: PROMOTED / DETAINED / LEFT_SCHOOL / PASSED_OUT
3. Owner reviews and confirms
4. System:
   - Creates `student_promotions` record per student
   - Updates `student.class_id` to new class
   - Carries forward any unpaid fees as "Previous Year Balance" fee type
   - Archives the old academic year

### Holiday Calendar
- Pre-loaded public holidays for India (national + major state holidays for UP)
- School can add/remove holidays freely
- Half-day holidays: attendance still marked but working hours halved (currently informational)
- Weekly offs: defined in school settings (Sunday / Saturday+Sunday / None)
- Working days formula: `TOTAL_DAYS - HOLIDAYS - WEEKLY_OFFS_IN_RANGE`
- Attendance % = `PRESENT + LATE` days ÷ Working days × 100

---

## 14. NOTIFICATION & SCHEDULER SYSTEM

### Automated Scheduler Jobs (`@Scheduled`)

| Job | Schedule | Action |
|-----|----------|--------|
| `MonthlyFeeGenerator` | 1st of every month, 6:00 AM IST | Auto-generate fee due records for all active students based on their fee structures |
| `MonthlyFeeReminder` | 6th of every month, 9:00 AM IST | WhatsApp reminder to all defaulters (respects opt-out list) |
| `AttendanceMissingAlert` | Daily 5:00 PM IST | Alert owner if any class has no attendance marked today (working day only) |
| `AbsentNotificationRetry` | Daily 8:00 PM IST | Retry failed absent WhatsApp notifications |
| `PlanExpiryWarning` | Daily 10:00 AM IST | WhatsApp + in-app alert if plan expires in 7 days |
| `PlanExpiredDowngrade` | Daily 12:00 AM IST | Downgrade school to FREE plan if plan expired > 7 days ago |
| `StaleAttendanceAlert` | Every Monday 9:00 AM IST | Alert if no attendance marked in last 7 days (not holiday) |

### In-App Notification Types

| Type | Trigger | Message |
|------|---------|---------|
| `FEE_OVERDUE_ALERT` | Daily job | "15 students have fees pending for 30+ days" |
| `ATTENDANCE_MISSING` | 5PM daily | "Attendance not marked today for: Class 5A, Class 6B" |
| `PLAN_EXPIRY_WARNING` | 7 days before | "Your plan expires on [date]. Renew to continue." |
| `PLAN_EXPIRED` | Day of expiry | "Your plan has expired. Upgrade to restore full access." |
| `STUDENT_LIMIT_WARNING` | On student add | "You have 143/150 students. Upgrade for more." |
| `WHATSAPP_FAILURES` | After bulk send | "3 WhatsApp messages failed. Check parent numbers." |
| `IMPORT_COMPLETE` | After import job | "Student import complete: 197 added, 3 failed." |
| `REPORT_READY` | After report job | "Your monthly report is ready. [Download]" |

---

## 15. DATA IMPORT & EXPORT SYSTEM

### Student CSV Import

**Step 1:** Owner downloads CSV template from `/import/template/students`
**Template columns:** `first_name*, last_name, class_name*, section, roll_number, dob (DD/MM/YYYY), gender, parent_name*, parent_phone*, parent_relation, parent_whatsapp`
(* = required)

**Step 2:** Owner fills template in Excel/Google Sheets and uploads

**Step 3:** Server validates:
- Required fields present
- Phone numbers: exactly 10 digits, starts with 6–9
- Class name matches an existing class in this school
- Duplicate roll numbers flagged
- Returns validation report: `{ valid: 197, invalid: 3, errors: [{row: 23, error: "Phone invalid"}, ...] }`

**Step 4:** Owner reviews errors, confirms import of valid rows
**Step 5:** Background job inserts valid students (async, returns `jobId`)
**Step 6:** In-app notification when complete

**Limits:** Max 500 students per import batch. Max file size: 5MB.

### Data Export
- **Students CSV:** name, class, roll number, parent name, phone — instant download
- **Fee Payments CSV:** all payments with date, amount, method, receipt number — instant
- **Full Backup ZIP:** all data as JSON files + PDFs — async job, download when ready
- All exports are school-scoped (no cross-tenant data ever)
- Exports available even on Free plan (data portability guarantee)

---

## 16. SUBSCRIPTION & BILLING MANAGEMENT

### Razorpay Integration Flow

```
Owner clicks "Upgrade to Basic ₹499/month"
         ↓
Backend: POST /billing/upgrade {plan: "BASIC", cycle: "MONTHLY"}
         ↓
Backend creates Razorpay Order (or Subscription for auto-renewal)
         ↓
Frontend opens Razorpay Checkout modal
         ↓
Owner pays via UPI / Card / NetBanking
         ↓
Razorpay calls POST /billing/webhook (backend)
         ↓
Backend verifies signature (HMAC-SHA256 with Razorpay webhook secret)
         ↓
Backend updates school.plan_type + creates subscriptions record
         ↓
Backend generates GST invoice PDF (school name, GSTIN if provided, amount, GST breakup)
         ↓
Invoice sent via WhatsApp + email to school owner
         ↓
In-app notification: "Payment successful. Welcome to Basic plan!"
```

### GST Invoice Fields
- Invoice number: VID-YEAR-SEQUENCE (e.g., VID-2025-00142)
- Vidyasetu's GSTIN (register once revenue > ₹20 lakh)
- School's name and GSTIN (if provided)
- Service description: "School Management Software Subscription – [Plan] – [Period]"
- HSN code: 998314
- Base amount, CGST 9%, SGST 9%, Total
- Payment reference number

### Plan Change Logic
- **Upgrade:** Immediate activation. Charge = (days remaining in month / 30) × difference in plan price. Razorpay handles pro-ration.
- **Downgrade:** Effective from next billing cycle. School keeps current features until then.
- **Auto-renewal failure:** Retry on day 2, day 4, day 7. On day 7, send WhatsApp: "Payment failed. Update payment method to continue." After 7 days, grace period ends.
- **Cancellation:** Effective next cycle. `is_active` stays TRUE until then.
- **Free plan limits:** After expiry into Free, student management stays read-only. Cannot add new students, cannot record fees, cannot send WhatsApp. Can still export data.

---

## 17. SCHOOL SETTINGS & CONFIGURATION

### Settings Sections

**1. School Profile**
- School name, institution type (School / Coaching Center)
- Address, city, state, phone, email
- Logo upload (shown on receipts, report cards)
- GST number (optional, for invoices)

**2. Academic Settings**
- Current academic year
- Weekly off days
- Grading scale: A+/A/B/C/D/F (with configurable percentage ranges)
- Academic year start month (April for most UP schools)

**3. Fee Settings**
- Default fee due day (e.g., 10th of every month)
- Late fee: enabled/disabled, late fee amount
- Fee reminder: enable/disable automated monthly reminder

**4. WhatsApp Integration**
- Connect WhatsApp Business API (Meta Business Manager link + phone number ID + access token)
- Test connection (sends a test message to owner's own number)
- Disconnect
- Message templates status: shows approval status of each template

**5. Notification Preferences**
- Toggle per notification type: fee reminders, absent notifications, broadcast, etc.
- Default message language: Hindi / English

**6. User Management**
- List all staff with role and last login
- Add teacher/admin
- Reset password for staff member
- Deactivate/reactivate user

**7. Plan & Billing**
- Current plan, usage stats (students used / limit)
- Next billing date, auto-renewal status
- Upgrade / Downgrade options
- Invoice download history

**8. Data & Privacy**
- Export all school data (full ZIP)
- View data retention policy
- Delete account (confirmation + forced export prompt)

---

## 18. TESTING STRATEGY

### Unit Tests (JUnit 5 + Mockito)

**Target:** 70%+ line coverage on all Service layer classes

Critical test cases:
```
FeeCalculationService:
  - student with no concession → full fee shown as due
  - student with PERCENTAGE concession (50%) → half fee shown as due
  - student with FULL_WAIVER → zero due, never appears in defaulter list
  - student with partial payment → correct remaining balance
  - student with payment in previous academic year → carries forward correctly

AttendanceService:
  - attendance % with no holidays → present/30 calendar days (wrong)
  - attendance % with holidays → present/working_days (correct)
  - Sunday excluded from working days
  - half-day holiday counted as 0.5 working day

ReceiptNumberGenerator:
  - format: SCHOOLCODE-YEAR-0001
  - sequential uniqueness (no duplicates in concurrent requests)
  - year rollover: resets to 0001 each April

AuditService:
  - write ops auto-logged via AOP
  - void payment creates audit entry
  - audit table insert only — no update/delete
```

### Integration Tests (Spring Boot Test + Testcontainers)

```java
// Full flow test — most important
@Test
void fullFeeCollectionFlow() {
    // 1. Register school
    // 2. Create academic year
    // 3. Create class
    // 4. Add student with parent
    // 5. Set fee structure
    // 6. Record payment
    // 7. Verify receipt number generated
    // 8. Verify audit log entry created
    // 9. Verify student not in defaulter list
    // 10. Verify balance = 0
}

// Multi-tenant isolation — security critical
@Test
void schoolACannotSeeSchoolBData() {
    // Create 2 schools with students
    // Login as school A owner
    // Call GET /students — verify only school A students returned
    // Call GET /students/{schoolB_student_id} — verify 403 Forbidden
}

// Concession defaulter test
@Test
void concessionStudentNotInDefaulterList() {
    // Add student with 100% concession on tuition fee
    // Do not record any payment
    // Call GET /fees/defaulters
    // Verify this student is NOT in the list
}
```

### Manual QA Checklist (Run Before Each Release)

```
STUDENT MANAGEMENT:
[ ] Add new student → verify appears in list
[ ] Edit student phone number → verify updated
[ ] Soft-delete student → verify not in active list, visible in inactive
[ ] CSV import 10 students → verify all 10 appear

FEE MANAGEMENT:
[ ] Record cash payment → receipt PDF generated → WhatsApp sent to parent
[ ] Record UPI payment → verify transaction ref saved
[ ] Partial payment → verify correct balance shown in defaulter list
[ ] Add concession to student → verify they do NOT appear in defaulter list
[ ] Void payment (as OWNER) → verify voided, audit log shows void reason
[ ] Void payment (as TEACHER) → verify 403 Forbidden

ATTENDANCE:
[ ] Mark full class attendance → verify counts correct
[ ] Absent student → verify WhatsApp notification sent to parent
[ ] Add holiday → verify it is excluded from attendance % calculation
[ ] View monthly attendance report → verify working day count correct

SECURITY:
[ ] Login as TEACHER → verify cannot access /settings, /audit-logs, /billing
[ ] Login as TEACHER → verify can only access assigned classes
[ ] Try accessing another school's student ID → verify 403
[ ] 6 failed login attempts → verify account temporarily locked

REPORTS:
[ ] Generate monthly fee collection PDF → verify totals match dashboard
[ ] Generate defaulter report → verify concession students excluded
[ ] Generate attendance report → verify working-day denominator correct
```

---

## 19. BUILD TIMELINE (PHASE-BY-PHASE)

> **Assumption:** 3–4 hours/day part-time alongside job search.
> **Adjusted duration:** 28 weeks (~9–11 months at learning pace)

### ⚠️ BEFORE WRITING ANY CODE — Week 0 Actions
- [ ] Install Docker Desktop on Windows
- [ ] Run PostgreSQL via Docker: `docker run --name vidyasetu-db -e POSTGRES_PASSWORD=vidyasetu_dev -e POSTGRES_DB=vidyasetu -p 5432:5432 -d postgres:16`
- [ ] Install pgAdmin 4 (GUI for PostgreSQL)
- [ ] Create GitHub repos: `vidyasetu-backend` and `vidyasetu-frontend` (private)
- [ ] Apply for WhatsApp Business API (Meta) — approval takes 2–4 weeks. Do this FIRST.
- [ ] Register 4 WhatsApp message templates: fee_reminder, fee_receipt, absent_notification, broadcast_announcement
- [ ] Register on TRAI DLT platform for SMS (Fast2SMS) — takes 7–14 days
- [ ] Register domain: vidyasetu.in (GoDaddy/Namecheap ~₹800/year)

---

### PHASE 0 — Foundation (Weeks 1–4)

#### Week 1: Project Bootstrap
- [ ] Spring Boot project via start.spring.io (dependencies: Web, Security, Data JPA, PostgreSQL, Lombok, Validation, Flyway, Actuator)
- [ ] Add extra dependencies: `springdoc-openapi`, `bucket4j-spring-boot-starter`, `spring-retry`, `mapstruct`, `caffeine`
- [ ] React project: `npm create vite@latest vidyasetu-frontend -- --template react`
- [ ] Install frontend dependencies (see complete list in Section 12)
- [ ] Setup shadcn/ui: `npx shadcn-ui@latest init`
- [ ] Docker Compose file: PostgreSQL + pgAdmin
- [ ] GitHub Actions CI pipeline: build + test on every push to main
- [ ] `.env` and `.env.example` files for secrets
- [ ] Sentry project created, SDK integrated in Spring Boot

#### Week 2: Database Schema
- [ ] Write `V1__init.sql` Flyway migration with ALL 22 tables (from Section 10)
- [ ] Write `V2__indexes.sql` with all indexes
- [ ] Write `V3__seed_data.sql`: 1 sample school, 1 owner user, 1 academic year, 3 classes, 10 students, 5 fee payments, 10 holidays
- [ ] Test all queries in pgAdmin — verify joins and indexes work
- [ ] Write SchemaValidationTest: verifies all tables and columns exist

#### Week 3: Authentication
- [ ] `School`, `User` JPA entities with all fields
- [ ] `UserRepository`, `SchoolRepository`
- [ ] `JwtUtil`: generate/validate tokens (access 15min + refresh 30 days)
- [ ] `JwtFilter`: extract JWT from HttpOnly cookie → set SecurityContext
- [ ] `TenantContext`: ThreadLocal storing `schoolId` per request
- [ ] `POST /auth/register`: create school + owner user in one transaction
- [ ] `POST /auth/login`: validate credentials → set HttpOnly cookies (access + refresh)
- [ ] `POST /auth/logout`: clear cookies
- [ ] `POST /auth/refresh`: validate refresh token → issue new access token
- [ ] `GET /auth/me`: return current user info
- [ ] React: Login page with react-hook-form + zod validation
- [ ] React: AuthContext + Protected Route HOC
- [ ] React: Axios interceptor — on 401, attempt refresh → retry → redirect to login

#### Week 4: Academic Year + Core Schema Layer
- [ ] `AcademicYear` entity + repository + service
- [ ] `Class` entity + repository + service
- [ ] `Holiday` entity + repository + service (with pre-loaded Indian public holidays list)
- [ ] `POST /academic-years`, `GET /academic-years`, `POST /academic-years/{id}/set-current`
- [ ] `POST /classes`, `GET /classes`, `PUT /classes/{id}`
- [ ] `GET/POST/DELETE /holidays`
- [ ] React: Academic Year setup screen
- [ ] React: Class management screen
- [ ] React: Holiday calendar screen (calendar view with holidays marked)
- [ ] Unit tests for AcademicYearService + working day calculation logic

---

### PHASE 1 — Core MVP (Weeks 5–10)

#### Week 5: Student Management
- [ ] `Student`, `Parent` entities + repositories + services
- [ ] All student CRUD endpoints + parent management endpoints
- [ ] School-ID tenant scoping on all queries (verify with integration test)
- [ ] React: Student list page with @tanstack/react-table (pagination, search, class filter)
- [ ] React: Add/Edit student form (react-hook-form + zod)
- [ ] React: Student profile page (tabs: Fees, Attendance, Marks, Messages)
- [ ] React: Empty state for no-students

#### Week 6: Fee Types + Fee Structures + Concessions
- [ ] `FeeType`, `FeeStructure`, `FeeConcession` entities + repositories + services
- [ ] All fee type, structure, concession CRUD endpoints
- [ ] `FeeCalculationService.calculateAmountDue(student, feeType, month)` — concession-aware
- [ ] Unit tests for FeeCalculationService (all concession types)
- [ ] React: Fee structure setup page (class → fee type → amount → frequency)
- [ ] React: Fee concession page (search student → select fee type → grant concession)

#### Week 7: Fee Collection (Recording Payments)
- [ ] `FeePayment` entity + `FeePaymentRepository` + `FeePaymentService`
- [ ] `POST /fee-payments` — record payment, auto-generate receipt number
- [ ] `POST /fee-payments/{id}/void` — ROLE_OWNER only, requires reason
- [ ] Balance calculation: `amountDue - totalPaid` per student per fee type per month
- [ ] `GET /fees/defaulters` — concession-aware, returns only true defaulters
- [ ] `GET /fees/summary` — dashboard stats
- [ ] AuditLog entries on every payment and void
- [ ] React: Fee collection form (4-step flow on one page)
- [ ] React: Defaulter list with checkbox multi-select
- [ ] React: Voided payments badge in fee history

#### Week 8: Fee Receipt PDF (iText 7)
- [ ] `PdfReceiptService.generateReceipt(FeePayment)` using iText 7
- [ ] Receipt layout: school name + logo, student name + class, fee type, amount, date, receipt number, "Powered by Vidyasetu"
- [ ] Upload generated PDF to Cloudflare R2
- [ ] Save R2 URL to `fee_payments.receipt_url`
- [ ] `GET /fee-payments/{id}/receipt` — serve PDF (redirect to signed R2 URL)
- [ ] PDF generation runs async via `@Async` — not blocking payment API
- [ ] Unit test: generate receipt for sample payment, verify PDF is valid

#### Week 9: Owner Dashboard
- [ ] `DashboardService.getStats(schoolId)` — aggregated stats
- [ ] Cache dashboard stats in Caffeine (5-minute TTL, invalidated on payment)
- [ ] `GET /dashboard` — all stats in one call
- [ ] React: Dashboard page — stat cards, Recharts bar chart (6-month fee trend), recent payments table, quick action buttons
- [ ] React: Dashboard skeleton loader (gray placeholder before data loads)

#### Week 10: In-App Notifications + Audit Logs
- [ ] `AuditService` — AOP `@AfterReturning` on all service write methods
- [ ] `AppNotificationService.createNotification(schoolId, userId, type, title, body)`
- [ ] `GET /notifications`, `POST /notifications/read-all` endpoints
- [ ] React: Bell icon in header with unread count badge
- [ ] React: Notification panel (slide-in sheet from right)
- [ ] `GET /audit-logs` endpoint (ROLE_OWNER only, paginated with filters)
- [ ] React: Audit log page with date/user/action filters

---

### PHASE 2 — Communication (Weeks 11–14)

#### Week 11: WhatsApp Business API Integration
- [ ] `WhatsAppService.sendTemplate(phone, templateName, params)` — calls Meta Graph API
- [ ] `WhatsAppService.sendRawMessage(phone, text)` — for broadcast
- [ ] Webhook endpoint `POST /whatsapp/webhook` — Meta verification + delivery status updates
- [ ] Update `message_logs.status` (DELIVERED/READ/FAILED) from webhook callbacks
- [ ] Connection test endpoint: sends test message to school owner
- [ ] 5-second timeout + 3 retries with exponential backoff
- [ ] All calls async (`@Async`) — never block HTTP request

#### Week 12: Fee Reminders + Auto-Receipt WhatsApp
- [ ] Send WhatsApp receipt after every payment (auto, async)
- [ ] `POST /messages/fee-reminder` — sends to selected defaulters, returns jobId
- [ ] Bulk sender job: sends in batches of 20/minute (Meta rate limit aware)
- [ ] `NotificationJobService` — creates `notification_jobs` row, updates progress
- [ ] `GET /jobs/{jobId}` — returns progress: `{status, processed, total, failed}`
- [ ] React: "Send Reminder" button on defaulter page → progress modal with live polling

#### Week 13: SMS Fallback + Broadcast
- [ ] `SmsService.send(phone, message)` — calls Fast2SMS API
- [ ] Every WhatsApp send: on FAILED status, auto-retry via SMS after 30 minutes
- [ ] `POST /messages/broadcast` — school-wide or class-wise announcement
- [ ] React: Broadcast page — audience selector (all / class-wise) + message composer + send
- [ ] React: Message logs page — filter by type, status, date

#### Week 14: Scheduler Service
- [ ] `SchedulerService` with Spring `@Scheduled` for all jobs (see Section 14)
- [ ] `MonthlyFeeGenerator`: generates fee_payment "due" records (status: DUE) on 1st of month
- [ ] `MonthlyFeeReminderJob`: sends WhatsApp to all defaulters on 6th of month
- [ ] `AttendanceMissingAlert`: 5pm daily check + in-app notification
- [ ] `PlanExpiryWarning`: 7-day advance WhatsApp + in-app notification
- [ ] `PlanExpiredDowngrade`: auto-downgrade to FREE on expiry
- [ ] All scheduler jobs: logged to `notification_jobs` table with success/failure

---

### PHASE 3 — Attendance (Weeks 15–17)

#### Week 15: Teacher Accounts + RBAC
- [ ] Teacher/Admin user creation endpoints
- [ ] `POST /users` (ROLE_OWNER only)
- [ ] Class assignment: teacher can only see/mark attendance for their assigned classes
- [ ] Spring Security `@PreAuthorize` on all endpoints: verify role AND school ownership
- [ ] React: Staff management page (list, add, edit, deactivate)
- [ ] React: Teacher login experience (no sidebar items they can't access)

#### Week 16: Attendance Marking
- [ ] `AttendanceService.markBulk(classId, date, List<{studentId, status}>)`
- [ ] Duplicate prevention: `UNIQUE(student_id, date)` constraint + service-level check
- [ ] Working day validation: cannot mark attendance on holidays or weekly-off days
- [ ] `POST /attendance/bulk`, `GET /attendance`, `GET /attendance/summary`
- [ ] React: Attendance page — class selector → student list → one-tap P/A/L → submit
- [ ] Mobile UX: full-width buttons, 56px height minimum, large font

#### Week 17: Absent Notifications + Attendance Reports
- [ ] After attendance submit: async job sends WhatsApp to all absent students' parents (respecting opt-out)
- [ ] `GET /attendance/summary` — working-day-aware percentage calculation
- [ ] `GET /reports/attendance` — monthly attendance PDF
- [ ] React: Attendance report screen with student/month selector

---

### PHASE 4 — Exams, Reports & Data (Weeks 18–21)

#### Week 18: Exam Module
- [ ] `Exam`, `ExamSubject`, `ExamMark` entities + services
- [ ] `POST /exams`, `POST /exams/{id}/subjects/bulk`, `POST /exams/{id}/marks/bulk`
- [ ] `POST /exams/{id}/publish` — locks marks, sets `result_published = true`
- [ ] `ExamMarkService.calculateGrade(percentage, gradingScale)` — configurable by school
- [ ] React: Exam creation wizard (name, type, dates, then subject setup per class)
- [ ] React: Marks entry grid — table with student rows × subject columns

#### Week 19: Report Cards + Expense Tracking
- [ ] `ReportCardPdfService.generate(studentId, examId)` — iText 7 multi-subject layout
- [ ] Bulk report card generation: async job, returns jobId
- [ ] Send report cards to parents via WhatsApp after publish
- [ ] `Expense` entity + CRUD + `GET /expenses/summary`
- [ ] React: Expense tracking page + monthly P&L view
- [ ] React: Report card preview before sending

#### Week 20: Full Reports Module
- [ ] `MonthlyCollectionReport` PDF: class-wise and student-wise breakdown
- [ ] `DefaulterReport` PDF: sortable by class, days overdue
- [ ] `FinancialSummaryReport` PDF: total collected, waived (concessions), expenses, net
- [ ] React: Reports hub — all report types with date/filter options
- [ ] React: Report preview in-browser before download

#### Week 21: Data Import + Export
- [ ] CSV student import: template, upload, validate, async insert, jobId polling
- [ ] Fee payment CSV import (historical data)
- [ ] Student CSV export, fee payment CSV export
- [ ] Full school data ZIP export (async job)
- [ ] React: Import page — template download, file upload with drag-drop, validation results, confirm screen
- [ ] React: Export page — choose what to export, download buttons

---

### PHASE 5 — Billing, Settings & Year-End (Weeks 22–25)

#### Week 22: School Settings
- [ ] `PUT /settings/profile`, `PUT /settings/academic`, `PUT /settings/fees`
- [ ] `PUT /settings/whatsapp` — save and test WhatsApp credentials
- [ ] `PUT /settings/notifications` — per-notification-type toggles
- [ ] React: Settings page — all sections with forms, save state management

#### Week 23: Subscription & Billing
- [ ] Razorpay SDK integration
- [ ] `POST /billing/upgrade` — creates Razorpay order
- [ ] `POST /billing/webhook` — verifies signature, updates plan, generates invoice
- [ ] `PdfInvoiceService.generateGstInvoice(subscription)` — iText 7 invoice PDF
- [ ] Invoice sent via WhatsApp + stored in R2
- [ ] React: Billing page — current plan stats, usage bars, upgrade CTAs, invoice history

#### Week 24: Year-End Promotion + Archive
- [ ] `POST /academic-years/{id}/promote-students` — promotion wizard API
- [ ] Carry-forward unpaid fees as "Previous Balance" fee type in new year
- [ ] `POST /academic-years/{id}/archive` — makes year read-only
- [ ] React: Year-end promotion wizard UI — class-by-class with status assignment

#### Week 25: Integration Testing + Staging
- [ ] Write all integration tests (see Section 18)
- [ ] Set up Railway staging environment (separate DB, same code)
- [ ] Run full manual QA checklist on staging
- [ ] Fix all bugs found
- [ ] Load test: simulate 100 concurrent schools, 500 students each

---

### PHASE 6 — Polish, Security & Launch (Weeks 26–28)

#### Week 26: Hindi Language + Mobile Polish
- [ ] Complete Hindi translations in `hi.json` (every string in the app)
- [ ] Language toggle in header — persists on reload
- [ ] Full mobile responsiveness audit (test every screen at 360px and 390px)
- [ ] Test on actual Android device (Redmi/Realme) — fix touch targets, font sizes
- [ ] Offline indicator: detect `navigator.onLine` → show banner

#### Week 27: Security Audit + Performance
- [ ] OWASP Top 10 checklist review:
  - [ ] SQL injection: all queries via JPA (parameterized) ✓
  - [ ] XSS: React escapes HTML by default ✓; review any `dangerouslySetInnerHTML`
  - [ ] Broken auth: review JWT validation, cookie flags
  - [ ] IDOR: every entity access check verifies `school_id` matches current school
  - [ ] Rate limiting: Bucket4j on all endpoints
- [ ] Performance: run explain analyze on all slow queries (> 100ms)
- [ ] Add missing indexes if found
- [ ] Test PDF generation memory usage with 300-student report
- [ ] Input validation: verify all 400 errors have human-readable messages

#### Week 28: Production Launch
- [ ] Neon PostgreSQL production database (Mumbai region) — run migrations
- [ ] Railway production app deployment — connect to Neon
- [ ] Vercel production frontend deployment
- [ ] Custom domain: `vidyasetu.in` → Vercel; `api.vidyasetu.in` → Railway
- [ ] SSL certificates (auto via Vercel + Railway)
- [ ] Cloudflare R2 bucket created (production)
- [ ] Environment variables set in production (DB URL, JWT secret, WhatsApp token, Razorpay keys, Sentry DSN)
- [ ] UptimeRobot: monitor `https://api.vidyasetu.in/actuator/health` every 5 minutes
- [ ] Sentry alerts: email when error rate spikes
- [ ] Smoke test: register a test school, add 5 students, record a fee, generate receipt
- [ ] Privacy Policy published at `vidyasetu.in/privacy`
- [ ] Terms of Service published at `vidyasetu.in/terms`
- [ ] **🚀 Launch: onboard first 3 pilot schools (free trial)**

---

## 20. DEVELOPMENT ENVIRONMENT SETUP

### Prerequisites Checklist (Windows)
```
✅ Java 21 JDK — https://adoptium.net
✅ IntelliJ IDEA Community — https://jetbrains.com/idea
✅ VS Code — https://code.visualstudio.com
✅ Node.js 20 LTS — https://nodejs.org
✅ Git — https://git-scm.com
❌ Docker Desktop — https://docker.com/products/docker-desktop (INSTALL FIRST)
❌ pgAdmin 4 — https://pgadmin.org (install after Docker)
```

### Step 1: Start PostgreSQL via Docker
```bash
docker run --name vidyasetu-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=vidyasetu_dev \
  -e POSTGRES_DB=vidyasetu \
  -p 5432:5432 \
  -d postgres:16

# Verify running:
docker ps
```

### Step 2: Spring Boot Project Setup (start.spring.io)
```
Project: Maven
Language: Java
Spring Boot: 3.3.x
Group: in.vidyasetu
Artifact: vidyasetu-api
Java: 21
Packaging: JAR

Dependencies to select:
  - Spring Web
  - Spring Security
  - Spring Data JPA
  - PostgreSQL Driver
  - Lombok
  - Validation
  - Flyway Migration
  - Spring Cache (Caffeine)
  - Spring Actuator
```

### Step 3: pom.xml — Additional Dependencies
```xml
<!-- MapStruct for DTO mapping -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>

<!-- Bucket4j for rate limiting -->
<dependency>
    <groupId>com.giffing.bucket4j.spring.boot.starter</groupId>
    <artifactId>bucket4j-spring-boot-starter</artifactId>
    <version>0.10.0</version>
</dependency>

<!-- Caffeine cache -->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>

<!-- SpringDoc OpenAPI (Swagger) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>

<!-- iText 7 for PDF -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
    <type>pom</type>
</dependency>

<!-- AWS SDK for Cloudflare R2 (S3-compatible) -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.21.0</version>
</dependency>

<!-- Sentry -->
<dependency>
    <groupId>io.sentry</groupId>
    <artifactId>sentry-spring-boot-starter-jakarta</artifactId>
    <version>7.0.0</version>
</dependency>

<!-- Spring Retry -->
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>

<!-- Testcontainers for integration tests -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

### Step 4: application.properties (Complete)
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/vidyasetu
spring.datasource.username=postgres
spring.datasource.password=vidyasetu_dev
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

# JWT
jwt.access.secret=${JWT_ACCESS_SECRET}
jwt.access.expiration.ms=900000
jwt.refresh.secret=${JWT_REFRESH_SECRET}
jwt.refresh.expiration.ms=2592000000

# Cookie
cookie.secure=true
cookie.same-site=Strict
cookie.domain=vidyasetu.in

# WhatsApp
whatsapp.api.url=https://graph.facebook.com/v18.0
whatsapp.phone.number.id=${WHATSAPP_PHONE_NUMBER_ID}
whatsapp.access.token=${WHATSAPP_ACCESS_TOKEN}
whatsapp.webhook.verify.token=${WHATSAPP_WEBHOOK_VERIFY_TOKEN}

# SMS (Fast2SMS)
fast2sms.api.key=${FAST2SMS_API_KEY}

# Cloudflare R2
r2.bucket.name=vidyasetu-prod
r2.account.id=${CF_ACCOUNT_ID}
r2.access.key.id=${CF_R2_ACCESS_KEY_ID}
r2.secret.access.key=${CF_R2_SECRET_ACCESS_KEY}

# Razorpay
razorpay.key.id=${RAZORPAY_KEY_ID}
razorpay.key.secret=${RAZORPAY_KEY_SECRET}
razorpay.webhook.secret=${RAZORPAY_WEBHOOK_SECRET}

# Sentry
sentry.dsn=${SENTRY_DSN}
sentry.traces-sample-rate=0.1
sentry.environment=production

# Cache
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=300s

# File upload
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=10MB

# Actuator
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=never

# Logging
logging.level.in.vidyasetu=INFO
logging.level.org.springframework.security=WARN
logging.level.org.hibernate.SQL=WARN
```

### Step 5: React Frontend Setup
```bash
npm create vite@latest vidyasetu-frontend -- --template react
cd vidyasetu-frontend

# Core dependencies
npm install axios react-router-dom \
  @tanstack/react-query @tanstack/react-table \
  react-hook-form zod @hookform/resolvers \
  recharts date-fns sonner \
  lucide-react clsx tailwind-merge

# Tailwind
npm install -D tailwindcss postcss autoprefixer @vitejs/plugin-react
npx tailwindcss init -p

# shadcn/ui
npx shadcn-ui@latest init
# Choose: Default style, Slate color, CSS variables: yes

# Add frequently used shadcn components
npx shadcn-ui@latest add button card dialog sheet table \
  select dropdown-menu badge toast input label \
  skeleton alert-dialog date-picker
```

### Project Folder Structure

#### Backend
```
vidyasetu-api/
├── src/
│   ├── main/
│   │   ├── java/in/vidyasetu/
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── CacheConfig.java
│   │   │   │   ├── AsyncConfig.java
│   │   │   │   ├── AuditAspect.java         ← AOP for audit logging
│   │   │   │   └── TenantContext.java        ← ThreadLocal school_id
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── StudentController.java
│   │   │   │   ├── FeeController.java
│   │   │   │   ├── AttendanceController.java
│   │   │   │   ├── ExamController.java
│   │   │   │   ├── MessageController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   ├── ImportExportController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── BillingController.java
│   │   │   │   ├── SettingsController.java
│   │   │   │   └── AuditLogController.java
│   │   │   ├── service/
│   │   │   │   ├── StudentService.java
│   │   │   │   ├── FeeCalculationService.java  ← concession-aware
│   │   │   │   ├── FeePaymentService.java
│   │   │   │   ├── AttendanceService.java      ← holiday-aware
│   │   │   │   ├── ExamService.java
│   │   │   │   ├── WhatsAppService.java
│   │   │   │   ├── SmsService.java
│   │   │   │   ├── PdfReceiptService.java
│   │   │   │   ├── ReportCardPdfService.java
│   │   │   │   ├── ReportPdfService.java
│   │   │   │   ├── SchedulerService.java
│   │   │   │   ├── AuditService.java
│   │   │   │   ├── AppNotificationService.java
│   │   │   │   ├── BillingService.java
│   │   │   │   ├── ImportExportService.java
│   │   │   │   ├── R2StorageService.java
│   │   │   │   └── DashboardService.java
│   │   │   ├── repository/
│   │   │   │   └── [one interface per entity]
│   │   │   ├── entity/
│   │   │   │   └── [one class per table]
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   ├── mapper/
│   │   │   │   └── [MapStruct mappers]
│   │   │   ├── security/
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── JwtFilter.java
│   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── AccessDeniedException.java
│   │   │   │   └── BusinessRuleException.java
│   │   │   └── util/
│   │   │       ├── ReceiptNumberGenerator.java
│   │   │       ├── WorkingDayCalculator.java
│   │   │       └── EncryptionConverter.java  ← AES-256 for tokens
│   │   └── resources/
│   │       ├── db/migration/
│   │       │   ├── V1__init.sql
│   │       │   ├── V2__indexes.sql
│   │       │   └── V3__seed_data.sql
│   │       └── application.properties
│   └── test/
│       └── java/in/vidyasetu/
│           ├── service/
│           │   └── [unit tests]
│           └── integration/
│               └── [integration tests with Testcontainers]
└── Dockerfile
```

#### Frontend
```
vidyasetu-frontend/
├── src/
│   ├── pages/
│   │   ├── Login.jsx
│   │   ├── Dashboard.jsx
│   │   ├── students/  (StudentList, AddStudent, StudentProfile)
│   │   ├── fees/      (FeeOverview, RecordPayment, FeeStructure, Concessions)
│   │   ├── attendance/ (MarkAttendance, AttendanceReport)
│   │   ├── exams/     (ExamList, MarksEntry, ReportCards)
│   │   ├── reports/   (ReportsHub, individual report pages)
│   │   ├── messages/  (MessageLogs, Broadcast)
│   │   ├── staff/     (StaffList, AddStaff)
│   │   ├── AcademicYears.jsx
│   │   ├── Settings.jsx
│   │   ├── Billing.jsx
│   │   ├── AuditLogs.jsx
│   │   └── Notifications.jsx
│   ├── components/
│   │   ├── layout/    (Navbar, Sidebar, MobileNav)
│   │   ├── ui/        (shadcn/ui components)
│   │   ├── shared/    (SkeletonTable, EmptyState, ErrorState, OfflineBanner)
│   │   └── domain/    (StudentCard, FeeStatusBadge, AttendanceGrid, etc.)
│   ├── services/
│   │   ├── api.js          ← Axios instance with interceptors
│   │   ├── authService.js
│   │   ├── studentService.js
│   │   └── [one file per domain]
│   ├── hooks/
│   │   ├── useStudents.js  ← React Query hooks
│   │   ├── useFees.js
│   │   └── [one file per domain]
│   ├── context/
│   │   └── AuthContext.jsx
│   ├── i18n/
│   │   ├── hi.json    ← Hindi strings
│   │   └── en.json    ← English strings
│   └── utils/
│       ├── formatCurrency.js
│       ├── formatDate.js
│       └── validators.js
├── tailwind.config.js
└── vite.config.js
```

---

## 21. DEPLOYMENT & INFRASTRUCTURE

### Environments

| Environment | Backend | Frontend | Database |
|-------------|---------|----------|----------|
| Local (dev) | localhost:8080 | localhost:5173 | Docker PostgreSQL |
| Staging | staging-api.vidyasetu.in (Railway) | staging.vidyasetu.in (Vercel) | Neon (staging branch) |
| Production | api.vidyasetu.in (Railway) | vidyasetu.in (Vercel) | Neon (main branch) |

### CI/CD Pipeline (GitHub Actions)

```yaml
# Workflow: on every push to main branch

jobs:
  backend:
    - Checkout code
    - Setup Java 21
    - Run Maven: mvn test (unit + integration tests with Testcontainers)
    - If tests pass: Build JAR
    - Deploy to Railway (auto-deploy via GitHub integration)
    - Run DB migrations (Flyway runs on app startup)

  frontend:
    - Checkout code
    - Setup Node 20
    - npm ci
    - npm run build
    - Deploy to Vercel (auto-deploy via GitHub integration)
```

Staging deploys from `develop` branch.
Production deploys from `main` branch (requires PR + tests passing).

### Infrastructure Stack

```
Domain: GoDaddy → DNS → Cloudflare (CDN + DDoS protection)
Frontend: Vercel (global CDN, auto-SSL, free tier)
Backend: Railway (Docker container, auto-deploy, ~$15/month)
Database: Neon PostgreSQL (serverless, PITR, Mumbai region, ~$10/month)
Files: Cloudflare R2 (PDFs, photos, exports, ~$0.015/GB/month)
Cache: Caffeine (in-memory, built into Spring Boot — free)
Error tracking: Sentry (free tier: 5,000 errors/month)
Uptime: UptimeRobot (free: 50 monitors, 5-min interval)
```

### Dockerfile (Backend)
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/vidyasetu-api-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", \
  "-Xmx512m", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
```

### Production Checklist (Run Once Before Launch)
```
Infrastructure:
[ ] Neon PostgreSQL database created (Mumbai region)
[ ] Railway app created, connected to GitHub repo
[ ] Vercel project created, connected to GitHub repo
[ ] Cloudflare R2 bucket created (vidyasetu-prod)
[ ] Custom domain vidyasetu.in pointed to Vercel
[ ] Custom domain api.vidyasetu.in pointed to Railway
[ ] SSL verified on both domains

Environment variables set in Railway:
[ ] DATABASE_URL (Neon connection string)
[ ] JWT_ACCESS_SECRET (256-bit random)
[ ] JWT_REFRESH_SECRET (256-bit random)
[ ] WHATSAPP_PHONE_NUMBER_ID
[ ] WHATSAPP_ACCESS_TOKEN
[ ] WHATSAPP_WEBHOOK_VERIFY_TOKEN
[ ] FAST2SMS_API_KEY
[ ] CF_ACCOUNT_ID, CF_R2_ACCESS_KEY_ID, CF_R2_SECRET_ACCESS_KEY
[ ] RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET, RAZORPAY_WEBHOOK_SECRET
[ ] SENTRY_DSN

Monitoring:
[ ] UptimeRobot monitor: GET https://api.vidyasetu.in/actuator/health every 5 min
[ ] Sentry alert: email when error rate > 10 errors in 5 minutes
[ ] Neon: enable PITR (Point-in-Time Recovery)

Post-deploy smoke test:
[ ] Register test school
[ ] Add 3 students
[ ] Record a fee payment
[ ] Download PDF receipt
[ ] Send test WhatsApp message
[ ] Login as test teacher, mark attendance
[ ] View dashboard stats
[ ] Export student CSV
```

---

## 22. MARKETING & GO-TO-MARKET STRATEGY

### Phase 1: Customer Discovery (Before Building — Now)
Goal: Validate willingness to pay before writing code.

- [ ] Identify 10 school owners in Ballia via personal/community connections
- [ ] Delegate 5 in-person school visits to trusted local contact in Ballia
- [ ] Interview script (in Hindi):
  - "Aap abhi fees kaise track karte hain?"
  - "Month-end pe kitna time lagta hai fees ka hisaab karne mein?"
  - "Kitne parents ko manually WhatsApp karte ho?"
  - "Agar yeh sab automatic ho jaaye — ₹499/month mein — toh kya aap lenge?"
  - "Coaching center ke owners bhi same problem face karte hain — kya aapke koi contacts hain?"
- Success criteria: 5 out of 10 say "haan, lenge"

### Phase 2: Pilot Launch (After MVP — Month 7)
- [ ] First 10 schools/coaching centers: 3-month free trial
- [ ] Owner or local contact visits each school for in-person onboarding (school setup takes 30 minutes)
- [ ] WhatsApp group created with all pilot schools: "Vidyasetu Pilot Group"
- [ ] Weekly check-in message to pilot group
- [ ] Monthly structured feedback form (5 questions, Hindi, sent via WhatsApp)
- [ ] Track: which features are used daily, which are never touched

### Phase 3: Growth Marketing (Month 10+)

**Channel 1: WhatsApp Word-of-Mouth (₹0)**
- Every fee receipt footer: "Vidyasetu ke zariye generate hua | vidyasetu.in"
- Every WhatsApp message sent via Vidyasetu ends with: "— Sent via Vidyasetu"
- Referral program: refer 1 paying school/coaching center → get 1 month free
- Goal: net promoter score from pilot schools → each school refers 2 more

**Channel 2: Local Newspaper (₹5,000–8,000/insertion)**
- Dainik Jagran Ballia edition + Amar Ujala Ballia edition
- Ad copy: "Ballia ke schools aur coaching centers ke liye — digital fee register. ₹499/month. Shuru karo free mein. vidyasetu.in"
- Run: 2 insertions per month for first 3 months post-launch

**Channel 3: Stationery Shop & Printing Network (₹200/referral)**
- Partner with 5 stationery shops near schools in Ballia
- Display A4 pamphlet in store
- Shopkeeper gets ₹200 per converted school
- Target: places where school owners already go to buy registers and notebooks

**Channel 4: Teacher WhatsApp Groups (₹0)**
- Teachers have active WhatsApp groups (Ballia Teachers Group, etc.)
- Share 2-minute Hindi demo screen recording: "Yeh school ka fee register digital kar diya"
- Target teacher groups — teachers influence owners and sometimes manage fees themselves

**Channel 5: YouTube + Instagram (Month 12+)**
- Hindi short-form videos: real product demo, not animation
- "School fees register → Vidyasetu mein migrate kiya" (before/after)
- Keyword target: "school management software Hindi", "fees management app"
- Budget: ₹0 (organic only until 100 schools)

### Pricing Psychology
- Always lead with FREE tier: "Shuru karo free mein — koi credit card nahin chahiye"
- Never say "software" — say "digital register" or "smart register"
- Price anchor: "Jitne mein ek saal ke register kharidoge, utne mein poora school digital ho jaata hai"
- Show monthly equivalent: "₹499/month — ek din ka ₹16.60"

### Feedback Loop (Ongoing)
- Monthly NPS WhatsApp survey (single question + optional comment)
- "Which 3 features would you miss most if Vidyasetu disappeared?" — tracks stickiness
- Exit interview when a school cancels: one WhatsApp message + optional 10-min call
- Win-back: 3 months after cancellation, WhatsApp with new features added since they left
- Product analytics: track API endpoint call counts per school (proxy for feature usage)

---

## 23. NON-FUNCTIONAL REQUIREMENTS

### Performance
- API response time: P95 < 500ms for all read endpoints; P95 < 800ms for write endpoints
- Dashboard load: < 2 seconds on 4G mobile (with Caffeine cache hit: < 200ms)
- PDF generation: < 3 seconds per receipt; < 30 seconds for 300-student report card batch
- Maximum students per school: 1,000 (beyond this, requires query optimization review)
- Concurrent schools: application must handle 200 concurrent active schools without degradation
- Database queries: all list endpoints paginated (default: 20 rows/page, max: 100)

### Availability
- Target uptime: 99.5% monthly (allows ~3.6 hours downtime/month)
- No scheduled maintenance during school hours: 8am–4pm IST, Monday–Saturday
- Graceful degradation: if WhatsApp API is down, queue messages + notify owner in-app
- Graceful degradation: if Cloudflare R2 is slow, serve receipt download links with retry
- Health endpoint: `GET /actuator/health` — returns 200 when DB is reachable

### Security
- Passwords: BCrypt strength 12
- JWT: short-lived access (15 min) + long-lived refresh (30 days), both HttpOnly cookies
- All write endpoints: CSRF protection via SameSite=Strict cookie + double-submit cookie pattern
- Rate limiting: Bucket4j on all endpoints (see Section 9)
- Sensitive fields: `whatsapp_access_token` AES-256 encrypted at rest
- No PII stored in logs or error messages
- HTTPS enforced everywhere; HTTP → HTTPS redirect
- Audit logs: `INSERT` only at database user permission level

### Scalability
- Multi-tenant row-level isolation supports 10,000+ schools without re-architecture
- Stateless Spring Boot backend: horizontally scalable behind Railway load balancer
- HikariCP connection pool: max 10 connections (scales to 20 on Railway Pro plan)
- Caffeine → Upstash Redis upgrade path when school count > 500

### Accessibility & Usability
- Touch targets: minimum 44px height on all interactive elements
- Color contrast: WCAG AA (4.5:1 for body text, 3:1 for large text)
- Screen width: fully functional at 360px (minimum modern Android)
- Slow network: API responses < 100KB payload on list endpoints
- Offline: show cached data with offline banner; attendance can be viewed (not marked) offline

### Localization
- Hindi and English from Day 1
- Date format: DD/MM/YYYY
- Currency: ₹ with Indian comma formatting (₹1,00,000)
- Numbers: English numerals only (no Devanagari digits)
- Time: 12-hour format (9:30 AM, not 09:30)

### Data Retention
- Active schools: data retained indefinitely while subscription active
- Cancelled schools: data retained 90 days post-cancellation (exportable)
- After 90 days: student names anonymized, financial totals kept (7-year Indian legal requirement for financial records)
- Audit logs: never deleted — retained permanently

### Input Validation Rules
- Phone numbers: exactly 10 digits, starts with 6, 7, 8, or 9 (Indian mobile)
- Fee amounts: positive, ≤ ₹1,00,000 per payment
- Dates: attendance dates — not future; payment dates — not more than 1 year in past (unless ROLE_OWNER)
- Names: 2–255 characters, no `<script>` or SQL injection patterns (strip HTML on save)
- File uploads: JPEG/PNG only for photos (max 5MB); CSV/XLSX only for imports (max 5MB)
- Marks: 0 ≤ marks_obtained ≤ max_marks

---

## 24. LEGAL & COMPLIANCE

### GST (Goods & Services Tax)
- **Threshold:** Register for GST when annual revenue exceeds ₹20 lakh
- **Rate:** 18% GST on SaaS subscriptions
- **HSN Code:** 998314 (data processing, hosting, and related services)
- **Invoicing:** Auto-generate GST invoice for every subscription payment
- **Filing:** Monthly GSTR-1, quarterly GSTR-3B
- **Impact on pricing:** ₹499 + ₹89.82 GST = ₹588.82 total billed; or price inclusive and declare ₹423 as taxable value

### Privacy & Data Protection
**DPDP Act 2023 (India):** Digital Personal Data Protection Act — India's new data law
- Student data (minors) has heightened protection requirements
- Parental consent should be obtained by the school (not Vidyasetu's responsibility, but note in ToS)
- Data localisation: host in Mumbai region (Neon has Mumbai)
- Data deletion: must honor deletion requests within 30 days

**Privacy Policy (required for any web/app product):**
Must cover:
- What data is collected (student names, phone numbers, fee records, attendance)
- Why it is collected (school management purposes)
- Who it is shared with (Meta/WhatsApp for message delivery; Razorpay for payments)
- How long it is kept (see data retention policy above)
- How to request deletion
- Contact email for privacy concerns
- Generate at: privacypolicygenerator.info → customize for Indian law

**Terms of Service:**
Must cover:
- Subscription terms, payment, refund policy
- Data ownership: "All school data belongs to the school"
- Acceptable use (no illegal activity, no spam)
- Liability limitation
- Dispute resolution (Indian jurisdiction, UP courts)
- Use IndiaFilings or Vakilsearch for Indian SaaS ToS template

### WhatsApp Compliance
- All WhatsApp messages must use Meta-approved templates for promotional/transactional messages
- Users must have opt-out mechanism (reply STOP) — implemented in `parents.whatsapp_opt_out`
- Cannot send marketing messages to users who have not interacted with the business in 24 hours (Meta policy)
- Apply for templates early — can take 5–14 business days for approval

### SMS Compliance (TRAI DLT)
- All SMS senders in India must register on TRAI's Distributed Ledger Technology (DLT) platform
- Register with: Airtel, Jio, or Vi DLT portal (any one covers all)
- Register: Sender ID (e.g., VDYSTU) + all SMS templates
- Approval: 7–14 working days
- Without DLT registration, SMS are blocked by all Indian telecom operators

### Business Registration
- For collecting payments professionally: register as proprietorship (simplest) or private limited company
- Proprietorship: PAN card + current bank account in business name = functional
- Private limited: required if you raise investment; use Razorpay Atlas (free company formation)
- Razorpay account required for online payments: needs GSTIN or business registration

---

## 25. RISK REGISTER

| # | Risk | Probability | Impact | Mitigation |
|---|------|-------------|--------|------------|
| 1 | WhatsApp API approval delayed >4 weeks | Medium | High | Apply Week 0. Build MVP without WhatsApp first. SMS works from Day 1 (TRAI DLT registered separately). |
| 2 | WhatsApp Business account banned (spam reports) | Low | Critical | Implement opt-out (reply STOP). Limit 3 reminders/student/month. SMS always as fallback. |
| 3 | WhatsApp template rejected by Meta | Medium | Medium | Submit all 4 templates in Week 0. Have SMS fallback. Rephrase and resubmit within 48 hours. |
| 4 | TRAI DLT registration delayed | Medium | Low | Apply Week 0 (same time as WhatsApp). Without DLT, SMS is blocked — WhatsApp becomes primary. |
| 5 | Schools unwilling to pay ₹499 | Low | High | Customer discovery interviews before building. Pilot free trial validates willingness to pay. |
| 6 | ClassPlus drops price to ₹499 | Low | High | Local trust (Ballia) is defensible. Hindi-first is hard to copy fast. Add coaching center segment where ClassPlus is weaker on simplicity. |
| 7 | Solo founder lands job — Vidyasetu deprioritized | Medium | High | Build automation-first. Document everything. Set up CI/CD so deployment is one click. Even part-time maintenance is viable once schools pay. |
| 8 | Fee calculation bug — shows wrong defaulters | High | High | Unit test every calculation variant (concession, partial, annual, quarterly). Integration test full flow. Manual QA on staging before launch. |
| 9 | JWT token vulnerability exploited | Low | Critical | HttpOnly cookies (not localStorage). Short 15-minute access token expiry. Rate limiting on auth endpoints. |
| 10 | Database query performance at scale | Medium | Medium | Caffeine cache for aggregations. Proper indexes on all WHERE columns. Test with 500-student school before launch. |
| 11 | Teacher misappropriates fee collection | Medium | High | Audit log captures every payment with collector's name and device. Receipt sent to parent immediately — creates accountability. Void requires ROLE_OWNER. |
| 12 | School owner loses phone / locked out | High | Low | OTP-based password reset. Secondary admin account. School owner can contact support for manual recovery. |
| 13 | Student data breach (school data leaked) | Low | Critical | Row-level tenant isolation. HTTPS everywhere. Input sanitization. No PII in logs. Regular security audit. |
| 14 | Hosting cost spike at 1,000 schools | Low | Medium | Monitor Railway + Neon costs monthly. Railway auto-scales. Neon scales per compute second. Migrate to AWS Mumbai if costs exceed 15% of revenue. |
| 15 | Academic year management not built → app breaks April | High | Critical | Academic year tables in schema from Week 2. Year management UI in Phase 0 Week 4. Cannot go live without this. |
| 16 | Internet connectivity issues in small towns | High | Medium | Skeleton loaders + cached data on slow connections. Offline indicator banner. PWA installable (v2.0). Attendance offline sync (v1.3). |

---

## 26. SUPPORT & OPERATIONS

### Support Channels
- **Primary:** Dedicated WhatsApp number (not personal number) — 9am–6pm IST, Mon–Sat
- **Secondary:** Email: support@vidyasetu.in (respond within 24 hours)
- **Self-service:** Hindi FAQ page at vidyasetu.in/help (launch with top 20 questions)
- **Emergency:** If production is down during school hours, respond within 1 hour

### Common Support Scenarios
| Issue | Resolution |
|-------|-----------|
| Forgot password | Send OTP to registered number via in-app + support |
| WhatsApp not sending | Check in Settings → WhatsApp → Test connection. Check if template approved. |
| Wrong fee recorded | Owner can void payment via Fee History. Add new correct payment. Audit log tracks both. |
| Student shows as defaulter despite paying | Check: was concession set? Was payment for correct month? Was payment voided accidentally? |
| Want to migrate from Excel | Use CSV import feature. Support can help map columns. |
| Lost access (owner changed phone) | Identity verification via registered email + business address. Manual account recovery process. |

### Incident Response
- **Detection:** Sentry alert + UptimeRobot down notification
- **Triage:** Check Railway logs → Neon DB status → Sentry error details
- **Communication:** WhatsApp broadcast to affected schools if outage > 30 minutes
- **Fix:** Hotfix branch → CI/CD deploy → verify with smoke test → mark resolved
- **Post-mortem:** Document root cause + prevention steps for every P0/P1 incident

---

## 27. GLOSSARY

| Term | Meaning in Vidyasetu Context |
|------|------------------------------|
| MRR | Monthly Recurring Revenue — total subscription revenue collected per month |
| ARR | Annual Recurring Revenue — MRR × 12 |
| Churn | Schools that cancel subscription in a given period |
| LTV | Lifetime Value — total revenue from one school over its entire subscription |
| CAC | Customer Acquisition Cost — total marketing + sales cost to sign up one school |
| ARPU | Average Revenue Per User — MRR / total paying schools |
| Net Revenue Expansion | Revenue growth from existing customers (upgrades + add-ons) minus churn |
| Multi-tenant | One application instance serving many schools, each isolated |
| Row-level isolation | Tenant separation via `school_id` on every DB table |
| SaaS | Software as a Service — subscription-based cloud software |
| Freemium | Free basic tier that converts to paid for more features |
| Add-on | Paid feature module on top of base subscription |
| Fee concession | Discount given to a specific student for a specific fee type |
| Defaulter | Student with outstanding fee balance (concession-aware — not shown if concession covers full amount) |
| Academic year | April 1 – March 31 session for UP schools |
| Working day | Calendar day excluding Sundays, weekly offs, and school holidays |
| Bulk promotion | Year-end process of moving all students to their next class |
| JWT | JSON Web Token — cryptographically signed authentication credential |
| HttpOnly cookie | Browser cookie inaccessible to JavaScript — protects JWT from XSS |
| RBAC | Role-Based Access Control — access rights based on OWNER/ADMIN/TEACHER role |
| Async job | Background operation that doesn't block the HTTP response |
| Caffeine | In-memory Java cache — stores frequently computed data for fast access |
| Flyway | Database migration tool — applies versioned SQL scripts on startup |
| iText 7 | Java library for programmatic PDF generation (Community Edition is free) |
| HMAC-SHA256 | Cryptographic signature algorithm — used to verify WhatsApp webhooks |
| DLT (TRAI) | Distributed Ledger Technology — Indian SMS registration platform |
| DPDP Act | Digital Personal Data Protection Act 2023 — India's data privacy law |
| GST | Goods and Services Tax — 18% applicable on SaaS subscriptions in India |
| HSN 998314 | GST classification code for data processing and hosting services (SaaS) |
| PITR | Point-In-Time Recovery — Neon PostgreSQL feature for restoring to any past moment |
| RTO | Recovery Time Objective — maximum acceptable downtime (target: 4 hours) |
| RPO | Recovery Point Objective — maximum acceptable data loss (target: 24 hours) |
| Institution type | Flag distinguishing School vs Coaching Center — changes UI labels only |
| WhatsApp template | Pre-approved message format required by Meta for automated messages |
| Opt-out | Parent who replied STOP — never receives automated WhatsApp messages |

---

## 🚀 IMMEDIATE NEXT ACTIONS

Before writing a single line of code, complete these in Week 0:

```
Priority 1 — Apply for WhatsApp Business API (takes 2–4 weeks approval)
  → business.facebook.com → Create App → WhatsApp → Register number
  → Submit 4 templates: fee_reminder, fee_receipt, absent_notification, broadcast

Priority 2 — Register on TRAI DLT for SMS (takes 7–14 days)
  → Register at DLT portal (Airtel/Jio/Vi)
  → Register Sender ID: VDYSTU
  → Register SMS templates

Priority 3 — Install Docker Desktop and start PostgreSQL
  → docker run --name vidyasetu-db -e POSTGRES_PASSWORD=vidyasetu_dev ...

Priority 4 — Create GitHub repos and start Spring Boot project
  → vidyasetu-backend (Spring Boot 3.3, Java 21)
  → vidyasetu-frontend (Vite + React + Tailwind)

Priority 5 — Run the complete SQL schema (Section 10) in pgAdmin
  → All 22 tables created and verified before any Java code is written
```

---

*Master Plan v2.0 | June 2026 | Incorporating all 65 audit findings*
*Sections: 27 | Tables: 22 | API endpoints: 80+ | Build phases: 6 (28 weeks)*
