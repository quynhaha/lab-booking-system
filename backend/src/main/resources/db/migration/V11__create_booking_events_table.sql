CREATE TABLE IF NOT EXISTS booking_events (
    id          BIGSERIAL PRIMARY KEY,
    booking_id  BIGINT      NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    event_id    BIGINT      NOT NULL REFERENCES events(event_id) ON DELETE CASCADE,
    status      VARCHAR(50) NOT NULL DEFAULT 'LINKED',
    approved_by_user_id BIGINT REFERENCES users(user_id),
    approved_at TIMESTAMP,
    rejection_reason VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (booking_id)
);

CREATE INDEX IF NOT EXISTS idx_booking_events_booking ON booking_events(booking_id);
CREATE INDEX IF NOT EXISTS idx_booking_events_event ON booking_events(event_id);

