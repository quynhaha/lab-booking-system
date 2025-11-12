-- Add equipment table
CREATE TABLE equipment (
    equipment_id BIGSERIAL PRIMARY KEY,
    equipment_name VARCHAR(100) NOT NULL,
    equipment_type VARCHAR(50),
    description VARCHAR(255),
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    lab_id BIGINT NOT NULL REFERENCES labs(lab_id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Add booking_limits table
CREATE TABLE booking_limits (
    limit_id BIGSERIAL PRIMARY KEY,
    limit_name VARCHAR(100) NOT NULL UNIQUE,
    max_bookings_per_user INTEGER NOT NULL,
    max_duration_hours INTEGER NOT NULL,
    advance_booking_days INTEGER NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Add private event fields to events table
ALTER TABLE events ADD COLUMN is_private BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE events ADD COLUMN invitees VARCHAR(1000);

-- Insert sample equipment data
INSERT INTO equipment (equipment_name, equipment_type, description, lab_id) VALUES
('Dell OptiPlex 7090', 'Computer', 'High-performance desktop computer with Intel i7 processor', 1),
('MacBook Pro M2', 'Laptop', 'Apple MacBook Pro with M2 chip for development', 1),
('Dell Monitor 24"', 'Monitor', '24-inch 4K monitor for programming', 1),
('Logitech MX Master 3', 'Mouse', 'Wireless mouse for productivity', 1),
('Mechanical Keyboard', 'Keyboard', 'RGB mechanical keyboard for coding', 1),
('Arduino Uno R3', 'Microcontroller', 'Arduino development board for IoT projects', 2),
('Raspberry Pi 4', 'Single Board Computer', 'Raspberry Pi 4 Model B for embedded projects', 2),
('Oscilloscope', 'Test Equipment', 'Digital oscilloscope for electronics testing', 2),
('Multimeter', 'Test Equipment', 'Digital multimeter for circuit testing', 2),
('Soldering Station', 'Tool', 'Temperature-controlled soldering station', 2);

-- Insert sample booking limits
INSERT INTO booking_limits (limit_name, max_bookings_per_user, max_duration_hours, advance_booking_days) VALUES
('Default Limits', 5, 4, 7),
('Student Limits', 3, 2, 3),
('Lecturer Limits', 10, 8, 14),
('Admin Limits', 20, 12, 30);
