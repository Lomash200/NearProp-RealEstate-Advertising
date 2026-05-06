-- Add missing column to properties table
ALTER TABLE properties
ADD COLUMN IF NOT EXISTS added_by_franchisee BOOLEAN DEFAULT FALSE; 