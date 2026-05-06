-- Add new columns to property_reels table
ALTER TABLE property_reels
ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION,
ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION,
ADD COLUMN IF NOT EXISTS district VARCHAR(255),
ADD COLUMN IF NOT EXISTS city VARCHAR(255),
ADD COLUMN IF NOT EXISTS state VARCHAR(255),
ADD COLUMN IF NOT EXISTS save_count BIGINT DEFAULT 0;

-- Create indexes for faster geospatial queries
CREATE INDEX IF NOT EXISTS idx_reels_location ON property_reels(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_reels_city ON property_reels(city);
CREATE INDEX IF NOT EXISTS idx_reels_district ON property_reels(district);

-- Update reels with location data from their properties
UPDATE property_reels pr
SET
    latitude = p.latitude,
    longitude = p.longitude,
    district = p.district,
    city = p.city,
    state = p.state
FROM properties p
WHERE pr.property_id = p.id; 