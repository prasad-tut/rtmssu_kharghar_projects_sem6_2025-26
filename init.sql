-- Create databases for microservices
CREATE DATABASE IF NOT EXISTS user_db;
CREATE DATABASE IF NOT EXISTS ticket_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON user_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON ticket_db.* TO 'root'@'%';
FLUSH PRIVILEGES;