-- Align ID types with JPA (Long -> BIGINT) and seed roles via SQL

-- Roles
ALTER TABLE roles
    ALTER COLUMN role_id TYPE BIGINT;

-- Users and FK to roles
ALTER TABLE users
    ALTER COLUMN user_id TYPE BIGINT,
    ALTER COLUMN role_id TYPE BIGINT;

-- Tables referencing users
ALTER TABLE events
    ALTER COLUMN user_id TYPE BIGINT;

ALTER TABLE recurring_schedules
    ALTER COLUMN created_by TYPE BIGINT;

ALTER TABLE notifications
    ALTER COLUMN user_id TYPE BIGINT;

ALTER TABLE events_bookings
    ALTER COLUMN approved_by TYPE BIGINT;

ALTER TABLE audit_logs
    ALTER COLUMN user_id TYPE BIGINT;

-- Seed default roles if absent
INSERT INTO roles(role_name)
SELECT 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'ADMIN');

INSERT INTO roles(role_name)
SELECT 'USER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'USER');


