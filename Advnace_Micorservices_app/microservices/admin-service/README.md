# 🛡️ Admin Service Codebase Guide

This document provides a detailed breakdown of every single file in the `admin-service`. It is designed to help you understand **what** each file does, **why** it was created, and **how** it fits into the larger picture.

## 📂 Root & Configuration
These files set up the application and define how it runs.

### `pom.xml`
- **What**: The Project Object Model file for Maven.
- **Purpose**: It lists all the external code libraries (dependencies) this service needs to run.
- **Why**: We need libraries for Spring Boot (`spring-boot-starter-web`), Database access (`spring-boot-starter-data-jpa`), PostgreSQL driver, and JSON processing. Without this, we'd have to write millions of lines of basic code ourselves.

### `src/main/resources/application.properties`
- **What**: The central configuration file.
- **Purpose**: Defines **Port 9104**, database connection specific to Admin (AWS RDS), and API keys for AI (Gemini).
- **Why**: To keep settings out of the Java code so they can be changed easily without recompiling.

### `src/main/java/mssu/in/admin_service/AdminServiceApplication.java`
- **What**: The Main Class.
- **Purpose**: Contains the `public static void main` method that starts the Spring Boot application.
- **Why**: Every Java application needs an entry point to begin execution.

---

## 📂 Controllers (`controller/`)
Controllers are the **Traffic Cops**. They listen for incoming HTTP requests (like "Create User") and decide which Service should handle them.

### `DashboardController.java`
- **Purpose**: Handles the main Admin Dashboard statistics.
- **Endpoint**: `GET /api/admin/dashboard/stats`
- **Why**: When an admin logs in, they see a summary (Total Users, Total Revenue). This controller fetches that data so the frontend can display the charts.

### `ProductController.java`
- **Purpose**: Manages the Product Catalog.
- **Endpoints**: `POST /products`, `GET /products`, `DELETE /products/{id}`.
- **Why**: Admins need to add new products (like "iPhone 15") so customers can select them when filing tickets. This file handles the requests to save or list those products.

### `UserManagementController.java`
- **Purpose**: manages hiring/creating new users (specifically Executives).
- **Endpoint**: `POST /users/executive`.
- **Why**: An Admin is the only one who can hire an "Executive" (Support Agent). This controller validates the request and sends it to the Auth Service to actually create the account.

### `ExecutiveManagementController.java`
- **Purpose**: Tracks what executives are doing.
- **Endpoint**: `GET /executives/workload`.
- **Why**: Admins need to see if an agent is overworked (e.g., has 50 active tickets) to decide who to assign new tickets to.

### `TicketManagementController.java`
- **Purpose**: Allows Admins to oversee tickets.
- **Why**: Sometimes an Admin needs to step in and re-assign a ticket or view high-priority issues that are being ignored.

---

## 📂 Services (`service/`)
Services contains the **Business Logic**. This is where the actual work happens (calculations, decisions, data processing).

### `DashboardService.java`
- **What**: Logic for calculating stats.
- **Why**: The controller just asks for "Stats". This service actually goes out, counts the users, sums up the revenue, and packages it into a nice object.

### `ProductService.java`
- **What**: Logic for handling Products.
- **Why**: When adding a product, we might need to validate the price or format the name. This service prepares the product data before saving it to the database.

### `GeminiClient.java`
- **What**: The AI Connector.
- **Why**: We want "Magic" automation. When an Admin types "iPhone 15", this file sends that text to the Gemini AI API and asks for a professional description and image. It parses the AI's response so we can save it.

### `ExecutiveService.java`
- **What**: Logic for fetching executive workload.
- **Why**: It talks to the `Ticket Service` to count how many tickets are assigned to each executive id.

### `UserManagementService.java`
- **What**: Logic for creating users.
- **Why**: It prepares the user data (name, email, default password) and calls the `UserServiceClient` to send the data to the correct microservice.

---

## 📂 Entities (`entity/`)
Entities are **Database Tables** represented as Java classes.

### `Product.java`
- **What**: Represents the `products` table in the database.
- **Columns**: `modelName`, `productType`, `maxPrice`, `aiOverviewJson`, `imageUrl`.
- **Why**: We need a Java object that maps directly to the rows in our PostgreSQL database so we can save and load data easily using JPA.

---

## 📂 Repositories (`repository/`)
Repositories are the **Data Access Layer**. They talk directly to the Database.

### `ProductRepository.java`
- **What**: Interface extending `JpaRepository`.
- **Why**: It provides built-in methods like `.save()`, `.findAll()`, and `.deleteById()` so we don't have to write raw SQL queries (SELECT * FROM products...) manually.

---

## 📂 DTOs (`dto/`)
DTOs (Data Transfer Objects) are **Envelopes**. They are simple classes used to carry data between the client (frontend) and the server, or between microservices.

### `ProductRequest.java` / `ProductResponse.java`
- **Purpose**: formats the JSON sent from the frontend when creating a product.
- **Why**: We don't want to expose our internal database structure (`Product.java`) directly to the outside world. DTOs let us control exactly what fields we accept and send back.

### `DashboardStats.java`
- **Purpose**: A simple container for holding `totalUsers`, `activeTickets`, `revenue`, etc.
- **Why**: To group these separate numbers into one single JSON object to send to the UI.

### `CreateUserRequest.java` / `UserResponse.java`
- **Purpose**: Carries user data (name, email, role) when creating a new executive.

### `ExecutiveWorkload.java`
- **Purpose**: Holds an Executive's Name and their Ticket Count.
- **Why**: Used for the charts in the Admin Dashboard.

### `ErrorResponse.java`
- **Purpose**: Standard format for error messages (Status, Message, Timestamp).
- **Why**: If something goes wrong, we want to send a clean JSON error, not a giant Java stack trace.

---

## 📂 Clients (`client/`)
Clients are **Messengers**. They allow this microservice just to talk to other microservices.

### `UserServiceClient.java`
- **What**: Connects to the **Auth User Service** (Port 9101).
- **Why**: The Admin Service doesn't own the "Users" table. So when an Admin creates a new Executive, this client sends a HTTP POST request to the Auth Service to do the actual saving.

### `TicketServiceClient.java`
- **What**: Connects to the **Ticket Service** (Port 9102).
- **Why**: The Admin Service doesn't own the "Tickets" table. To show the "Total Active Tickets" on the dashboard, this client asks the Ticket Service for that number.

---

## 📂 Config (`config/`)
Configuration classes that setup specific behaviors.

### `RestTemplateConfig.java`
- **What**: Creates a `RestTemplate` bean.
- **Why**: `RestTemplate` is the tool Spring uses to make HTTP requests (like fetching data from another service or calling the AI API). We configure it here so we can inject it wherever we need it.

---

## 📂 Exceptions (`exception/`)
Files that handle when things go wrong.

### `GlobalExceptionHandler.java`
- **What**: A central error Catcher.
- **Why**: If any part of the code throws an error (like "Product not found"), this class catches it and converts it into a nice `ErrorResponse` JSON. It prevents the app from crashing and showing an ugly white label error page.
