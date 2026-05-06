-- Create the franchise_requests table
CREATE TABLE franchise_requests (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    district_id BIGINT NOT NULL,
    district_name VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    business_name VARCHAR(255) NOT NULL,
    business_address VARCHAR(500) NOT NULL,
    business_registration_number VARCHAR(100),
    gst_number VARCHAR(100),
    pan_number VARCHAR(50) NOT NULL,
    aadhar_number VARCHAR(50) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    years_of_experience INTEGER,
    document_ids TEXT,
    status VARCHAR(20) NOT NULL,
    admin_comments VARCHAR(1000),
    reviewed_by BIGINT REFERENCES users(id),
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Add unique constraint for user and district
    CONSTRAINT uk_franchise_request_user_district UNIQUE (user_id, district_id)
);

-- Create indexes
CREATE INDEX idx_franchise_requests_user_id ON franchise_requests(user_id);
CREATE INDEX idx_franchise_requests_district_id ON franchise_requests(district_id);
CREATE INDEX idx_franchise_requests_status ON franchise_requests(status);
CREATE INDEX idx_franchise_requests_reviewed_by ON franchise_requests(reviewed_by);

-- Add a field to the FranchiseeDistrict table to reference the approved franchise request
ALTER TABLE franchisee_districts ADD COLUMN franchise_request_id BIGINT REFERENCES franchise_requests(id); 