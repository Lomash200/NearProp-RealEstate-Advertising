-- Add transaction and balance tracking fields to franchisee_withdrawal_requests table
ALTER TABLE franchisee_withdrawal_requests 
    ADD COLUMN IF NOT EXISTS transaction_type VARCHAR(50),
    ADD COLUMN IF NOT EXISTS transaction_id VARCHAR(100),
    ADD COLUMN IF NOT EXISTS original_balance DECIMAL(15, 2),
    ADD COLUMN IF NOT EXISTS updated_balance DECIMAL(15, 2); 