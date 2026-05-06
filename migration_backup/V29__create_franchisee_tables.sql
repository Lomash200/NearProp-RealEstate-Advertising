-- Create districts table
CREATE TABLE districts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    pincode VARCHAR(20),
    revenue_share_percentage DECIMAL(5, 2) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    radius_km DOUBLE PRECISION,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on city and state for faster lookups
CREATE INDEX idx_districts_city ON districts(city);
CREATE INDEX idx_districts_state ON districts(state);

-- Create franchisee_districts table
CREATE TABLE franchisee_districts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    district_id BIGINT NOT NULL REFERENCES districts(id) ON DELETE CASCADE,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    revenue_share_percentage DECIMAL(5, 2) NOT NULL,
    total_properties INTEGER DEFAULT 0,
    total_transactions INTEGER DEFAULT 0,
    total_revenue DECIMAL(12, 2) DEFAULT 0,
    total_commission DECIMAL(12, 2) DEFAULT 0,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'SUSPENDED', 'TERMINATED', 'PENDING_APPROVAL')),
    office_address VARCHAR(500),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_franchisee_district UNIQUE (user_id, district_id)
);

-- Create index on user_id and district_id for faster lookups
CREATE INDEX idx_franchisee_districts_user_id ON franchisee_districts(user_id);
CREATE INDEX idx_franchisee_districts_district_id ON franchisee_districts(district_id);
CREATE INDEX idx_franchisee_districts_status ON franchisee_districts(status);

-- Create district_revenues table
CREATE TABLE district_revenues (
    id BIGSERIAL PRIMARY KEY,
    district_id BIGINT NOT NULL REFERENCES districts(id) ON DELETE CASCADE,
    franchisee_district_id BIGINT NOT NULL REFERENCES franchisee_districts(id) ON DELETE CASCADE,
    revenue_type VARCHAR(20) NOT NULL CHECK (revenue_type IN ('PROPERTY_LISTING', 'SUBSCRIPTION_PAYMENT', 'VISIT_BOOKING', 'TRANSACTION_FEE', 'MARKETING_FEE', 'OTHER')),
    property_id BIGINT REFERENCES properties(id) ON DELETE SET NULL,
    subscription_id BIGINT REFERENCES subscriptions(id) ON DELETE SET NULL,
    transaction_id VARCHAR(100),
    transaction_date TIMESTAMP NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    franchisee_commission DECIMAL(12, 2) NOT NULL,
    company_revenue DECIMAL(12, 2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL CHECK (payment_status IN ('PENDING', 'PAID', 'CANCELLED')),
    payment_date TIMESTAMP,
    payment_reference VARCHAR(100),
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indices for faster lookups
CREATE INDEX idx_district_revenues_district_id ON district_revenues(district_id);
CREATE INDEX idx_district_revenues_franchisee_district_id ON district_revenues(franchisee_district_id);
CREATE INDEX idx_district_revenues_property_id ON district_revenues(property_id);
CREATE INDEX idx_district_revenues_subscription_id ON district_revenues(subscription_id);
CREATE INDEX idx_district_revenues_revenue_type ON district_revenues(revenue_type);
CREATE INDEX idx_district_revenues_payment_status ON district_revenues(payment_status);
CREATE INDEX idx_district_revenues_transaction_date ON district_revenues(transaction_date);

-- Add district_id column to properties table
ALTER TABLE properties 
ADD COLUMN district_id BIGINT REFERENCES districts(id) ON DELETE SET NULL,
ADD COLUMN district_name VARCHAR(100) NOT NULL DEFAULT 'Unknown';

-- Create index on district_id in properties table
CREATE INDEX idx_properties_district_id ON properties(district_id); 