-- Add fields to chat_messages table for threading, admin messages, and moderation
ALTER TABLE chat_messages
ADD COLUMN parent_message_id BIGINT,
ADD COLUMN is_admin_message BOOLEAN DEFAULT FALSE,
ADD COLUMN is_edited BOOLEAN DEFAULT FALSE,
ADD COLUMN edited_at TIMESTAMP,
ADD COLUMN is_reported BOOLEAN DEFAULT FALSE,
ADD COLUMN is_warned BOOLEAN DEFAULT FALSE;

-- Add foreign key constraint for parent_message_id
ALTER TABLE chat_messages
ADD CONSTRAINT fk_parent_message
FOREIGN KEY (parent_message_id) REFERENCES chat_messages(id) ON DELETE SET NULL;

-- Create message reports table
CREATE TABLE message_reports (
    id SERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL,
    reporter_id BIGINT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(20) NOT NULL,
    admin_note VARCHAR(1000),
    processed_by_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    FOREIGN KEY (message_id) REFERENCES chat_messages(id) ON DELETE CASCADE,
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by_id) REFERENCES users(id) ON DELETE SET NULL
); 