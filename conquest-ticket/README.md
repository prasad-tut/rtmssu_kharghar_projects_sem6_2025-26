# Microservice Ticket Management System

A comprehensive microservices-based ticket management system built with Spring Boot, featuring a professional white-themed frontend interface.

## Architecture

This project demonstrates a complete microservices architecture with:

- **Eureka Server** - Service discovery and registration (Port 9000)
- **API Gateway** - Single entry point for all client requests (Port 9090)
- **User Service** - Manages user data and operations (Port 9001)
- **Ticket Service** - Handles ticket creation, assignment, and tracking (Port 9002)
- **Frontend** - Professional web interface for system interaction (Port 8000)

## Tech Stack

### Backend
- **Java 17+**
- **Spring Boot** - Microservices framework
- **Spring Cloud Netflix Eureka** - Service discovery
- **Spring Cloud Gateway** - API Gateway
- **Spring Data JPA** - Data persistence
- **H2 Database** - In-memory database (development)
- **Maven** - Build tool

### Frontend
- **HTML5** - Structure
- **CSS3** - Professional white-themed design
- **Vanilla JavaScript** - Logic and API integration
- **Google Fonts (Inter)** - Typography

## Installation & Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Python 3 (for frontend server)

### Start Backend Services
**Linux/macOS:**
```bash
./start.sh
```
**Windows:**
```cmd
start.bat
```
Wait for all services to start (approx 1-2 mins).

### Start Frontend
**Linux/macOS:**
```bash
cd Basic-frontend
./start-frontend.sh
```
**Windows:**
```cmd
cd Basic-frontend
start-frontend.bat
```
The frontend will be available at: **http://localhost:8081**

## Service Endpoints (via Gateway Port 9090)

### User Service
- `GET /user-micro-service/users` - Get all users
- `POST /user-micro-service/users` - Create user
- `PUT /user-micro-service/users/{id}` - Update user
- `DELETE /user-micro-service/users/{id}` - Delete user

### Ticket Service
- `GET /ticket-service/tickets` - Get all tickets
- `POST /ticket-service/tickets` - Create ticket
- `PUT /ticket-service/tickets/{id}` - Update ticket
- `PATCH /ticket-service/tickets/{id}` - Close ticket
- `GET /ticket-service/tickets/userdto/{id}` - Get ticket with user info
# conquest-ticket
