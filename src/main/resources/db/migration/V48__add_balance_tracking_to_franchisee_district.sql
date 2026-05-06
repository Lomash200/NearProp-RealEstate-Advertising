-- Add available_balance and withdrawal_history columns to franchisee_districts table
ALTER TABLE franchisee_districts 
    ADD COLUMN IF NOT EXISTS available_balance DECIMAL(15, 2) DEFAULT 0.00,
    ADD COLUMN IF NOT EXISTS withdrawal_history JSONB DEFAULT '[]'::jsonb; 