-- Drop the existing constraint
ALTER TABLE properties DROP CONSTRAINT IF EXISTS properties_label_check;

-- Add the new constraint with the DEVELOPER value
ALTER TABLE properties ADD CONSTRAINT properties_label_check
    CHECK (label::text = ANY (ARRAY['GOLDEN_OFFER'::text, 'HOT_OFFER'::text, 'OPEN_HOUSE'::text, 'SOLD'::text, 'DEVELOPER'::text])); 