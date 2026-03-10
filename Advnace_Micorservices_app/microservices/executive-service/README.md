# 👔 Executive Service Codebase Guide

The `executive-service` provides tools for the Support Agents to be productive. It integrates AI and Draft handling.

## 📂 Root & Configuration
### `src/main/resources/application.properties`
- **Purpose**: Configures **Port 9105** and connects to the Aiven Cloud database.

---

## 📂 Controllers (`controller/`)
### `ExecutiveTicketController.java`
- **Purpose**: How executives see their work.
- **Endpoints**: `GET /executive/tickets`. Returns only tickets assigned to *me* (the logged-in executive).

### `AiAssistantController.java`
- **Purpose**: The "Magic" feature.
- **Endpoint**: `POST /ai/suggest-reply`.
- **Why**: Sends ticket history to Gemini AI and returns a polite, suggested response for the agent to use.

### `DraftController.java`
- **Purpose**: Saving work in progress.
- **Endpoint**: `POST /drafts`.
- **Why**: If an agent starts typing but gets distracted, this saves the text so they don't lose it.

---

## 📂 Entity & Model (`model/`)
### `Draft.java`
- **Table**: `drafts` (in Aiven Cloud).
- **Columns**: `ticketId`, `content`, `executiveId`.
- **Why**: Temporary storage that doesn't belong in the main message history.

---

## 📂 Service (`service/`)
### `GeminiClient.java`
- **What**: AI Integration logic.
- **Why**: Connects to the external LLM (OpenRouter/Gemini). It constructs a prompt like "You are a helpful support agent. Here is the user's problem: [Problem]. Write a reply."

### `ExecutiveTicketService.java`
- **What**: Logic for fetching/updating tickets.
- **Why**: Coordinates with the Ticket Service to get the latest status of assigned tasks.

---

## 📂 Clients (`client/`)
### `TicketServiceClient.java`
- **Purpose**: To fetch ticket details.
### `CustomerServiceClient.java`
- **Purpose**: To send the final reply message to the customer.
