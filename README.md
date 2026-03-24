# Conquest Microservices Architecture
> A next-generation Ticket Management System built with Spring Boot, Spring Cloud OpenFeign, Eureka Service Discovery, and a modern Glassmorphism UI.

## 🚀 Architecture Overview
This project decomposes a monolithic ticket management system into scalable, independent microservices configured to dynamically communicate via **Spring Cloud OpenFeign** over a **Netflix Eureka** Service Registry. It features automated MySQL relational database generation using Hibernate.

### Services
1. **Eureka Server (`eureka_server_app`)** - Runs on port `9000`. Acts as the service registry.
2. **User Service (`user_service`)** - Runs on port `9001`. Manages user data in `user_service_db`.
3. **Ticket Service (`ticket_service`)** - Runs on port `9002`. Manages ticket data in `ticket_service_db`.
4. **API Gateway (`api_gateway`)** - Runs on port `9090`. Exposes backend microservices to the frontend transparently with CORS fully enabled.
5. **Frontend UI (`frontend/`)** - A custom built Vanilla JS Glassmorphism dashboard running entirely independent of Node/NPM.

## ⚙️ Quick Start Guide

### 1. Start the Microservices Core
Run the following commands in four separate terminals to spin up the architecture. **(Note: Eureka must be started first!).**

```bash
# Terminal 1: Service Registry
cd eureka_server_app
.\mvnw spring-boot:run

# Terminal 2: User Service (Automatically registers to Eureka)
cd user_service
.\mvnw spring-boot:run

# Terminal 3: Ticket Service (Automatically registers to Eureka)
cd ticket_service
.\mvnw spring-boot:run

# Terminal 4: API Gateway (Automatically routes frontend traffic)
cd api_gateway
.\mvnw spring-boot:run
```

### 2. Launch the UI
There is no `npm install` necessary. The entire UI relies on pure JavaScript and CSS.
Open `frontend/index.html` in your web browser. The frontend will dynamically fetch live data from the MySQL databases automatically via the API Gateway!

## 🔗 Cross-Service Communication (Core Feature)
The primary feature of this project is demonstrating programmatic, IP-agnostic cross-communication between microservices.

**Test Endpoint:** 
`GET http://localhost:9002/tickets/1/user`

When this endpoint is hit, the Ticket Service queries its database for Ticket #1, extracts the `raisedBy` User ID, dynamically looks up the `user-micro-service` IP addressing via Eureka, and natively pulls the User JSON payload via `@FeignClient` directly into the return body.

## 🛠️ Built With:
- **Java 17 / Spring Boot 3.5.9** 
- **Spring Cloud** (Netflix Eureka, OpenFeign, Gateway)
- **Hibernate / Spring Data JPA**
- **MySQL 8.0**
- **Vanilla JS / HTML5 / CSS3** (Custom Glassmorphism Design System)
