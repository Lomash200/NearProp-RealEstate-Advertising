-- Add indexes to improve subscription and property queries for email notifications

-- Index for finding active subscriptions by user ID
CREATE INDEX IF NOT EXISTS idx_subscription_user_id_status
ON subscription (user_id, status);

-- Index for finding properties by user ID and status
CREATE INDEX IF NOT EXISTS idx_property_user_id_status
ON property (user_id, status);

-- Index for finding properties by subscription ID
CREATE INDEX IF NOT EXISTS idx_property_subscription_id
ON property (subscription_id);

-- Index for finding subscriptions by plan type
CREATE INDEX IF NOT EXISTS idx_subscription_plan_type
ON subscription_plan (type);

-- Add column to track email notification status if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                  WHERE table_name='subscription' AND column_name='email_notification_sent') THEN
        ALTER TABLE subscription ADD COLUMN email_notification_sent BOOLEAN DEFAULT FALSE;
    END IF;
END
$$;

-- Add column to track last notification date if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                  WHERE table_name='subscription' AND column_name='last_notification_date') THEN
        ALTER TABLE subscription ADD COLUMN last_notification_date TIMESTAMP;
    END IF;
END
$$; 