-- Create the reels table
CREATE TABLE property_reels (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    video_url VARCHAR(1024) NOT NULL,
    thumbnail_url VARCHAR(1024),
    duration_seconds INTEGER,
    file_size BIGINT,
    status VARCHAR(50) NOT NULL,
    processing_status VARCHAR(50) NOT NULL,
    public_id VARCHAR(255) UNIQUE NOT NULL,
    view_count BIGINT DEFAULT 0,
    like_count BIGINT DEFAULT 0,
    comment_count BIGINT DEFAULT 0,
    share_count BIGINT DEFAULT 0,
    save_count BIGINT DEFAULT 0,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    district VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create index for faster lookups
CREATE INDEX idx_reels_property_id ON property_reels(property_id);
CREATE INDEX idx_reels_user_id ON property_reels(user_id);
CREATE INDEX idx_reels_public_id ON property_reels(public_id);
CREATE INDEX idx_reels_location ON property_reels(latitude, longitude);
CREATE INDEX idx_reels_city ON property_reels(city);
CREATE INDEX idx_reels_district ON property_reels(district);

-- Create the reel interactions table
CREATE TABLE reel_interactions (
    id BIGSERIAL PRIMARY KEY,
    reel_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reel_id) REFERENCES property_reels(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create index for faster lookups
CREATE INDEX idx_reel_interactions_reel_id ON reel_interactions(reel_id);
CREATE INDEX idx_reel_interactions_user_id ON reel_interactions(user_id);
CREATE INDEX idx_reel_interactions_type ON reel_interactions(type);
CREATE UNIQUE INDEX idx_reel_interaction_unique ON reel_interactions(reel_id, user_id, type);

-- Create the subscription plan features table
CREATE TABLE subscription_plan_features (
    id BIGSERIAL PRIMARY KEY,
    plan_name VARCHAR(100) UNIQUE NOT NULL,
    plan_type VARCHAR(50) NOT NULL,
    max_properties INTEGER NOT NULL,
    max_reels_per_property INTEGER NOT NULL,
    max_total_reels INTEGER NOT NULL,
    max_reel_duration_seconds INTEGER NOT NULL,
    max_reel_file_size_mb INTEGER NOT NULL,
    allowed_video_formats VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    monthly_price DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default subscription plans
INSERT INTO subscription_plan_features 
    (plan_name, plan_type, max_properties, max_reels_per_property, max_total_reels, max_reel_duration_seconds, max_reel_file_size_mb, allowed_video_formats, is_active, monthly_price)
VALUES 
    ('Free User', 'USER', 0, 0, 0, 0, 0, 'none', TRUE, 0.0),
    ('Basic Seller', 'SELLER', 5, 2, 10, 60, 40, 'mp4,mov,avi', TRUE, 499.0),
    ('Premium Seller', 'SELLER', 15, 5, 30, 120, 100, 'mp4,mov,avi,webm', TRUE, 999.0),
    ('Basic Advisor', 'ADVISOR', 10, 3, 15, 60, 40, 'mp4,mov,avi', TRUE, 799.0),
    ('Premium Advisor', 'ADVISOR', 30, 10, 50, 120, 100, 'mp4,mov,avi,webm', TRUE, 1499.0),
    ('Basic Developer', 'DEVELOPER', 50, 10, 100, 120, 100, 'mp4,mov,avi,webm', TRUE, 2999.0),
    ('Premium Developer', 'DEVELOPER', 100, 20, 200, 180, 200, 'mp4,mov,avi,webm,mkv', TRUE, 4999.0); 