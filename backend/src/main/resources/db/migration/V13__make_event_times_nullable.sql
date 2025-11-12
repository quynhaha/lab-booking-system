-- Make start_time and end_time nullable for events
-- Events can be created without times, times will be set when teacher books a lab
ALTER TABLE events ALTER COLUMN start_time DROP NOT NULL;
ALTER TABLE events ALTER COLUMN end_time DROP NOT NULL;

