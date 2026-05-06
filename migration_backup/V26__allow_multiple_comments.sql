-- Remove the existing unique constraint
DROP INDEX IF EXISTS idx_reel_interaction_unique;

-- Create a new unique constraint that excludes COMMENT type
CREATE UNIQUE INDEX idx_reel_interaction_unique_non_comment 
ON reel_interactions(reel_id, user_id, type) 
WHERE type != 'COMMENT';

-- Create an index for comments for better performance
CREATE INDEX idx_reel_comment_interactions 
ON reel_interactions(reel_id, user_id) 
WHERE type = 'COMMENT'; 