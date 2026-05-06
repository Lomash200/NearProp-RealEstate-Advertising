-- Add subscription_id column to properties table
ALTER TABLE properties ADD COLUMN subscription_id BIGINT;

-- Add foreign key constraint
ALTER TABLE properties ADD CONSTRAINT fk_property_subscription 
FOREIGN KEY (subscription_id) REFERENCES subscriptions (id) ON DELETE SET NULL; 