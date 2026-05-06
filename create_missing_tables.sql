-- Create subscription_plan_features table if missing
CREATE TABLE IF NOT EXISTS subscription_plan_features (
    plan_id BIGINT NOT NULL,
    feature VARCHAR(100) NOT NULL,
    PRIMARY KEY (plan_id, feature),
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id) ON DELETE CASCADE
);

-- Create franchisee_withdrawal_requests table if missing
CREATE TABLE IF NOT EXISTS franchisee_withdrawal_requests (
    id BIGSERIAL PRIMARY KEY,
    franchisee_district_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    bank_account_details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    processed_by BIGINT,
    rejection_reason TEXT,
    FOREIGN KEY (franchisee_district_id) REFERENCES franchisee_districts(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Create indexes for new tables
CREATE INDEX IF NOT EXISTS idx_subscription_plan_features_plan_id ON subscription_plan_features(plan_id);
CREATE INDEX IF NOT EXISTS idx_franchisee_withdrawal_requests_franchisee_district_id ON franchisee_withdrawal_requests(franchisee_district_id);
CREATE INDEX IF NOT EXISTS idx_franchisee_withdrawal_requests_status ON franchisee_withdrawal_requests(status); 