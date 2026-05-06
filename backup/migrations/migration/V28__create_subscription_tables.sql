-- Create subscription plans table
CREATE TABLE subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('SELLER', 'ADVISOR', 'DEVELOPER', 'FRANCHISEE')),
    price DECIMAL(10, 2) NOT NULL,
    duration_days INTEGER NOT NULL,
    max_properties INTEGER,
    max_reels_per_property INTEGER,
    max_total_reels INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create subscriptions table
CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id BIGINT NOT NULL REFERENCES subscription_plans(id),
    price DECIMAL(10, 2) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'EXPIRED', 'CANCELLED', 'PENDING_PAYMENT')),
    property_id BIGINT REFERENCES properties(id) ON DELETE SET NULL,
    payment_reference VARCHAR(100),
    auto_renew BOOLEAN DEFAULT FALSE,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on user_id for faster user subscription lookup
CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);

-- Create index on property_id for faster property subscription lookup
CREATE INDEX idx_subscriptions_property_id ON subscriptions(property_id);

-- Create index on status for faster status filtering
CREATE INDEX idx_subscriptions_status ON subscriptions(status);

-- Create index on end_date to help with expiration processing
CREATE INDEX idx_subscriptions_end_date ON subscriptions(end_date);

-- Insert default subscription plans
INSERT INTO subscription_plans (name, description, type, price, duration_days, max_properties, active)
VALUES ('Property Listing Basic', 'Basic property listing subscription - 300 INR per property per month', 'SELLER', 300.00, 30, 1, TRUE);

INSERT INTO subscription_plans (name, description, type, price, duration_days, max_properties, max_reels_per_property, max_total_reels, active)
VALUES ('Advisor Basic', 'Basic plan for property advisors with up to 10 properties', 'ADVISOR', 1500.00, 30, 10, 3, 30, TRUE);

INSERT INTO subscription_plans (name, description, type, price, duration_days, max_properties, max_reels_per_property, max_total_reels, active)
VALUES ('Advisor Premium', 'Premium plan for property advisors with up to 50 properties', 'ADVISOR', 3000.00, 30, 50, 5, 100, TRUE);

INSERT INTO subscription_plans (name, description, type, price, duration_days, max_properties, max_reels_per_property, max_total_reels, active)
VALUES ('Developer Standard', 'Standard plan for property developers', 'DEVELOPER', 5000.00, 30, 100, 10, 250, TRUE);

INSERT INTO subscription_plans (name, description, type, price, duration_days, active)
VALUES ('Franchisee Standard', 'Standard plan for district franchisees with 50% revenue share', 'FRANCHISEE', 50000.00, 365, TRUE); 