-- Custom ID Generator for NearProp
-- Creates IDs in format RNPUYYYYMMDDTTTT where TTTT is time in milliseconds

-- Function to generate custom IDs
CREATE OR REPLACE FUNCTION generate_nearprop_id() 
RETURNS TEXT AS $$
DECLARE
    date_part TEXT;
    time_part TEXT;
    random_suffix TEXT;
    complete_id TEXT;
    counter INT;
BEGIN
    -- Create date part in YYYYMMDD format
    date_part := TO_CHAR(CURRENT_TIMESTAMP, 'YYYYMMDD');
    
    -- Create time part with milliseconds
    time_part := TO_CHAR(CURRENT_TIMESTAMP, 'HH24MISSMS');
    
    -- Add random 3-digit suffix to ensure uniqueness even with multiple creations in the same millisecond
    random_suffix := LPAD(FLOOR(RANDOM() * 1000)::TEXT, 3, '0');
    
    -- Combine parts (RNPU prefix + YYYYMMDD date + TTTT time in ms + random suffix)
    complete_id := 'RNPU' || date_part || SUBSTRING(time_part, 1, 4) || random_suffix;
    
    RETURN complete_id;
END;
$$ LANGUAGE plpgsql;

-- Update properties table to use custom ID generator
ALTER TABLE properties DROP COLUMN IF EXISTS permanent_id CASCADE;
ALTER TABLE properties ADD COLUMN permanent_id TEXT UNIQUE DEFAULT generate_nearprop_id();

-- Create trigger to auto-generate IDs for new properties
CREATE OR REPLACE FUNCTION set_permanent_id()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.permanent_id IS NULL THEN
        NEW.permanent_id := generate_nearprop_id();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_permanent_id
BEFORE INSERT ON properties
FOR EACH ROW
EXECUTE FUNCTION set_permanent_id(); 