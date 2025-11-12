-- Complete database setup with test data
-- This migration combines schema fixes, seeding, and test data

-- 1. Ensure pgcrypto extension for password hashing
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 2. Align ID types with JPA (Long -> BIGINT) - only if not already done
DO $$
BEGIN
    -- Check if roles.role_id is already BIGINT
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'roles' 
        AND column_name = 'role_id' 
        AND data_type = 'integer'
    ) THEN
        ALTER TABLE roles ALTER COLUMN role_id TYPE BIGINT;
    END IF;
    
    -- Check if users.user_id is already BIGINT
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' 
        AND column_name = 'user_id' 
        AND data_type = 'integer'
    ) THEN
        ALTER TABLE users ALTER COLUMN user_id TYPE BIGINT;
        ALTER TABLE users ALTER COLUMN role_id TYPE BIGINT;
    END IF;
    
    -- Check other tables
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'events' 
        AND column_name = 'user_id' 
        AND data_type = 'integer'
    ) THEN
        ALTER TABLE events ALTER COLUMN user_id TYPE BIGINT;
    END IF;
    
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'recurring_schedules' 
        AND column_name = 'created_by' 
        AND data_type = 'integer'
    ) THEN
        ALTER TABLE recurring_schedules ALTER COLUMN created_by TYPE BIGINT;
    END IF;
    
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' 
        AND column_name = 'user_id' 
        AND data_type = 'integer'
    ) THEN
        ALTER TABLE notifications ALTER COLUMN user_id TYPE BIGINT;
    END IF;
    
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'events_bookings' 
        AND column_name = 'approved_by' 
        AND data_type = 'integer'
    ) THEN
        ALTER TABLE events_bookings ALTER COLUMN approved_by TYPE BIGINT;
    END IF;
    
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'audit_logs' 
        AND column_name = 'user_id' 
        AND data_type = 'integer'
    ) THEN
        ALTER TABLE audit_logs ALTER COLUMN user_id TYPE BIGINT;
    END IF;
END $$;

-- 3. Seed default roles if absent
INSERT INTO roles(role_name)
SELECT 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'ADMIN');

INSERT INTO roles(role_name)
SELECT 'USER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'USER');

INSERT INTO roles(role_name)
SELECT 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'STUDENT');

INSERT INTO roles(role_name)
SELECT 'TEACHER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'TEACHER');

-- 4. Create admin user if not exists
DO $$
DECLARE
    admin_role_id BIGINT;
BEGIN
    SELECT role_id INTO admin_role_id FROM roles WHERE role_name = 'ADMIN' LIMIT 1;
    IF admin_role_id IS NOT NULL THEN
        INSERT INTO users (full_name, email, password_hash, role_id, status)
        SELECT 'System Administrator', 'admin@unilab.local', crypt('admin123', gen_salt('bf')), admin_role_id, TRUE
        WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@unilab.local');
    END IF;
END $$;

-- 5. Create test users for different roles
DO $$
DECLARE
    admin_role_id BIGINT;
    user_role_id BIGINT;
    student_role_id BIGINT;
    teacher_role_id BIGINT;
BEGIN
    -- Get role IDs
    SELECT role_id INTO admin_role_id FROM roles WHERE role_name = 'ADMIN' LIMIT 1;
    SELECT role_id INTO user_role_id FROM roles WHERE role_name = 'USER' LIMIT 1;
    SELECT role_id INTO student_role_id FROM roles WHERE role_name = 'STUDENT' LIMIT 1;
    SELECT role_id INTO teacher_role_id FROM roles WHERE role_name = 'TEACHER' LIMIT 1;
    
    -- Test Admin Users
    INSERT INTO users (full_name, email, password_hash, role_id, status)
    VALUES 
        ('John Admin', 'john.admin@unilab.local', crypt('admin123', gen_salt('bf')), admin_role_id, TRUE),
        ('Jane Manager', 'jane.manager@unilab.local', crypt('admin123', gen_salt('bf')), admin_role_id, TRUE)
    ON CONFLICT (email) DO NOTHING;
    
    -- Test Regular Users
    INSERT INTO users (full_name, email, password_hash, role_id, status)
    VALUES 
        ('Alice User', 'alice.user@unilab.local', crypt('user123', gen_salt('bf')), user_role_id, TRUE),
        ('Bob User', 'bob.user@unilab.local', crypt('user123', gen_salt('bf')), user_role_id, TRUE),
        ('Charlie User', 'charlie.user@unilab.local', crypt('user123', gen_salt('bf')), user_role_id, FALSE)
    ON CONFLICT (email) DO NOTHING;
    
    -- Test Students
    INSERT INTO users (full_name, email, password_hash, role_id, student_id, faculty, status)
    VALUES 
        ('David Student', 'david.student@unilab.local', crypt('student123', gen_salt('bf')), student_role_id, 'SE123456', 'Software Engineering', TRUE),
        ('Emma Student', 'emma.student@unilab.local', crypt('student123', gen_salt('bf')), student_role_id, 'IT123789', 'Information Technology', TRUE),
        ('Frank Student', 'frank.student@unilab.local', crypt('student123', gen_salt('bf')), student_role_id, 'CS123321', 'Computer Science', FALSE)
    ON CONFLICT (email) DO NOTHING;
    
    -- Test Teachers
    INSERT INTO users (full_name, email, password_hash, role_id, faculty, status)
    VALUES 
        ('Grace Teacher', 'grace.teacher@unilab.local', crypt('teacher123', gen_salt('bf')), teacher_role_id, 'Software Engineering', TRUE),
        ('Henry Teacher', 'henry.teacher@unilab.local', crypt('teacher123', gen_salt('bf')), teacher_role_id, 'Information Technology', TRUE),
        ('Ivy Teacher', 'ivy.teacher@unilab.local', crypt('teacher123', gen_salt('bf')), teacher_role_id, 'Computer Science', FALSE)
    ON CONFLICT (email) DO NOTHING;
    
    -- Test FPT University Students (@fpt.edu.vn)
    INSERT INTO users (full_name, email, password_hash, role_id, student_id, faculty, status)
    VALUES 
        ('Nguyen Van An', 'an.nguyen@fpt.edu.vn', crypt('fpt123', gen_salt('bf')), student_role_id, 'SE123456', 'Software Engineering', TRUE),
        ('Tran Thi Binh', 'binh.tran@fpt.edu.vn', crypt('fpt123', gen_salt('bf')), student_role_id, 'IT123789', 'Information Technology', TRUE),
        ('Le Van Cuong', 'cuong.le@fpt.edu.vn', crypt('fpt123', gen_salt('bf')), student_role_id, 'CS123321', 'Computer Science', TRUE),
        ('Pham Thi Dung', 'dung.pham@fpt.edu.vn', crypt('fpt123', gen_salt('bf')), student_role_id, 'SE123654', 'Software Engineering', FALSE)
    ON CONFLICT (email) DO NOTHING;
    
    -- Test FPT University Teachers (@fpt.edu.vn)
    INSERT INTO users (full_name, email, password_hash, role_id, faculty, status)
    VALUES 
        ('Dr. Nguyen Van Em', 'em.nguyen@fpt.edu.vn', crypt('fpt123', gen_salt('bf')), teacher_role_id, 'Software Engineering', TRUE),
        ('Prof. Tran Thi Phuong', 'phuong.tran@fpt.edu.vn', crypt('fpt123', gen_salt('bf')), teacher_role_id, 'Information Technology', TRUE)
    ON CONFLICT (email) DO NOTHING;
END $$;

-- 6. Create some test labs
INSERT INTO labs (lab_name, capacity, location, description, status)
VALUES 
    ('Computer Lab 1', 30, 'Building A, Room 101', 'General purpose computer lab with Windows machines', 'ACTIVE'),
    ('Computer Lab 2', 25, 'Building A, Room 102', 'Programming lab with Linux workstations', 'ACTIVE'),
    ('Network Lab', 20, 'Building B, Room 201', 'Network equipment and server lab', 'ACTIVE'),
    ('Hardware Lab', 15, 'Building B, Room 202', 'Computer hardware and assembly lab', 'MAINTENANCE'),
    ('Multimedia Lab', 35, 'Building C, Room 301', 'Audio/video editing and design lab', 'ACTIVE')
ON CONFLICT DO NOTHING;
