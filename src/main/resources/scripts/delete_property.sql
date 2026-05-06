-- Script to forcefully delete a property and all its dependencies
-- Usage: Replace 19 with the actual property ID

-- Disable foreign key constraints temporarily
SET session_replication_role = 'replica';

-- Delete property related data
DELETE FROM property_visits WHERE property_id = 19;
DELETE FROM property_images WHERE property_id = 19;
DELETE FROM property_amenities WHERE property_id = 19;
DELETE FROM property_features WHERE property_id = 19;
DELETE FROM property_security_features WHERE property_id = 19;
DELETE FROM property_luxurious_features WHERE property_id = 19;
DELETE FROM property_additional_details WHERE property_id = 19;

-- Delete advertisements
DELETE FROM advertisements WHERE property_id = 19;

-- Delete property update fields and requests
DELETE FROM property_update_fields 
WHERE property_update_request_id IN (
    SELECT id FROM property_update_requests WHERE property_id = 19
);
DELETE FROM property_update_requests WHERE property_id = 19;

-- Delete reel interactions and comments
DELETE FROM reel_interactions 
WHERE reel_id IN (
    SELECT id FROM reels WHERE property_id = 19
);
DELETE FROM reel_comments 
WHERE reel_id IN (
    SELECT id FROM reels WHERE property_id = 19
);
DELETE FROM reels WHERE property_id = 19;

-- Delete chat related data
DELETE FROM chat_message_reports 
WHERE message_id IN (
    SELECT m.id FROM chat_messages m 
    JOIN chat_rooms r ON m.chat_room_id = r.id 
    WHERE r.property_id = 19
);
DELETE FROM chat_attachments 
WHERE chat_room_id IN (
    SELECT id FROM chat_rooms WHERE property_id = 19
);
DELETE FROM chat_messages 
WHERE chat_room_id IN (
    SELECT id FROM chat_rooms WHERE property_id = 19
);
DELETE FROM chat_room_participants 
WHERE chat_room_id IN (
    SELECT id FROM chat_rooms WHERE property_id = 19
);
DELETE FROM chat_rooms WHERE property_id = 19;

-- Delete user favorites
DELETE FROM user_favorites WHERE property_id = 19;
DELETE FROM user_favorite_properties WHERE property_id = 19;

-- Finally delete the property
DELETE FROM properties WHERE id = 19;

-- Re-enable foreign key constraints
SET session_replication_role = 'origin'; 