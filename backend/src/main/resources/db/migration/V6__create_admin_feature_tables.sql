-- Create labs table (using existing lab_id structure)
CREATE TABLE IF NOT EXISTS labs (
    lab_id BIGSERIAL PRIMARY KEY,
    lab_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    location VARCHAR(255) NOT NULL,
    capacity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    facilities VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create event_categories table
CREATE TABLE IF NOT EXISTS event_categories (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    color VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    max_duration_hours INTEGER,
    requires_approval BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_code VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    lab_id BIGINT NOT NULL,
    category_id BIGINT,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    participants_count INTEGER,
    is_multi_lab BOOLEAN DEFAULT FALSE,
    parent_booking_id BIGINT,
    refund_amount DECIMAL(10, 2),
    refund_status VARCHAR(50) DEFAULT 'NONE',
    cancellation_reason VARCHAR(1000),
    cancelled_by_user_id BIGINT,
    cancelled_at TIMESTAMP,
    approved_by_user_id BIGINT,
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (lab_id) REFERENCES labs(lab_id),
    FOREIGN KEY (category_id) REFERENCES event_categories(id)
);

-- Create indexes for bookings
CREATE INDEX idx_booking_status ON bookings(status);
CREATE INDEX idx_booking_user ON bookings(user_id);
CREATE INDEX idx_booking_dates ON bookings(start_time, end_time);

-- Create penalty_rules table
CREATE TABLE IF NOT EXISTS penalty_rules (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    violation_type VARCHAR(100) NOT NULL,
    penalty_amount DECIMAL(10, 2) NOT NULL,
    penalty_type VARCHAR(50) NOT NULL,
    penalty_points INTEGER,
    suspension_days INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    grace_period_hours INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create penalty_history table
CREATE TABLE IF NOT EXISTS penalty_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    booking_id BIGINT,
    penalty_rule_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    penalty_points INTEGER,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reason VARCHAR(2000),
    notes VARCHAR(2000),
    applied_by_user_id BIGINT,
    waived_by_user_id BIGINT,
    paid_at TIMESTAMP,
    waived_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (penalty_rule_id) REFERENCES penalty_rules(id)
);

-- Create indexes for penalty_history
CREATE INDEX idx_penalty_user ON penalty_history(user_id);
CREATE INDEX idx_penalty_booking ON penalty_history(booking_id);
CREATE INDEX idx_penalty_status ON penalty_history(status);

-- Create booking_rules table
CREATE TABLE IF NOT EXISTS booking_rules (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    rule_type VARCHAR(100) NOT NULL,
    rule_value VARCHAR(1000) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    priority INTEGER NOT NULL,
    applies_to VARCHAR(50),
    category_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
