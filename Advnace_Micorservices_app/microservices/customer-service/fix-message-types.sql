-- Update existing messages to have REGULAR messageType
-- This fixes any NULL values that existed before the migration

UPDATE messages 
SET message_type = 'REGULAR' 
WHERE message_type IS NULL;
