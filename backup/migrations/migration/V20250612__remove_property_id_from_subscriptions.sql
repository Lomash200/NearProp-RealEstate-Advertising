-- Remove property_id column from subscriptions table
ALTER TABLE subscriptions DROP CONSTRAINT IF EXISTS subscriptions_property_id_fkey;
DROP INDEX IF EXISTS idx_subscriptions_property_id;
ALTER TABLE subscriptions DROP COLUMN IF EXISTS property_id; 