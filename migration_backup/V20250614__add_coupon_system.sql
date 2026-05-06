-- Create coupons table
CREATE TABLE coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permanent_id VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(255),
    discount_amount DECIMAL(10, 2),
    discount_percentage INT,
    max_discount DECIMAL(10, 2),
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    max_uses INT,
    current_uses INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    discount_type VARCHAR(20) NOT NULL,
    subscription_type VARCHAR(20),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Add coupon-related fields to subscriptions table
ALTER TABLE subscriptions 
ADD COLUMN coupon_id BIGINT,
ADD COLUMN coupon_code VARCHAR(20),
ADD COLUMN original_price DECIMAL(10, 2),
ADD COLUMN discount_amount DECIMAL(10, 2),
ADD FOREIGN KEY (coupon_id) REFERENCES coupons(id);

-- Add index for faster coupon code lookups
CREATE INDEX idx_coupon_code ON coupons(code);

-- Add index for active coupons
CREATE INDEX idx_coupon_active ON coupons(is_active);

-- Add index for coupon validity period
CREATE INDEX idx_coupon_validity ON coupons(valid_from, valid_until); 