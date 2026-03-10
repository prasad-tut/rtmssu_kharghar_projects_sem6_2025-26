# 🗣️ Customer Service Codebase Guide

The `customer-service` handles all interactions for the End User (the Customer). Its main focus is on **Communication** (Messages).

## 📂 Root & Configuration
### `src/main/resources/application.properties`
- **Purpose**: Configures **Port 9103** and connects to the Supabase database.

---

## 📂 Controllers (`controller/`)
### `CustomerTicketController.java`
- **Endpoints**: `POST /customer/ticket`, `GET /customer/tickets`.
- **Purpose**: Allows customers to view *their* specific tickets.
- **Note**: It doesn't store tickets itself; it acts as a proxy to the `Ticket Service` to fetch them, filtering only for the current user.

### `MessageController.java`
- **Endpoints**: `POST /messages`, `GET /messages/{ticketId}`.
- **Purpose**: The Chat System. Retreives and saves the conversation history between Customer and Executive.

---

## 📂 Entity & Repository (`entity/`, `repository/`)
### `Message.java`
- **Table**: `messages` (in Supabase).
- **Columns**: `ticketId`, `senderId`, `content`, `attachment`.
- **Why**: Stores every single chat line.

### `MessageRepository.java`
- **Purpose**: Helper for database operations, like `findByTicketIdOrderByCreatedAtAsc`.

---

## 📂 Service (`service/`)
### `CustomerTicketService.java`
- **What**: Intermediary logic.
- **Why**: When a customer asks for tickets, this service calls the `TicketServiceClient` to get *all* tickets and then filters/validates them for the user.

### `MessageService.java`
- **What**: Logic for chat.
- **Why**: Saves messages to the DB and could trigger notifications (future scope).

---

## 📂 Clients (`client/`)
### `TicketServiceClient.java`
- **What**: Connects to **Ticket Service (Port 9102)**.
- **Why**: Logic separation! The Customer Service manages *Messages*, but needs to know if a *Ticket* actually exists before saving a message for it. It asks the Ticket Service: "Hey, does Ticket #123 exist?"
