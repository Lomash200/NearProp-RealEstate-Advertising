-- Add index on subscription_id in properties table for faster lookups
CREATE INDEX IF NOT EXISTS idx_properties_subscription_id ON properties(subscription_id);

-- Add index on owner_id and active status for faster lookups of inactive properties by owner
CREATE INDEX IF NOT EXISTS idx_properties_owner_active ON properties(owner_id, active);

-- Add index on subscription_id and expiry date for faster lookups
CREATE INDEX IF NOT EXISTS idx_properties_subscription_expiry ON properties(subscription_id, subscription_expiry);

-- Add indexes for faster property lookups by subscription ID and activation status
CREATE INDEX IF NOT EXISTS idx_property_active ON properties (active);
CREATE INDEX IF NOT EXISTS idx_property_approved ON properties (approved);
CREATE INDEX IF NOT EXISTS idx_property_owner_id ON properties (owner_id);
CREATE INDEX IF NOT EXISTS idx_property_subscription_expiry ON properties (subscription_expiry);
CREATE INDEX IF NOT EXISTS idx_property_owner_subscription ON properties (owner_id, subscription_id);

-- Add indexes for faster subscription lookups
CREATE INDEX IF NOT EXISTS idx_subscription_user_id ON subscriptions (user_id);
CREATE INDEX IF NOT EXISTS idx_subscription_status ON subscriptions (status);
CREATE INDEX IF NOT EXISTS idx_subscription_end_date ON subscriptions (end_date);
CREATE INDEX IF NOT EXISTS idx_subscription_plan_type ON subscriptions (plan_id); 