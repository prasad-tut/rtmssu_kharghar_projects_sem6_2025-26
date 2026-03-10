# 🎟️ Ticket Service Codebase Guide

The `ticket-service` is the core Vault. It manages the lifecycle of a support request.

## 📂 Root & Configuration
### `src/main/resources/application.properties`
- **Purpose**: Configures **Port 9102** and connects to the AWS RDS PostgreSQL database.

---

## 📂 Controllers (`controller/`)
### `TicketController.java`
- **Endpoints**: `POST /tickets` (Create), `PUT /tickets/{id}/status` (Update Status).
- **Purpose**: The main API for CRUD operations on tickets. Used by Admins, Services, and internally by other microservices.

---

## 📂 Entity & Repository (`entity/`, `repository/`)
### `Ticket.java`
- **Table**: `tickets` (in AWS RDS).
- **Columns**: `title`, `priority`, `status`, `customerId`, `executiveId`.
- **Why**: The absolute source of truth for a ticket's state.

### `TicketStatus.java` / `Priority.java` / `TicketCategory.java`
- **Type**: Enums.
- **Why**: Defines strict rules. A status can only be `OPEN`, `IN_PROGRESS`, `RESOLVED`, or `CLOSED`.

### `TicketRepository.java`
- **Purpose**: JPA Repository for complex queries like "Find all tickets assigned to Executive X with Status OPEN".

---

## 📂 Service (`service/`)
### `TicketService.java`
- **What**: Core Business Logic.
- **Why**:
  - Handles **State Transitions**: You can't go from "CLOSED" back to "NEW".
  - Handles **Assignment**: Associating an Executive ID with a ticket.

---

## 📂 DTOs (`dto/`)
### `CreateTicketRequest.java`
- **Purpose**: The form data sent by the customer (Subject, Description, Priority).

### `TicketResponse.java`
- **Purpose**: The full ticket details returned to the UI.

### `ExecutiveWorkloadResponse.java`
- **Purpose**: Special DTO for the Admin Service.
- **Why**: The Admin Service asks "How many tickets does everyone have?". This DTO packages that answer.
