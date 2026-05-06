-- Add profile_image_url column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(512);

-- Update with default profile image URLs for existing users
UPDATE users 
SET profile_image_url = 'https://nearprop-documents.s3.ap-south-1.amazonaws.com/defaults/default-user-profile.png'
WHERE profile_image_url IS NULL; 