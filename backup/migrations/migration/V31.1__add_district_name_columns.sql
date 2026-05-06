-- Add district_name and state columns to franchisee_districts
ALTER TABLE franchisee_districts ADD COLUMN IF NOT EXISTS district_name VARCHAR(255);
ALTER TABLE franchisee_districts ADD COLUMN IF NOT EXISTS state VARCHAR(255);

-- Update franchisee_districts table to include district names and state
UPDATE franchisee_districts fd
SET district_name = d.name, state = d.state
FROM districts d
WHERE fd.district_id = d.id;

-- Add NOT NULL constraints to the new columns if there's data
ALTER TABLE franchisee_districts 
  ALTER COLUMN district_name SET NOT NULL,
  ALTER COLUMN state SET NOT NULL;

-- Add district_id column to district_revenues table if not exists
ALTER TABLE district_revenues ADD COLUMN IF NOT EXISTS district_id BIGINT; 