-- Add missing columns if they don't exist
ALTER TABLE advertisements ADD COLUMN IF NOT EXISTS click_count bigint;
ALTER TABLE advertisements ADD COLUMN IF NOT EXISTS social_media_clicks bigint;
ALTER TABLE advertisements ADD COLUMN IF NOT EXISTS view_count bigint;
ALTER TABLE advertisements ADD COLUMN IF NOT EXISTS website_clicks bigint;
ALTER TABLE advertisements ADD COLUMN IF NOT EXISTS whatsapp_clicks bigint;
ALTER TABLE advertisements ADD COLUMN IF NOT EXISTS phone_clicks bigint;
ALTER TABLE advertisements ADD COLUMN IF NOT EXISTS twitter_url varchar(255);
ALTER TABLE advertisements ADD COLUMN IF NOT EXISTS linkedin_url varchar(255);

-- Update columns with default values
UPDATE advertisements SET click_count = 0 WHERE click_count IS NULL;
UPDATE advertisements SET social_media_clicks = 0 WHERE social_media_clicks IS NULL;
UPDATE advertisements SET view_count = 0 WHERE view_count IS NULL;
UPDATE advertisements SET website_clicks = 0 WHERE website_clicks IS NULL;
UPDATE advertisements SET whatsapp_clicks = 0 WHERE whatsapp_clicks IS NULL;
UPDATE advertisements SET phone_clicks = 0 WHERE phone_clicks IS NULL;

-- Add NOT NULL constraints
ALTER TABLE advertisements ALTER COLUMN click_count SET NOT NULL;
ALTER TABLE advertisements ALTER COLUMN social_media_clicks SET NOT NULL;
ALTER TABLE advertisements ALTER COLUMN view_count SET NOT NULL;
ALTER TABLE advertisements ALTER COLUMN website_clicks SET NOT NULL;
ALTER TABLE advertisements ALTER COLUMN whatsapp_clicks SET NOT NULL;
ALTER TABLE advertisements ALTER COLUMN phone_clicks SET NOT NULL;

-- Set default values for new advertisements
ALTER TABLE advertisements ALTER COLUMN social_media_clicks SET DEFAULT 0;
ALTER TABLE advertisements ALTER COLUMN view_count SET DEFAULT 0;
ALTER TABLE advertisements ALTER COLUMN website_clicks SET DEFAULT 0;
ALTER TABLE advertisements ALTER COLUMN whatsapp_clicks SET DEFAULT 0;
ALTER TABLE advertisements ALTER COLUMN phone_clicks SET DEFAULT 0;
ALTER TABLE advertisements ALTER COLUMN click_count SET DEFAULT 0; 