-- Ensure bcrypt-compatible hash for admin password using pgcrypto

CREATE EXTENSION IF NOT EXISTS pgcrypto;

UPDATE users
SET password_hash = crypt('admin123', gen_salt('bf'))
WHERE email = 'admin@fpt.edu.vn';


