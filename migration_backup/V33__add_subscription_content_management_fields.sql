-- Add active column to properties table with default true
ALTER TABLE properties ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT true;

-- Add content management fields to subscription_plans table
ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS content_hide_after_days INTEGER DEFAULT 30;
ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS content_delete_after_days INTEGER DEFAULT 60;
ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS marketing_fee NUMERIC(38,2);

-- Add content management fields to subscriptions table
ALTER TABLE subscriptions ADD COLUMN IF NOT EXISTS is_renewal BOOLEAN DEFAULT false;
ALTER TABLE subscriptions ADD COLUMN IF NOT EXISTS previous_subscription_id BIGINT;
ALTER TABLE subscriptions ADD COLUMN IF NOT EXISTS marketing_fee NUMERIC(38,2);
ALTER TABLE subscriptions ADD COLUMN IF NOT EXISTS content_hidden_at TIMESTAMP;
ALTER TABLE subscriptions ADD COLUMN IF NOT EXISTS content_deleted_at TIMESTAMP;

-- Add constraint for previous_subscription_id foreign key
ALTER TABLE subscriptions 
ADD CONSTRAINT fk_previous_subscription 
FOREIGN KEY (previous_subscription_id) 
REFERENCES subscriptions(id) 
ON DELETE SET NULL; 