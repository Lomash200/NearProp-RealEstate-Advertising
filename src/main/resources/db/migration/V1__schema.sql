-- NearProp Complete Database Schema
-- This file contains all table definitions consolidated from migration files

-- Make sure functions don't exist first
DROP FUNCTION IF EXISTS generate_nearprop_id() CASCADE;
DROP FUNCTION IF EXISTS update_updated_at_column() CASCADE;
DROP FUNCTION IF EXISTS set_permanent_id() CASCADE;

-- Custom ID Generator Function
CREATE OR REPLACE FUNCTION generate_nearprop_id() 
RETURNS TEXT AS $$
DECLARE
    date_part TEXT;
    time_part TEXT;
    random_suffix TEXT;
    complete_id TEXT;
    counter INT;
BEGIN
    -- Create date part in YYYYMMDD format
    date_part := TO_CHAR(CURRENT_TIMESTAMP, 'YYYYMMDD');
    
    -- Create time part with milliseconds
    time_part := TO_CHAR(CURRENT_TIMESTAMP, 'HH24MISSMS');
    
    -- Add random 3-digit suffix to ensure uniqueness even with multiple creations in the same millisecond
    random_suffix := LPAD(FLOOR(RANDOM() * 1000)::TEXT, 3, '0');
    
    -- Combine parts (RNPU prefix + YYYYMMDD date + TTTT time in ms + random suffix)
    complete_id := 'RNPU' || date_part || SUBSTRING(time_part, 1, 4) || random_suffix;
    
    RETURN complete_id;
END;
$$ LANGUAGE plpgsql;

-- Update timestamp function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Add permanent_id function
CREATE OR REPLACE FUNCTION set_permanent_id() 
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.permanent_id IS NULL THEN
        NEW.permanent_id := generate_nearprop_id();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Base Tables (No Dependencies)
CREATE TABLE IF NOT EXISTS users (
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
    last_login_at TIMESTAMP,
    password VARCHAR(255),
    profile_image_url VARCHAR(255),
    aadhaar_document_url VARCHAR(255),
    permanent_id TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS districts (
    id BIGSERIAL PRIMARY KEY,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    city VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    name VARCHAR(100) NOT NULL,
    pincode VARCHAR(20),
    radius_km DOUBLE PRECISION,
    revenue_share_percentage DECIMAL(5, 2) NOT NULL,
    state VARCHAR(100) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    duration_days INTEGER NOT NULL,
    features TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);

-- Tables with Single Dependencies
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(36) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    device_info VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    last_accessed_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS otps (
    id BIGSERIAL PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(15),
    email VARCHAR(255),
    type VARCHAR(10) NOT NULL,
    code VARCHAR(6) NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    attempts INTEGER NOT NULL DEFAULT 0,
    blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verified_at TIMESTAMP,
    expires_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_preferences (
    user_id BIGINT PRIMARY KEY,
    language VARCHAR(50),
    email_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT TRUE,
    app_notifications BOOLEAN DEFAULT TRUE,
    temperature_unit VARCHAR(10),
    distance_unit VARCHAR(10),
    currency VARCHAR(10),
    date_format VARCHAR(50),
    time_format VARCHAR(50),
    theme VARCHAR(50),
    dashboard_view VARCHAR(50),
    property_view_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS role_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    requested_role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admin_comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    processed_by BIGINT,
    comment TEXT,
    reason TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS role_request_documents (
    role_request_id BIGINT NOT NULL,
    document_url TEXT NOT NULL,
    FOREIGN KEY (role_request_id) REFERENCES role_requests(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS franchise_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    district_id BIGINT NOT NULL,
    documents TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    processed_by BIGINT,
    rejection_reason TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (district_id) REFERENCES districts(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS franchisee_districts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    district_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (district_id) REFERENCES districts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS coupons (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(10, 2) NOT NULL,
    min_purchase_amount DECIMAL(10, 2),
    max_discount_amount DECIMAL(10, 2),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    usage_limit INTEGER,
    used_count INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tables with Multiple Dependencies
CREATE TABLE IF NOT EXISTS properties (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    type VARCHAR(20) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    area DOUBLE PRECISION NOT NULL,
    address VARCHAR(255) NOT NULL,
    district VARCHAR(255) NOT NULL,
    city VARCHAR(255),
    state VARCHAR(255),
    pincode VARCHAR(20),
    bedrooms INT NOT NULL,
    bathrooms INT NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    status VARCHAR(20) NOT NULL,
    owner_id BIGINT NOT NULL,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    district_id BIGINT REFERENCES districts(id) ON DELETE SET NULL,
    district_name VARCHAR(100) NOT NULL DEFAULT 'Unknown',
    subscription_id BIGINT,
    permanent_id TEXT UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    agreement_accepted BOOLEAN NOT NULL DEFAULT FALSE,
    availability VARCHAR(50),
    garage_size DOUBLE PRECISION,
    garages INT,
    label VARCHAR(50),
    land_area DOUBLE PRECISION,
    land_area_postfix VARCHAR(20),
    note TEXT,
    place_name VARCHAR(255),
    private_note TEXT,
    renovated BOOLEAN DEFAULT FALSE,
    scheduled_deletion TIMESTAMP,
    size_postfix VARCHAR(20),
    street_number VARCHAR(50),
    subscription_expiry TIMESTAMP,
    video_url TEXT,
    year_built INTEGER,
    youtube_url TEXT,
    owner_permanent_id TEXT,
    added_by_user_id BIGINT,
    added_by_franchisee BOOLEAN DEFAULT FALSE,
    stock INT DEFAULT 0,
    unit_count INT,
    unit_type VARCHAR(50),
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Add trigger for custom ID generation
CREATE TRIGGER trigger_set_permanent_id
BEFORE INSERT ON properties
FOR EACH ROW
EXECUTE FUNCTION set_permanent_id();

CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    property_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    amount_paid DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS payment_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(50),
    transaction_id VARCHAR(255),
    entity_type VARCHAR(50),
    entity_id BIGINT,
    subscription_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS district_revenues (
    id BIGSERIAL PRIMARY KEY,
    district_id BIGINT NOT NULL,
    franchisee_district_id BIGINT NOT NULL,
    property_id BIGINT,
    subscription_id BIGINT,
    revenue_type VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (district_id) REFERENCES districts(id) ON DELETE CASCADE,
    FOREIGN KEY (franchisee_district_id) REFERENCES franchisee_districts(id) ON DELETE CASCADE,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE SET NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL
);

-- Property Related Tables
CREATE TABLE IF NOT EXISTS property_amenities (
    property_id BIGINT NOT NULL,
    amenity VARCHAR(100) NOT NULL,
    PRIMARY KEY (property_id, amenity),
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS property_images (
    property_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    image_order INT NOT NULL,
    PRIMARY KEY (property_id, image_order),
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS property_update_requests (
    id BIGSERIAL PRIMARY KEY,
    request_id TEXT UNIQUE,
    property_id BIGINT NOT NULL,
    requested_by BIGINT NOT NULL,
    reviewed_by_admin BIGINT,
    reviewed_by_franchisee BIGINT,
    status VARCHAR(20) DEFAULT 'PENDING',
    request_notes TEXT,
    admin_notes TEXT,
    franchisee_notes TEXT,
    rejection_reason TEXT,
    district VARCHAR(255),
    admin_reviewed BOOLEAN DEFAULT FALSE,
    franchisee_reviewed BOOLEAN DEFAULT FALSE,
    admin_approved BOOLEAN DEFAULT FALSE,
    franchisee_approved BOOLEAN DEFAULT FALSE,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    admin_reviewed_at TIMESTAMP,
    franchisee_reviewed_at TIMESTAMP,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    FOREIGN KEY (requested_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by_admin) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (reviewed_by_franchisee) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS property_update_fields (
    request_id BIGINT NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    PRIMARY KEY (request_id, field_name),
    FOREIGN KEY (request_id) REFERENCES property_update_requests(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS property_update_new_fields (
    request_id BIGINT NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    field_value TEXT,
    PRIMARY KEY (request_id, field_name),
    FOREIGN KEY (request_id) REFERENCES property_update_requests(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS property_additional_details (
    property_id BIGINT NOT NULL,
    key VARCHAR(100) NOT NULL,
    value TEXT,
    PRIMARY KEY (property_id, key),
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS property_features (
    property_id BIGINT NOT NULL,
    feature VARCHAR(100) NOT NULL,
    PRIMARY KEY (property_id, feature),
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS property_luxurious_features (
    property_id BIGINT NOT NULL,
    feature VARCHAR(100) NOT NULL,
    PRIMARY KEY (property_id, feature),
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS property_security_features (
    property_id BIGINT NOT NULL,
    feature VARCHAR(100) NOT NULL,
    PRIMARY KEY (property_id, feature),
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_favorites (
    user_id BIGINT NOT NULL,
    property_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, property_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS property_reviews (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS review_likes (
    review_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (review_id, user_id),
    FOREIGN KEY (review_id) REFERENCES property_reviews(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS property_visits (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    visit_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create subscription_plan_features table
CREATE TABLE IF NOT EXISTS subscription_plan_features (
    plan_id BIGINT NOT NULL,
    feature VARCHAR(100) NOT NULL,
    PRIMARY KEY (plan_id, feature),
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id) ON DELETE CASCADE
);

-- Create franchisee_withdrawal_requests table
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
-- Social Features
CREATE TABLE IF NOT EXISTS chat_rooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(20) NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_message_at TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_room_participants (
    chat_room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_read_at TIMESTAMP,
    PRIMARY KEY (chat_room_id, user_id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGSERIAL PRIMARY KEY,
    chat_room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    message_type VARCHAR(20) DEFAULT 'TEXT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_attachments (
    id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    file_type VARCHAR(50),
    file_size BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (message_id) REFERENCES chat_messages(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS message_reports (
    id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL,
    reported_by BIGINT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (message_id) REFERENCES chat_messages(id) ON DELETE CASCADE,
    FOREIGN KEY (reported_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Reels and Content
CREATE TABLE IF NOT EXISTS property_reels (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    video_url TEXT NOT NULL,
    thumbnail_url TEXT,
    caption TEXT,
    location VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    city VARCHAR(100),
    district VARCHAR(100),
    views_count INTEGER DEFAULT 0,
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    shares_count INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    public_id TEXT UNIQUE,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reel_interactions (
    id BIGSERIAL PRIMARY KEY,
    reel_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reel_id) REFERENCES property_reels(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Advertisements
CREATE TABLE IF NOT EXISTS advertisements (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url TEXT,
    target_url TEXT,
    district_id BIGINT,
    created_by BIGINT NOT NULL,
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    additional_info JSONB,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    budget DECIMAL(10, 2),
    spent_amount DECIMAL(10, 2) DEFAULT 0,
    clicks_count INTEGER DEFAULT 0,
    impressions_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (district_id) REFERENCES districts(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS advertisement_districts (
    advertisement_id BIGINT NOT NULL,
    district_id BIGINT NOT NULL,
    PRIMARY KEY (advertisement_id, district_id),
    FOREIGN KEY (advertisement_id) REFERENCES advertisements(id) ON DELETE CASCADE,
    FOREIGN KEY (district_id) REFERENCES districts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS advertisement_clicks (
    id BIGSERIAL PRIMARY KEY,
    advertisement_id BIGINT NOT NULL,
    user_id BIGINT,
    ip_address VARCHAR(45),
    click_type VARCHAR(50),
    referrer VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_agent TEXT,
    user_district VARCHAR(255),
    clicked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (advertisement_id) REFERENCES advertisements(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- User Following
CREATE TABLE IF NOT EXISTS user_following (
    follower_id BIGINT NOT NULL,
    followed_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, followed_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (followed_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create Indexes
CREATE INDEX IF NOT EXISTS idx_user_mobile ON users(mobile_number);
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_otp_identifier_type ON otps(identifier, type);
CREATE INDEX IF NOT EXISTS idx_role_requests_status ON role_requests(status);
CREATE INDEX IF NOT EXISTS idx_user_sessions_user ON user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_districts_city ON districts(city);
CREATE INDEX IF NOT EXISTS idx_districts_state ON districts(state);
CREATE INDEX IF NOT EXISTS idx_franchisee_districts_user_id ON franchisee_districts(user_id);
CREATE INDEX IF NOT EXISTS idx_franchisee_districts_district_id ON franchisee_districts(district_id);
CREATE INDEX IF NOT EXISTS idx_franchisee_districts_status ON franchisee_districts(status);
CREATE INDEX IF NOT EXISTS idx_district_revenues_district_id ON district_revenues(district_id);
CREATE INDEX IF NOT EXISTS idx_district_revenues_franchisee_district_id ON district_revenues(franchisee_district_id);
CREATE INDEX IF NOT EXISTS idx_district_revenues_property_id ON district_revenues(property_id);
CREATE INDEX IF NOT EXISTS idx_district_revenues_subscription_id ON district_revenues(subscription_id);
CREATE INDEX IF NOT EXISTS idx_district_revenues_revenue_type ON district_revenues(revenue_type);
CREATE INDEX IF NOT EXISTS idx_district_revenues_payment_status ON district_revenues(payment_status);
CREATE INDEX IF NOT EXISTS idx_district_revenues_transaction_date ON district_revenues(transaction_date);
CREATE INDEX IF NOT EXISTS idx_properties_district_id ON properties(district_id);
CREATE INDEX IF NOT EXISTS idx_properties_permanent_id ON properties(permanent_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_property_id ON subscriptions(property_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_status ON subscriptions(status);
CREATE INDEX IF NOT EXISTS idx_subscriptions_end_date ON subscriptions(end_date);
CREATE INDEX IF NOT EXISTS idx_payment_transactions_user_id ON payment_transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_payment_transactions_status ON payment_transactions(status);
CREATE INDEX IF NOT EXISTS idx_payment_transactions_entity ON payment_transactions(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_reel_interactions_reel_id ON reel_interactions(reel_id);
CREATE INDEX IF NOT EXISTS idx_reel_interactions_user_id ON reel_interactions(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_room_id ON chat_messages(chat_room_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_sender_id ON chat_messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_properties_owner_id ON properties(owner_id);
CREATE INDEX IF NOT EXISTS idx_properties_status ON properties(status);
CREATE INDEX IF NOT EXISTS idx_property_reviews_property_id ON property_reviews(property_id);
CREATE INDEX IF NOT EXISTS idx_property_additional_details_property_id ON property_additional_details(property_id);
CREATE INDEX IF NOT EXISTS idx_property_features_property_id ON property_features(property_id);
CREATE INDEX IF NOT EXISTS idx_property_luxurious_features_property_id ON property_luxurious_features(property_id);
CREATE INDEX IF NOT EXISTS idx_property_security_features_property_id ON property_security_features(property_id);
CREATE INDEX IF NOT EXISTS idx_property_reels_property_id ON property_reels(property_id);
CREATE INDEX IF NOT EXISTS idx_property_reels_user_id ON property_reels(user_id);
CREATE INDEX IF NOT EXISTS idx_property_reels_public_id ON property_reels(public_id);
CREATE INDEX IF NOT EXISTS idx_property_reels_location ON property_reels(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_property_reels_city ON property_reels(city);
CREATE INDEX IF NOT EXISTS idx_property_reels_district ON property_reels(district);
CREATE INDEX IF NOT EXISTS idx_advertisements_created_by ON advertisements(created_by);
CREATE INDEX IF NOT EXISTS idx_advertisements_district_id ON advertisements(district_id);
CREATE INDEX IF NOT EXISTS idx_advertisements_active ON advertisements(active);
CREATE INDEX IF NOT EXISTS idx_advertisement_clicks_advertisement_id ON advertisement_clicks(advertisement_id);
CREATE INDEX IF NOT EXISTS idx_advertisement_clicks_user_id ON advertisement_clicks(user_id);
CREATE INDEX IF NOT EXISTS idx_property_visits_property_id ON property_visits(property_id);
CREATE INDEX IF NOT EXISTS idx_property_visits_user_id ON property_visits(user_id);
CREATE INDEX IF NOT EXISTS idx_property_visits_status ON property_visits(status);
CREATE INDEX IF NOT EXISTS idx_property_update_requests_district ON property_update_requests USING btree (district);
CREATE INDEX IF NOT EXISTS idx_property_update_requests_property_id ON property_update_requests USING btree (property_id);
CREATE INDEX IF NOT EXISTS idx_property_update_requests_requested_by ON property_update_requests USING btree (requested_by);
CREATE INDEX IF NOT EXISTS idx_property_update_requests_status ON property_update_requests USING btree (status);
CREATE INDEX IF NOT EXISTS idx_review_likes_review_id ON review_likes(review_id);
CREATE INDEX IF NOT EXISTS idx_review_likes_user_id ON review_likes(user_id);
CREATE INDEX IF NOT EXISTS idx_property_reviews_user_id ON property_reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_created_at ON chat_messages(created_at);

-- Add update timestamp trigger
CREATE TRIGGER update_properties_updated_at
BEFORE UPDATE ON properties
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();