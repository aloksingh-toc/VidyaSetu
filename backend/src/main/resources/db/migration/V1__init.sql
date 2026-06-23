-- ============================================================
-- VidyaSetu — V1__init.sql
-- All 22 tables in dependency order
-- ============================================================

-- ── TABLE 1: SCHOOLS ──────────────────────────────────────────
CREATE TABLE schools (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                     VARCHAR(255) NOT NULL,
    institution_type         VARCHAR(20)  DEFAULT 'SCHOOL',        -- SCHOOL, COACHING_CENTER
    address                  TEXT,
    city                     VARCHAR(100),
    state                    VARCHAR(100) DEFAULT 'Uttar Pradesh',
    phone                    VARCHAR(15),
    email                    VARCHAR(255) UNIQUE,
    logo_url                 VARCHAR(500),
    plan_type                VARCHAR(50)  DEFAULT 'FREE',           -- FREE, STARTER, BASIC, STANDARD, PRO, COMPLETE, TRUST
    plan_expires_at          TIMESTAMP,
    weekly_off_days          VARCHAR(30)  DEFAULT 'SUNDAY',        -- SUNDAY, SATURDAY_SUNDAY, NONE
    grading_scale            VARCHAR(20)  DEFAULT 'STANDARD',      -- STANDARD, CGPA, PERCENTAGE_ONLY
    language_preference      VARCHAR(10)  DEFAULT 'hi',             -- hi, en
    whatsapp_phone_number_id VARCHAR(100),
    whatsapp_access_token    TEXT,                                  -- AES-256 encrypted at rest
    whatsapp_enabled         BOOLEAN      DEFAULT FALSE,
    sms_enabled              BOOLEAN      DEFAULT TRUE,
    late_fee_enabled         BOOLEAN      DEFAULT FALSE,
    late_fee_amount          DECIMAL(10, 2),
    fee_due_day              INTEGER      DEFAULT 10,               -- day of month fees are due
    support_phone            VARCHAR(15),
    gstin                    VARCHAR(20),
    is_active                BOOLEAN      DEFAULT TRUE,
    created_at               TIMESTAMP    DEFAULT NOW(),
    updated_at               TIMESTAMP    DEFAULT NOW()
);

-- ── TABLE 2: USERS ────────────────────────────────────────────
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID         REFERENCES schools(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(15)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(20)  NOT NULL,    -- OWNER, ADMIN, TEACHER
    is_active       BOOLEAN      DEFAULT TRUE,
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP    DEFAULT NOW()
);

CREATE INDEX idx_users_school_id ON users(school_id);
CREATE INDEX idx_users_phone     ON users(phone);

-- ── TABLE 3: ACADEMIC YEARS ───────────────────────────────────
CREATE TABLE academic_years (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id   UUID         REFERENCES schools(id) ON DELETE CASCADE,
    name        VARCHAR(20)  NOT NULL,   -- "2025-2026"
    start_date  DATE         NOT NULL,   -- 2025-04-01
    end_date    DATE         NOT NULL,   -- 2026-03-31
    is_current  BOOLEAN      DEFAULT FALSE,
    is_archived BOOLEAN      DEFAULT FALSE,
    created_at  TIMESTAMP    DEFAULT NOW(),
    UNIQUE (school_id, name)
);

CREATE INDEX idx_academic_years_school ON academic_years(school_id);

-- ── TABLE 4: CLASSES ──────────────────────────────────────────
CREATE TABLE classes (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID         REFERENCES schools(id) ON DELETE CASCADE,
    academic_year_id UUID         REFERENCES academic_years(id),
    name             VARCHAR(100) NOT NULL,   -- "Class 5", "Nursery", "Batch A"
    section          VARCHAR(10),             -- "A", "B", "C"
    class_teacher_id UUID         REFERENCES users(id),
    display_order    INTEGER      DEFAULT 0,
    created_at       TIMESTAMP    DEFAULT NOW(),
    UNIQUE (school_id, academic_year_id, name, section)
);

CREATE INDEX idx_classes_school_year ON classes(school_id, academic_year_id);

-- ── TABLE 5: STUDENTS ─────────────────────────────────────────
CREATE TABLE students (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID         REFERENCES schools(id) ON DELETE CASCADE,
    class_id         UUID         REFERENCES classes(id),
    roll_number      VARCHAR(20),
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100),
    date_of_birth    DATE,
    gender           VARCHAR(10),              -- MALE, FEMALE, OTHER
    photo_url        VARCHAR(500),
    admission_date   DATE         DEFAULT CURRENT_DATE,
    admission_number VARCHAR(50),
    blood_group      VARCHAR(5),
    address          TEXT,
    is_active        BOOLEAN      DEFAULT TRUE,
    created_at       TIMESTAMP    DEFAULT NOW(),
    updated_at       TIMESTAMP    DEFAULT NOW()
);

CREATE INDEX idx_students_school_id ON students(school_id);
CREATE INDEX idx_students_class_id  ON students(class_id);
CREATE INDEX idx_students_name      ON students(school_id, first_name, last_name);

-- ── TABLE 6: PARENTS ──────────────────────────────────────────
CREATE TABLE parents (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID         REFERENCES schools(id) ON DELETE CASCADE,
    student_id       UUID         REFERENCES students(id) ON DELETE CASCADE,
    name             VARCHAR(255) NOT NULL,
    relation         VARCHAR(30),    -- FATHER, MOTHER, GUARDIAN
    phone            VARCHAR(15)  NOT NULL,
    whatsapp_number  VARCHAR(15),    -- may differ from phone
    is_primary       BOOLEAN      DEFAULT FALSE,
    whatsapp_opt_out BOOLEAN      DEFAULT FALSE,
    created_at       TIMESTAMP    DEFAULT NOW()
);

CREATE INDEX idx_parents_student_id ON parents(student_id);
CREATE INDEX idx_parents_school_id  ON parents(school_id);

-- ── TABLE 7: HOLIDAYS ─────────────────────────────────────────
CREATE TABLE holidays (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID         REFERENCES schools(id) ON DELETE CASCADE,
    academic_year_id UUID         REFERENCES academic_years(id),
    date             DATE         NOT NULL,
    name             VARCHAR(100),   -- "Diwali", "Republic Day"
    type             VARCHAR(20),    -- PUBLIC, SCHOOL, HALF_DAY
    created_at       TIMESTAMP    DEFAULT NOW(),
    UNIQUE (school_id, date)
);

CREATE INDEX idx_holidays_school_year ON holidays(school_id, academic_year_id);

-- ── TABLE 8: FEE TYPES ────────────────────────────────────────
CREATE TABLE fee_types (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id   UUID         REFERENCES schools(id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,   -- "Tuition Fee", "Transport Fee"
    description TEXT,
    is_active   BOOLEAN      DEFAULT TRUE,
    created_at  TIMESTAMP    DEFAULT NOW()
);

CREATE INDEX idx_fee_types_school ON fee_types(school_id);

-- ── TABLE 9: FEE STRUCTURES ───────────────────────────────────
CREATE TABLE fee_structures (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID           REFERENCES schools(id) ON DELETE CASCADE,
    class_id         UUID           REFERENCES classes(id),
    fee_type_id      UUID           REFERENCES fee_types(id),
    academic_year_id UUID           REFERENCES academic_years(id),
    amount           DECIMAL(10, 2) NOT NULL,
    frequency        VARCHAR(20)    NOT NULL,   -- MONTHLY, QUARTERLY, ANNUAL, ONE_TIME
    due_day          INTEGER,
    created_at       TIMESTAMP      DEFAULT NOW(),
    UNIQUE (class_id, fee_type_id, academic_year_id)
);

CREATE INDEX idx_fee_structures_class ON fee_structures(class_id);

-- ── TABLE 10: FEE CONCESSIONS ─────────────────────────────────
CREATE TABLE fee_concessions (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID           REFERENCES schools(id) ON DELETE CASCADE,
    student_id       UUID           REFERENCES students(id) ON DELETE CASCADE,
    fee_type_id      UUID           REFERENCES fee_types(id),
    academic_year_id UUID           REFERENCES academic_years(id),
    concession_type  VARCHAR(20)    NOT NULL,    -- PERCENTAGE, FIXED_AMOUNT, FULL_WAIVER
    concession_value DECIMAL(10, 2),
    reason           VARCHAR(200),
    approved_by      UUID           REFERENCES users(id),
    is_active        BOOLEAN        DEFAULT TRUE,
    created_at       TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_fee_concessions_student ON fee_concessions(student_id, academic_year_id);

-- ── TABLE 11: FEE PAYMENTS ────────────────────────────────────
CREATE TABLE fee_payments (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID           REFERENCES schools(id) ON DELETE CASCADE,
    student_id       UUID           REFERENCES students(id),
    fee_type_id      UUID           REFERENCES fee_types(id),
    academic_year_id UUID           REFERENCES academic_years(id),
    amount_paid      DECIMAL(10, 2) NOT NULL,
    amount_due       DECIMAL(10, 2) NOT NULL,
    amount_waived    DECIMAL(10, 2) DEFAULT 0,
    payment_method   VARCHAR(20)    NOT NULL,    -- CASH, UPI, CHEQUE, ONLINE
    payment_date     DATE           NOT NULL DEFAULT CURRENT_DATE,
    for_month        VARCHAR(7),                 -- "2025-06" for monthly fees
    receipt_number   VARCHAR(50)    UNIQUE,
    receipt_url      VARCHAR(500),
    status           VARCHAR(20)    DEFAULT 'ACTIVE',   -- ACTIVE, VOIDED
    void_reason      TEXT,
    voided_by        UUID           REFERENCES users(id),
    voided_at        TIMESTAMP,
    collected_by     UUID           REFERENCES users(id),
    notes            TEXT,
    transaction_ref  VARCHAR(100),
    created_at       TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_fee_payments_school_student ON fee_payments(school_id, student_id);
CREATE INDEX idx_fee_payments_date           ON fee_payments(payment_date);
CREATE INDEX idx_fee_payments_academic_year  ON fee_payments(academic_year_id);
CREATE INDEX idx_fee_payments_status         ON fee_payments(school_id, status);

-- ── TABLE 12: ATTENDANCE ──────────────────────────────────────
CREATE TABLE attendance (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id            UUID      REFERENCES schools(id) ON DELETE CASCADE,
    student_id           UUID      REFERENCES students(id),
    class_id             UUID      REFERENCES classes(id),
    academic_year_id     UUID      REFERENCES academic_years(id),
    date                 DATE      NOT NULL,
    status               VARCHAR(10) NOT NULL,   -- PRESENT, ABSENT, LATE, LEAVE
    marked_by            UUID      REFERENCES users(id),
    notification_sent    BOOLEAN   DEFAULT FALSE,
    notification_sent_at TIMESTAMP,
    created_at           TIMESTAMP DEFAULT NOW(),
    UNIQUE (student_id, date)
);

CREATE INDEX idx_attendance_school_date  ON attendance(school_id, date);
CREATE INDEX idx_attendance_student_date ON attendance(student_id, date);
CREATE INDEX idx_attendance_class_date   ON attendance(class_id, date);

-- ── TABLE 13: MESSAGE LOGS ────────────────────────────────────
CREATE TABLE message_logs (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id            UUID        REFERENCES schools(id) ON DELETE CASCADE,
    student_id           UUID        REFERENCES students(id),
    recipient_phone      VARCHAR(15) NOT NULL,
    channel              VARCHAR(10) DEFAULT 'WHATSAPP',   -- WHATSAPP, SMS
    message_type         VARCHAR(50),   -- FEE_REMINDER, RECEIPT, ABSENT_NOTIF, BROADCAST, REPORT_CARD
    template_name        VARCHAR(100),
    message_body         TEXT,
    status               VARCHAR(20) DEFAULT 'PENDING',    -- PENDING, SENT, DELIVERED, READ, FAILED
    whatsapp_message_id  VARCHAR(200),
    failure_reason       TEXT,
    sent_at              TIMESTAMP,
    delivered_at         TIMESTAMP,
    read_at              TIMESTAMP,
    created_at           TIMESTAMP   DEFAULT NOW()
);

CREATE INDEX idx_message_logs_school_status ON message_logs(school_id, status);
CREATE INDEX idx_message_logs_student       ON message_logs(student_id);

-- ── TABLE 14: EXAMS ───────────────────────────────────────────
CREATE TABLE exams (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID         REFERENCES schools(id) ON DELETE CASCADE,
    academic_year_id UUID         REFERENCES academic_years(id),
    name             VARCHAR(100) NOT NULL,   -- "Half Yearly 2025"
    exam_type        VARCHAR(30),             -- UNIT_TEST, HALF_YEARLY, ANNUAL, PRACTICAL, INTERNAL
    start_date       DATE,
    end_date         DATE,
    result_published BOOLEAN   DEFAULT FALSE,
    published_at     TIMESTAMP,
    created_at       TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_exams_school_year ON exams(school_id, academic_year_id);

-- ── TABLE 15: EXAM SUBJECTS ───────────────────────────────────
CREATE TABLE exam_subjects (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exam_id       UUID           REFERENCES exams(id) ON DELETE CASCADE,
    class_id      UUID           REFERENCES classes(id),
    subject       VARCHAR(100)   NOT NULL,
    max_marks     DECIMAL(5, 2)  NOT NULL,
    passing_marks DECIMAL(5, 2),
    exam_date     DATE,
    created_at    TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_exam_subjects_exam ON exam_subjects(exam_id);

-- ── TABLE 16: EXAM MARKS ──────────────────────────────────────
CREATE TABLE exam_marks (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID          REFERENCES schools(id) ON DELETE CASCADE,
    student_id      UUID          REFERENCES students(id),
    exam_subject_id UUID          REFERENCES exam_subjects(id),
    marks_obtained  DECIMAL(5, 2),
    is_absent       BOOLEAN       DEFAULT FALSE,
    remarks         VARCHAR(200),
    entered_by      UUID          REFERENCES users(id),
    created_at      TIMESTAMP     DEFAULT NOW(),
    updated_at      TIMESTAMP     DEFAULT NOW(),
    UNIQUE (student_id, exam_subject_id)
);

CREATE INDEX idx_exam_marks_student ON exam_marks(student_id);

-- ── TABLE 17: EXPENSES ────────────────────────────────────────
CREATE TABLE expenses (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID           REFERENCES schools(id) ON DELETE CASCADE,
    academic_year_id UUID           REFERENCES academic_years(id),
    category         VARCHAR(50),    -- SALARY, UTILITIES, MAINTENANCE, SUPPLIES, RENT, OTHER
    description      VARCHAR(255),
    amount           DECIMAL(10, 2) NOT NULL,
    expense_date     DATE           NOT NULL DEFAULT CURRENT_DATE,
    payment_method   VARCHAR(20),
    recorded_by      UUID           REFERENCES users(id),
    receipt_url      VARCHAR(500),
    created_at       TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_expenses_school_date ON expenses(school_id, expense_date);

-- ── TABLE 18: SUBSCRIPTIONS ───────────────────────────────────
CREATE TABLE subscriptions (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id         UUID           REFERENCES schools(id) ON DELETE CASCADE,
    plan_type         VARCHAR(50)    NOT NULL,
    billing_cycle     VARCHAR(10)    NOT NULL,        -- MONTHLY, ANNUAL
    base_amount       DECIMAL(10, 2) NOT NULL,
    gst_amount        DECIMAL(10, 2) NOT NULL,
    total_amount      DECIMAL(10, 2) NOT NULL,
    currency          VARCHAR(5)     DEFAULT 'INR',
    starts_at         TIMESTAMP      NOT NULL,
    ends_at           TIMESTAMP,
    next_billing_date DATE,
    auto_renew        BOOLEAN        DEFAULT TRUE,
    payment_method    VARCHAR(30),
    payment_reference VARCHAR(200),
    razorpay_sub_id   VARCHAR(100),
    invoice_number    VARCHAR(50)    UNIQUE,
    invoice_url       VARCHAR(500),
    status            VARCHAR(20)    DEFAULT 'ACTIVE',   -- ACTIVE, EXPIRED, CANCELLED, FAILED
    failure_reason    TEXT,
    created_at        TIMESTAMP      DEFAULT NOW()
);

CREATE INDEX idx_subscriptions_school ON subscriptions(school_id);

-- ── TABLE 19: STUDENT PROMOTIONS ──────────────────────────────
CREATE TABLE student_promotions (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id             UUID      REFERENCES schools(id) ON DELETE CASCADE,
    student_id            UUID      REFERENCES students(id),
    from_class_id         UUID      REFERENCES classes(id),
    to_class_id           UUID      REFERENCES classes(id),
    from_academic_year_id UUID      REFERENCES academic_years(id),
    to_academic_year_id   UUID      REFERENCES academic_years(id),
    status                VARCHAR(20),   -- PROMOTED, DETAINED, LEFT_SCHOOL, PASSED_OUT, TRANSFERRED
    remarks               TEXT,
    promoted_by           UUID      REFERENCES users(id),
    promoted_at           TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_student_promotions_student ON student_promotions(student_id);

-- ── TABLE 20: AUDIT LOGS (IMMUTABLE) ─────────────────────────
CREATE TABLE audit_logs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id   UUID         REFERENCES schools(id) ON DELETE SET NULL,
    user_id     UUID         REFERENCES users(id)   ON DELETE SET NULL,
    user_name   VARCHAR(255),           -- denormalized — preserved even if user is deleted
    action      VARCHAR(100) NOT NULL,  -- FEE_COLLECTED, FEE_VOIDED, STUDENT_ADDED, etc.
    entity_type VARCHAR(50),            -- FeePayment, Student, FeeStructure, User
    entity_id   UUID,
    old_value   JSONB,
    new_value   JSONB,
    ip_address  VARCHAR(45),
    user_agent  TEXT,
    created_at  TIMESTAMP    DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_school ON audit_logs(school_id, created_at DESC);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
-- NOTE: Application DB user must have INSERT only on this table. No UPDATE or DELETE ever.

-- ── TABLE 21: NOTIFICATION JOBS ───────────────────────────────
CREATE TABLE notification_jobs (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id    UUID      REFERENCES schools(id) ON DELETE CASCADE,
    job_type     VARCHAR(50),    -- BULK_REMINDER, PDF_REPORT, CSV_IMPORT, BULK_MARKS
    status       VARCHAR(20) DEFAULT 'PENDING',   -- PENDING, RUNNING, DONE, FAILED
    total        INTEGER     DEFAULT 0,
    processed    INTEGER     DEFAULT 0,
    failed       INTEGER     DEFAULT 0,
    error_log    TEXT,
    result_url   VARCHAR(500),
    created_by   UUID        REFERENCES users(id),
    started_at   TIMESTAMP,
    completed_at TIMESTAMP,
    created_at   TIMESTAMP   DEFAULT NOW()
);

CREATE INDEX idx_notif_jobs_school ON notification_jobs(school_id, created_at DESC);

-- ── TABLE 22: APP NOTIFICATIONS ───────────────────────────────
CREATE TABLE app_notifications (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id  UUID      REFERENCES schools(id) ON DELETE CASCADE,
    user_id    UUID      REFERENCES users(id)   ON DELETE CASCADE,
    type       VARCHAR(50),   -- FEE_OVERDUE_ALERT, PLAN_EXPIRY, ATTENDANCE_MISSING, etc.
    title      VARCHAR(255),
    body       TEXT,
    action_url VARCHAR(255),
    is_read    BOOLEAN   DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_app_notifications_user ON app_notifications(user_id, is_read, created_at DESC);
