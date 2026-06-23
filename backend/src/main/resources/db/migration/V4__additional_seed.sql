-- ============================================================
-- VidyaSetu — V4__additional_seed.sql
-- Adds 5 more students (total 10) + 5 more holidays (total 10)
-- for the Bal Vikas Vidyalaya seed school
-- ============================================================

-- 5 additional students (Class 1–3, A-section)
INSERT INTO students (id, school_id, class_id, roll_number, first_name, last_name, gender)
VALUES
    ('66666666-6666-6666-6666-666666666666',
     'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'd4e5f6a7-b8c9-0123-def0-234567890123',  -- Class 1
     '003', 'Anjali',   'Mishra',  'FEMALE'),

    ('77777777-7777-7777-7777-777777777777',
     'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'd4e5f6a7-b8c9-0123-def0-234567890123',  -- Class 1
     '004', 'Rahul',    'Gupta',   'MALE'),

    ('88888888-8888-8888-8888-888888888888',
     'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'e5f6a7b8-c9d0-1234-ef01-345678901234',  -- Class 2
     '003', 'Neha',     'Sharma',  'FEMALE'),

    ('99999999-9999-9999-9999-999999999999',
     'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'f6a7b8c9-d0e1-2345-f012-456789012345',  -- Class 3
     '002', 'Deepak',   'Pandey',  'MALE'),

    ('aaaabbbb-cccc-dddd-eeee-ffffffffffff',
     'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'f6a7b8c9-d0e1-2345-f012-456789012345',  -- Class 3
     '003', 'Pooja',    'Yadav',   'FEMALE');

-- Parents for additional students
INSERT INTO parents (student_id, school_id, name, relation, phone, whatsapp_number, is_primary)
VALUES
    ('66666666-6666-6666-6666-666666666666',
     'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'Kamlesh Mishra',  'FATHER', '9866666666', '9866666666', TRUE),

    ('77777777-7777-7777-7777-777777777777',
     'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'Vijay Gupta',     'FATHER', '9877777777', '9877777777', TRUE),

    ('88888888-8888-8888-8888-888888888888',
     'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'Rakesh Sharma',   'FATHER', '9888888888', '9888888888', TRUE),

    ('99999999-9999-9999-9999-999999999999',
     'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'Sunil Pandey',    'FATHER', '9899999999', '9899999999', TRUE),

    ('aaaabbbb-cccc-dddd-eeee-ffffffffffff',
     'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'Ashok Yadav',     'FATHER', '9800000000', '9800000000', TRUE);

-- 5 additional holidays (total = 10 for 2025-2026)
INSERT INTO holidays (school_id, academic_year_id, date, name, type)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'c3d4e5f6-a7b8-9012-cdef-123456789012',
     '2025-04-14', 'Ambedkar Jayanti',      'PUBLIC'),

    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'c3d4e5f6-a7b8-9012-cdef-123456789012',
     '2025-04-18', 'Good Friday',           'PUBLIC'),

    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'c3d4e5f6-a7b8-9012-cdef-123456789012',
     '2025-11-05', 'Diwali Holiday',        'SCHOOL'),

    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'c3d4e5f6-a7b8-9012-cdef-123456789012',
     '2025-12-25', 'Christmas Day',         'PUBLIC'),

    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890',
     'c3d4e5f6-a7b8-9012-cdef-123456789012',
     '2026-01-14', 'Makar Sankranti',       'PUBLIC');
