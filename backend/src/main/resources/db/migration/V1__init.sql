-- Initial schema for Lab Booking (PostgreSQL)

CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id INTEGER NOT NULL REFERENCES roles(role_id),
    student_id VARCHAR(20),
    faculty VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    status BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE labs (
    lab_id BIGSERIAL PRIMARY KEY,
    lab_name VARCHAR(100) NOT NULL,
    capacity INTEGER NOT NULL,
    location VARCHAR(100),
    description VARCHAR(255),
    status VARCHAR(20),
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE events (
    event_id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users(user_id),
    lab_id INTEGER NOT NULL REFERENCES labs(lab_id),
    status VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE recurring_schedules (
    schedule_id SERIAL PRIMARY KEY,
    lab_id INTEGER NOT NULL REFERENCES labs(lab_id),
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    day_of_week INTEGER NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    frequency VARCHAR(20) NOT NULL,
    created_by INTEGER NOT NULL REFERENCES users(user_id)
);

CREATE TABLE notifications (
    notification_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id),
    message VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_read BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE events_bookings (
    booking_id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL REFERENCES events(event_id) ON DELETE CASCADE,
    approved_by INTEGER REFERENCES users(user_id),
    approved_at TIMESTAMP,
    rejection_reason VARCHAR(1000),
    status VARCHAR(20) NOT NULL
);

CREATE TABLE audit_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id),
    action VARCHAR(200) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    details VARCHAR(500)
);

-- Indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_events_lab_time ON events(lab_id, start_time, end_time);
CREATE INDEX idx_recurring_schedules_lab_day ON recurring_schedules(lab_id, day_of_week);

