-- Make lab_id nullable in events table
-- Events can be created without a lab, lab can be assigned later
ALTER TABLE events ALTER COLUMN lab_id DROP NOT NULL;

