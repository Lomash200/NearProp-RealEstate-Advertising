-- Add fields to chat_attachments table
ALTER TABLE chat_attachments
ADD COLUMN uploader_id BIGINT,
ADD COLUMN chat_room_id BIGINT,
ADD COLUMN file_path VARCHAR(512),
ADD COLUMN thumbnail_path VARCHAR(512);

-- Add foreign key constraints
ALTER TABLE chat_attachments
ADD CONSTRAINT fk_uploader
FOREIGN KEY (uploader_id) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE chat_attachments
ADD CONSTRAINT fk_chat_room
FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE; 