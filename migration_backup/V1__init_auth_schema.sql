-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    email_verified BOOLEAN DEFAULT FALSE,
    mobile_verified BOOLEAN DEFAULT FALSE,
    aadhaar_number VARCHAR(12),
    aadhaar_verified BOOLEAN DEFAULT FALSE,
    address TEXT,
    district VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- User roles table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- User sessions table
CREATE TABLE user_sessions (
    session_id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_info VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- OTP table
CREATE TABLE otps (
    id BIGSERIAL PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL,
    code VARCHAR(6) NOT NULL,
    type VARCHAR(10) NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL
);

-- Role requests table
CREATE TABLE role_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    requested_role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admin_comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    processed_by BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Role request documents table
CREATE TABLE role_request_documents (
    role_request_id BIGINT NOT NULL,
    document_url TEXT NOT NULL,
    FOREIGN KEY (role_request_id) REFERENCES role_requests(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_user_mobile ON users(mobile_number);
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_otp_identifier_type ON otps(identifier, type);
CREATE INDEX idx_role_requests_status ON role_requests(status);
CREATE INDEX idx_user_sessions_user ON user_sessions(user_id);

-- Create admin user
INSERT INTO users (name, mobile_number, mobile_verified, email, email_verified, address)
VALUES ('Admin', '+919999999999', TRUE, 'admin@nearprop.com', TRUE, 'NearProp Office');

INSERT INTO user_roles (user_id, role)
VALUES (1, 'USER'), (1, 'ADMIN'); 