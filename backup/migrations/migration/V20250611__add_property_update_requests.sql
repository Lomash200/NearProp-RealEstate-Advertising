-- Create property update requests table
CREATE TABLE property_update_requests (
    id BIGSERIAL PRIMARY KEY,
    request_id VARCHAR(255) UNIQUE,
    property_id BIGINT NOT NULL,
    requested_by BIGINT NOT NULL,
    reviewed_by_admin BIGINT,
    reviewed_by_franchisee BIGINT,
    status VARCHAR(50) NOT NULL,
    request_notes TEXT,
    admin_notes TEXT,
    franchisee_notes TEXT,
    rejection_reason TEXT,
    district VARCHAR(255),
    admin_reviewed BOOLEAN DEFAULT FALSE,
    franchisee_reviewed BOOLEAN DEFAULT FALSE,
    admin_approved BOOLEAN,
    franchisee_approved BOOLEAN,
    submitted_at TIMESTAMP,
    admin_reviewed_at TIMESTAMP,
    franchisee_reviewed_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_franchisee_request BOOLEAN DEFAULT FALSE,
    franchisee_id BIGINT,
    
    CONSTRAINT fk_update_request_property FOREIGN KEY (property_id) REFERENCES properties(id),
    CONSTRAINT fk_update_request_user FOREIGN KEY (requested_by) REFERENCES users(id),
    CONSTRAINT fk_update_request_admin FOREIGN KEY (reviewed_by_admin) REFERENCES users(id),
    CONSTRAINT fk_update_request_franchisee_reviewer FOREIGN KEY (reviewed_by_franchisee) REFERENCES users(id),
    CONSTRAINT fk_update_request_franchisee FOREIGN KEY (franchisee_id) REFERENCES users(id)
);

-- Create property update fields table (for old values)
CREATE TABLE property_update_fields (
    update_request_id BIGINT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    old_value TEXT,
    
    PRIMARY KEY (update_request_id, field_name),
    CONSTRAINT fk_update_fields_request FOREIGN KEY (update_request_id) REFERENCES property_update_requests(id) ON DELETE CASCADE
);

-- Create property update new fields table (for new values)
CREATE TABLE property_update_new_fields (
    update_request_id BIGINT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    new_value TEXT,
    
    PRIMARY KEY (update_request_id, field_name),
    CONSTRAINT fk_update_new_fields_request FOREIGN KEY (update_request_id) REFERENCES property_update_requests(id) ON DELETE CASCADE
);

-- Create index for faster queries
CREATE INDEX idx_property_update_requests_status ON property_update_requests(status);
CREATE INDEX idx_property_update_requests_property_id ON property_update_requests(property_id);
CREATE INDEX idx_property_update_requests_requested_by ON property_update_requests(requested_by);
CREATE INDEX idx_property_update_requests_district ON property_update_requests(district); 