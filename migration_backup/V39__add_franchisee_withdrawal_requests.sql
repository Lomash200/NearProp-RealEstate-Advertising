-- Add franchisee withdrawal requests table
CREATE TABLE IF NOT EXISTS franchisee_withdrawal_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    franchisee_district_id BIGINT NOT NULL,
    requested_amount DECIMAL(12,2) NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    status VARCHAR(20) NOT NULL,
    processed_by BIGINT,
    processed_at TIMESTAMP,
    admin_comments VARCHAR(1000),
    payment_reference VARCHAR(100),
    payment_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_withdrawal_request_district FOREIGN KEY (franchisee_district_id) REFERENCES franchisee_districts(id)
);

-- Add indexes for better performance
CREATE INDEX idx_withdrawal_franchisee_district_id ON franchisee_withdrawal_requests(franchisee_district_id);
CREATE INDEX idx_withdrawal_status ON franchisee_withdrawal_requests(status); 