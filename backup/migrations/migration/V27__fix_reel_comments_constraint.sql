-- Drop the existing unique constraint
ALTER TABLE reel_interactions DROP CONSTRAINT IF EXISTS uk_reel_user_interaction_type;
DROP INDEX IF EXISTS idx_reel_interaction_unique;
DROP INDEX IF EXISTS idx_reel_interaction_unique_non_comment;
DROP INDEX IF EXISTS idx_reel_comment_interactions;

-- Create a new unique constraint that excludes COMMENT type
CREATE UNIQUE INDEX uk_reel_interaction_non_comment 
ON reel_interactions(reel_id, user_id, type) 
WHERE type != 'COMMENT';

-- Create an index for comments for better performance
CREATE INDEX idx_reel_comment_interactions 
ON reel_interactions(reel_id, user_id) 
WHERE type = 'COMMENT'; 