-- Create franchisee_bank_details table
CREATE TABLE IF NOT EXISTS franchisee_bank_details (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    account_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(30) NOT NULL,
    ifsc_code VARCHAR(20) NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    branch_name VARCHAR(100),
    account_type VARCHAR(30),
    upi_id VARCHAR(50),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at TIMESTAMP,
    verified_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Add unique constraint for account number per user
    CONSTRAINT uk_franchisee_bank_user_account UNIQUE (user_id, account_number),
    
    -- Add constraint to ensure only one primary account per user
    CONSTRAINT check_one_primary_per_user CHECK (
        NOT is_primary OR (
            is_primary AND 
            (SELECT COUNT(*) FROM franchisee_bank_details 
             WHERE user_id = user_id AND is_primary = TRUE AND id != id) = 0
        )
    )
);

-- Create index for faster lookups
CREATE INDEX idx_franchisee_bank_user_id ON franchisee_bank_details(user_id);

-- Add a comment explaining the table
COMMENT ON TABLE franchisee_bank_details IS 'Stores bank account details for franchisees to facilitate revenue withdrawals';
