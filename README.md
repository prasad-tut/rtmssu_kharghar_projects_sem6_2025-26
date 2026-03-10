# Microservices Project - Conquest

This is a complete Spring Boot microservices project with service discovery, API gateway, and business services.

## Architecture

- **Eureka Server** (Port 8761) - Service Discovery
- **API Gateway** (Port 8080) - Entry point for all requests
- **User Service** (Port 8081) - User management
- **Ticket Service** (Port 8082) - Ticket management

## Prerequisites

- Java 17
- Maven 3.6+
- MySQL 8.0+

## Database Setup

Create two MySQL databases:
```sql
CREATE DATABASE user_db;
CREATE DATABASE ticket_db;
```

Update database credentials in application.properties files if needed (default: root/password).

## Running the Services

Start services in this order:

1. **Eureka Server**
```bash
cd eureka_server_app/eureka_server_app
mvn spring-boot:run
```

2. **User Service**
```bash
cd user_service/user_service
mvn spring-boot:run
```

3. **Ticket Service**
```bash
cd ticket_service/ticket_service
mvn spring-boot:run
```

4. **API Gateway**
```bash
cd api_gateway/api_gateway
mvn spring-boot:run
```

## API Endpoints

### Through API Gateway (http://localhost:8080)

#### User Service
- GET `/api/users` - Get all users
- GET `/api/users/{id}` - Get user by ID
- GET `/api/users/email/{email}` - Get user by email
- POST `/api/users` - Create user
- PUT `/api/users/{id}` - Update user
- DELETE `/api/users/{id}` - Delete user

#### Ticket Service
- GET `/api/tickets` - Get all tickets
- GET `/api/tickets/{id}` - Get ticket by ID
- GET `/api/tickets/user/{userId}` - Get tickets by user ID
- GET `/api/tickets/assigned/{assignedTo}` - Get tickets by assigned user
- GET `/api/tickets/status/{status}` - Get tickets by status
- POST `/api/tickets` - Create ticket
- PUT `/api/tickets/{id}` - Update ticket
- DELETE `/api/tickets/{id}` - Delete ticket

### Direct Service Access
- User Service: http://localhost:8081
- Ticket Service: http://localhost:8082

## Sample Requests

### Create User
```json
POST http://localhost:8080/api/users
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "address": "123 Main St"
}
```

### Create Ticket
```json
POST http://localhost:8080/api/tickets
{
    "title": "System Issue",
    "description": "Unable to login to the system",
    "priority": "HIGH",
    "userId": 1
}
```

## Monitoring

- Eureka Dashboard: http://localhost:8761
- Service health checks available through Spring Actuator

## Features Implemented

- Service Discovery with Eureka
- API Gateway with routing
- Load balancing
- Database integration with JPA
- RESTful APIs
- DTO pattern
- Exception handling
- Lombok for boilerplate reduction