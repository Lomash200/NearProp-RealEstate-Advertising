-- Create monthly revenue reports table
CREATE TABLE monthly_revenue_reports (
    id BIGSERIAL PRIMARY KEY,
    year INTEGER NOT NULL,
    month INTEGER NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    report_status VARCHAR(20) NOT NULL,
    
    -- Franchisee details
    franchisee_id BIGINT NOT NULL,
    franchisee_name VARCHAR(255) NOT NULL,
    business_name VARCHAR(255),
    
    -- District details
    franchisee_district_id BIGINT NOT NULL,
    district_id BIGINT NOT NULL,
    district_name VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    
    -- Revenue details
    total_revenue DECIMAL(19, 2) NOT NULL,
    franchisee_commission DECIMAL(19, 2) NOT NULL,
    admin_share DECIMAL(19, 2) NOT NULL,
    total_subscriptions INTEGER,
    new_subscriptions INTEGER,
    renewed_subscriptions INTEGER,
    
    -- Payment details
    payment_due_date DATE,
    payment_date DATE,
    payment_reference VARCHAR(255),
    payment_method VARCHAR(100),
    admin_comments TEXT,
    
    -- Bank details
    bank_detail_id BIGINT,
    account_name VARCHAR(255),
    account_number VARCHAR(255),
    bank_name VARCHAR(255),
    ifsc_code VARCHAR(100),
    
    -- Withdrawal details
    emergency_withdrawals_amount DECIMAL(19, 2),
    emergency_withdrawals_count INTEGER,
    
    -- Balance calculation
    previous_balance DECIMAL(19, 2),
    current_balance DECIMAL(19, 2),
    final_payable_amount DECIMAL(19, 2),
    
    -- Tracking
    updated_at TIMESTAMP,
    processed_by BIGINT,
    processed_at TIMESTAMP,
    
    -- Constraints
    FOREIGN KEY (franchisee_id) REFERENCES users(id),
    FOREIGN KEY (franchisee_district_id) REFERENCES franchisee_districts(id),
    FOREIGN KEY (bank_detail_id) REFERENCES franchisee_bank_details(id),
    FOREIGN KEY (processed_by) REFERENCES users(id),
    
    -- Unique constraint to prevent duplicate reports
    CONSTRAINT uk_monthly_report_district_month UNIQUE (franchisee_district_id, year, month)
);

-- Create index for faster queries
CREATE INDEX idx_monthly_report_franchisee ON monthly_revenue_reports(franchisee_id);
CREATE INDEX idx_monthly_report_district ON monthly_revenue_reports(franchisee_district_id);
CREATE INDEX idx_monthly_report_status ON monthly_revenue_reports(report_status);
CREATE INDEX idx_monthly_report_year_month ON monthly_revenue_reports(year, month);

-- Add methods to find withdrawal requests by district and date range
ALTER TABLE franchisee_withdrawal_requests ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();

-- Add missing methods to DistrictRevenueRepository
-- Note: These are just comments to remind you to add the methods to the repository interface
-- findByFranchiseeDistrictAndTransactionDateBetween
-- findByFranchiseeDistrictId 