-- ============================================================
-- VidyaSetu — V2__indexes.sql
-- Additional composite indexes for common query patterns
-- ============================================================

-- Fee payments: monthly collection queries
CREATE INDEX idx_fee_payments_month
    ON fee_payments(school_id, for_month)
    WHERE status = 'ACTIVE';

-- Attendance: status breakdown queries
CREATE INDEX idx_attendance_status
    ON attendance(school_id, status, date);

-- Students: active student counts per class
CREATE INDEX idx_students_active
    ON students(school_id, is_active, class_id);

-- Fee concessions: active concessions for defaulter calc
CREATE INDEX idx_fee_concessions_active
    ON fee_concessions(school_id, student_id, is_active)
    WHERE is_active = TRUE;

-- Message logs: delivery status tracking
CREATE INDEX idx_message_logs_created
    ON message_logs(school_id, created_at DESC);

-- Audit logs: user activity view
CREATE INDEX idx_audit_logs_user
    ON audit_logs(user_id, created_at DESC);
