# 🌐 API Gateway Codebase Guide

The `api-gateway` is the traffic controller of the entire system. It sits between the user (frontend) and all other backend microservices.

## 📂 Configuration
### `src/main/resources/application.yml`
- **What**: The Routing Rules.
- **Purpose**: Defines every path that the application handles.
- **Key Sections**:
  - `routes`: Maps paths like `/api/auth/**` to the Auth Service and `/api/tickets/**` to the Ticket Service.
  - `cors`: Allows the browser to make requests to this server (Cross-Origin Resource Sharing).
  - `resilience4j.circuitbreaker`: Defines what happens when a service goes down (Circuit Breaker pattern).

---

## 📂 Controllers (`controller/`)
### `GatewayHealthController.java`
- **Purpose**: Simple check to see if the Gateway is running.
- **Endpoint**: `GET /actuator/health` or custom health endpoints.
- **Why**: Useful for monitoring tools to know if the entry point is alive.

### `FallbackController.java`
- **Purpose**: The "Plan B" endpoints.
- **Why**: If the Ticket Service crashes, the Gateway's Circuit Breaker redirects the user here. Instead of a 500 error, this controller returns a friendly message like "Ticket Service is currently unavailable, please try again later."

---

## 📂 Main Application
### `ApiGatewayApplication.java`
- **Purpose**: Starts the Spring Boot application on **Port 9100**.
- **Why**: This is the single entry point. The frontend only ever talks to this port.
