-- Drop existing OTP table
DROP TABLE IF EXISTS otps;

-- Recreate OTP table with updated schema
CREATE TABLE otps (
    id BIGSERIAL PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(15) NULL,
    email VARCHAR(255) NULL,
    type VARCHAR(10) NOT NULL,
    code VARCHAR(6) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    attempts INT NOT NULL DEFAULT 0,
    blocked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verified_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL
);

-- Create indexes
CREATE INDEX idx_otp_identifier_type ON otps(identifier, type);
CREATE INDEX idx_otp_mobile ON otps(mobile_number);
CREATE INDEX idx_otp_email ON otps(email); 