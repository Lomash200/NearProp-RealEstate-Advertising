-- Drop foreign key constraints referencing the districts table
ALTER TABLE franchisee_districts DROP CONSTRAINT IF EXISTS fk_franchisee_districts_district;
ALTER TABLE district_revenues DROP CONSTRAINT IF EXISTS fk_district_revenues_district;
ALTER TABLE properties DROP CONSTRAINT IF EXISTS fk_properties_district;

-- Update properties table to include district name and state
ALTER TABLE properties 
  ADD COLUMN IF NOT EXISTS state VARCHAR(255),
  ADD COLUMN IF NOT EXISTS city VARCHAR(255); 

-- Copy data from districts table to properties table
UPDATE properties p
SET state = d.state, city = d.city
FROM districts d
WHERE p.district_id = d.id;

-- Create a district_id column in properties table if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'properties' AND column_name = 'district_id') THEN
        ALTER TABLE properties ADD COLUMN district_id BIGINT;
    END IF;
END $$;

-- Drop the districts table
DROP TABLE IF EXISTS districts CASCADE;

-- Update indexes
CREATE INDEX IF NOT EXISTS idx_properties_district_id ON properties(district_id);
CREATE INDEX IF NOT EXISTS idx_properties_district_name ON properties(district_name);
CREATE INDEX IF NOT EXISTS idx_properties_city ON properties(city);
CREATE INDEX IF NOT EXISTS idx_properties_state ON properties(state); 