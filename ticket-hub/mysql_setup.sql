-- Ticket System - Full MySQL Setup Script
-- Copy and execute these commands in your MySQL client

-- 1. Create and select the database
CREATE DATABASE IF NOT EXISTS ticketsystem;
USE ticketsystem;

-- 2. Create the Users table (for Auth service)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'CUSTOMER'
);

-- 3. Create the Tickets table (for Ticket service)
CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'OPEN',
    customer_id VARCHAR(100),
    assigned_to VARCHAR(100)
);

-- 4. Initial Sample User (Optional)
-- INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN');

-- Note: The Spring Boot app will automatically create and update these tables 
-- if they don't exist, thanks to the 'spring.jpa.hibernate.ddl-auto=update' setting.
