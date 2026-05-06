-- Add SELLER role to available users
DO $$
BEGIN
    -- Only add the SELLER role to user with ID 4 if it exists
    IF EXISTS (SELECT 1 FROM users WHERE id = 4) THEN
INSERT INTO user_roles (user_id, role)
VALUES (4, 'SELLER'); 
    END IF;
END $$; 