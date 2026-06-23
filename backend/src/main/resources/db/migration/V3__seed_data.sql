-- ============================================================
-- VidyaSetu — V3__seed_data.sql
-- Dev seed: 1 school, 1 owner, 1 academic year, 3 classes,
--           5 students, 3 fee types, holidays
-- Password for all users: "password123" (BCrypt strength 12)
-- ============================================================

-- School
INSERT INTO schools (id, name, institution_type, city, state, phone, email, plan_type, language_preference)
VALUES (
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'Bal Vikas Vidyalaya',
    'SCHOOL',
    'Ballia',
    'Uttar Pradesh',
    '9876543210',
    'admin@balvikas.in',
    'BASIC',
    'hi'
);

-- Owner user (password: password123)
INSERT INTO users (id, school_id, name, phone, password_hash, role)
VALUES (
    'b2c3d4e5-f6a7-8901-bcde-f12345678901',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'Ramesh Kumar',
    '9876543210',
    '$2a$12$d0G120cQdAKBip67jZ..VOsKBLZbANUm1NKzKVmreBXGSH1Z07d96',
    'OWNER'
);

-- Academic year 2025-2026
INSERT INTO academic_years (id, school_id, name, start_date, end_date, is_current)
VALUES (
    'c3d4e5f6-a7b8-9012-cdef-123456789012',
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    '2025-2026',
    '2025-04-01',
    '2026-03-31',
    TRUE
);

-- Classes
INSERT INTO classes (id, school_id, academic_year_id, name, section, display_order)
VALUES
    ('d4e5f6a7-b8c9-0123-def0-234567890123', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 'Class 1', 'A', 1),
    ('e5f6a7b8-c9d0-1234-ef01-345678901234', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 'Class 2', 'A', 2),
    ('f6a7b8c9-d0e1-2345-f012-456789012345', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 'Class 3', 'A', 3);

-- Students
INSERT INTO students (id, school_id, class_id, roll_number, first_name, last_name, gender)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'd4e5f6a7-b8c9-0123-def0-234567890123', '001', 'Aarav',   'Singh',  'MALE'),
    ('22222222-2222-2222-2222-222222222222', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'd4e5f6a7-b8c9-0123-def0-234567890123', '002', 'Priya',   'Verma',  'FEMALE'),
    ('33333333-3333-3333-3333-333333333333', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'e5f6a7b8-c9d0-1234-ef01-345678901234', '001', 'Rohit',   'Kumar',  'MALE'),
    ('44444444-4444-4444-4444-444444444444', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'e5f6a7b8-c9d0-1234-ef01-345678901234', '002', 'Sunita',  'Yadav',  'FEMALE'),
    ('55555555-5555-5555-5555-555555555555', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'f6a7b8c9-d0e1-2345-f012-456789012345', '001', 'Vikram',  'Tiwari', 'MALE');

-- Parents
INSERT INTO parents (student_id, school_id, name, relation, phone, whatsapp_number, is_primary)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Suresh Singh',    'FATHER', '9811111111', '9811111111', TRUE),
    ('22222222-2222-2222-2222-222222222222', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Mohan Verma',     'FATHER', '9822222222', '9822222222', TRUE),
    ('33333333-3333-3333-3333-333333333333', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Ramesh Kumar',    'FATHER', '9833333333', '9833333333', TRUE),
    ('44444444-4444-4444-4444-444444444444', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Dinesh Yadav',    'FATHER', '9844444444', '9844444444', TRUE),
    ('55555555-5555-5555-5555-555555555555', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Anil Tiwari',     'FATHER', '9855555555', '9855555555', TRUE);

-- Fee types
INSERT INTO fee_types (id, school_id, name)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Tuition Fee'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Transport Fee'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Exam Fee');

-- Fee structures (Tuition: ₹500/month for Class 1, ₹600 for Class 2, ₹700 for Class 3)
INSERT INTO fee_structures (school_id, class_id, fee_type_id, academic_year_id, amount, frequency, due_day)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'd4e5f6a7-b8c9-0123-def0-234567890123', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 500.00, 'MONTHLY', 10),
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'e5f6a7b8-c9d0-1234-ef01-345678901234', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 600.00, 'MONTHLY', 10),
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'f6a7b8c9-d0e1-2345-f012-456789012345', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 700.00, 'MONTHLY', 10);

-- Holidays (UP public holidays for 2025-2026)
INSERT INTO holidays (school_id, academic_year_id, date, name, type)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'c3d4e5f6-a7b8-9012-cdef-123456789012', '2025-08-15', 'Independence Day',  'PUBLIC'),
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'c3d4e5f6-a7b8-9012-cdef-123456789012', '2025-10-02', 'Gandhi Jayanti',    'PUBLIC'),
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'c3d4e5f6-a7b8-9012-cdef-123456789012', '2025-10-20', 'Diwali',            'PUBLIC'),
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'c3d4e5f6-a7b8-9012-cdef-123456789012', '2026-01-26', 'Republic Day',      'PUBLIC'),
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'c3d4e5f6-a7b8-9012-cdef-123456789012', '2026-03-18', 'Holi',              'PUBLIC');
