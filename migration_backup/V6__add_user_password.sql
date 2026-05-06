-- Add password column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS password VARCHAR(128);

-- Update password for existing admin user with a default password (must be changed after first login)
-- Password is 'admin123' encrypted with BCrypt
UPDATE users SET password = '$2a$10$FxOF5E2ZKTIjKzSBAy9LWOhB.LXjH9U0toiXMiR1lXsrh6NHjkpAS' WHERE id = 1; 