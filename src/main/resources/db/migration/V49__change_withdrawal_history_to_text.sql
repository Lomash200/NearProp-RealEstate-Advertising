-- Change withdrawal_history column type from JSONB to TEXT
ALTER TABLE franchisee_districts ALTER COLUMN withdrawal_history TYPE TEXT; 