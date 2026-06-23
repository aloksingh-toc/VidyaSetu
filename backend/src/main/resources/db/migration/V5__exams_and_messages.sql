-- Exams
CREATE TABLE IF NOT EXISTS exams (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID        NOT NULL REFERENCES schools(id) ON DELETE CASCADE,
    academic_year_id UUID        REFERENCES academic_years(id) ON DELETE SET NULL,
    name             VARCHAR(100) NOT NULL,
    exam_type        VARCHAR(30),
    start_date       DATE,
    end_date         DATE,
    result_published BOOLEAN     NOT NULL DEFAULT FALSE,
    published_at     TIMESTAMPTZ,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Exam Subjects (class-level breakdown inside an exam)
CREATE TABLE IF NOT EXISTS exam_subjects (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exam_id       UUID          NOT NULL REFERENCES exams(id) ON DELETE CASCADE,
    class_id      UUID          REFERENCES school_classes(id) ON DELETE SET NULL,
    subject       VARCHAR(100)  NOT NULL,
    max_marks     NUMERIC(5,2)  NOT NULL,
    passing_marks NUMERIC(5,2),
    exam_date     DATE,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- Exam Marks (one row per student per exam-subject)
CREATE TABLE IF NOT EXISTS exam_marks (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id        UUID          NOT NULL REFERENCES schools(id) ON DELETE CASCADE,
    student_id       UUID          REFERENCES students(id) ON DELETE SET NULL,
    exam_subject_id  UUID          REFERENCES exam_subjects(id) ON DELETE CASCADE,
    marks_obtained   NUMERIC(5,2),
    is_absent        BOOLEAN       NOT NULL DEFAULT FALSE,
    remarks          VARCHAR(200),
    entered_by       UUID          REFERENCES users(id) ON DELETE SET NULL,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    UNIQUE (student_id, exam_subject_id)
);

-- Message / Notification Logs
CREATE TABLE IF NOT EXISTS message_logs (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id            UUID          NOT NULL REFERENCES schools(id) ON DELETE CASCADE,
    student_id           UUID          REFERENCES students(id) ON DELETE SET NULL,
    recipient_phone      VARCHAR(15)   NOT NULL,
    channel              VARCHAR(10)   NOT NULL DEFAULT 'WHATSAPP',
    message_type         VARCHAR(50),
    template_name        VARCHAR(100),
    message_body         TEXT,
    status               VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    whatsapp_message_id  VARCHAR(200),
    failure_reason       TEXT,
    sent_at              TIMESTAMPTZ,
    created_at           TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_exams_school_year     ON exams(school_id, academic_year_id);
CREATE INDEX IF NOT EXISTS idx_exam_subjects_exam    ON exam_subjects(exam_id);
CREATE INDEX IF NOT EXISTS idx_exam_marks_subject    ON exam_marks(exam_subject_id);
CREATE INDEX IF NOT EXISTS idx_exam_marks_student    ON exam_marks(student_id);
CREATE INDEX IF NOT EXISTS idx_message_logs_school   ON message_logs(school_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_message_logs_student  ON message_logs(student_id);
