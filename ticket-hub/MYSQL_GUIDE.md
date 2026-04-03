# Ticket System - MySQL Setup Guide

Follow these steps to connect the app with your local MySQL database.

## 1. Prepare the Database
Run these commands in your MySQL Command Line or MySQL Workbench:

```sql
-- Create the main database
CREATE DATABASE IF NOT EXISTS ticketsystem;

-- (Optional) Create a specific user if you don't want to use 'root'
-- CREATE USER 'ticketsystem_user'@'localhost' IDENTIFIED BY 'password';
-- GRANT ALL PRIVILEGES ON ticketsystem.* TO 'ticketsystem_user'@'localhost';
-- FLUSH PRIVILEGES;
```

## 2. Update Connection Details
Ensure the credentials in the Following `application.properties` files match your MySQL setup.
**Default Config:**
- **URL**: `jdbc:mysql://localhost:3306/ticketsystem`
- **User**: `root`
- **Password**: `password`

**Files to Check:**
- [auth-user-service properties](file:///c:/Users/Khushboo/Downloads/AWS/ticket-hub/microservices/auth-user-service/src/main/resources/application.properties)
- [ticket-service properties](file:///c:/Users/Khushboo/Downloads/AWS/ticket-hub/microservices/ticket-service/src/main/resources/application.properties)

## 3. Run the App
Simply double-click the [restart_app.bat](file:///c:/Users/Khushboo/Downloads/AWS/ticket-hub/restart_app.bat) in the root folder. It will:
- Re-sync UI assets.
- Start all backend services in the background.

## 4. Verify
Access the dashboard at: [http://localhost:9100](http://localhost:9100)
