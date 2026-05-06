-- Create the user following table
CREATE TABLE user_following (
    id BIGSERIAL PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    followed_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (followed_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_following UNIQUE (follower_id, followed_id)
);

-- Create indexes for faster lookups
CREATE INDEX idx_user_following_follower ON user_following(follower_id);
CREATE INDEX idx_user_following_followed ON user_following(followed_id); 