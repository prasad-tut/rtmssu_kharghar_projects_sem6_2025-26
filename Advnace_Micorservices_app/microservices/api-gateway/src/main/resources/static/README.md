# 🖥️ UI (Frontend) Codebase Guide

This folder contains the actual web pages and scripts that run in the user's browser. It communicates with the backend via the API Gateway.

## 📂 HTML Pages (The Skeleton)
### `index.html`
- **What**: The Landing & Login Page.
- **Key Elements**: Login Form (Email/Password).
- **Purpose**: The first thing any user sees. It directs them to the correct dashboard after login.

### `admin.html`
- **What**: The Control Center.
- **Key Sections**:
  - `sidebar`: Navigation.
  - `dashboard-view`: Charts and Stats.
  - `products-view`: Form to add new products.
  - `users-view`: Form to hire executives.

### `customer.html`
- **What**: The Customer Portal.
- **Key Sections**:
  - `products-grid`: Browse available items.
  - `create-ticket-modal`: Popup form to file a complaint.
  - `tickets-list`: History of my tickets.
  - `chat-interface`: Real-time messaging implementation.

### `executive.html`
- **What**: The Agent Workspace.
- **Key Sections**:
  - `inbox-list`: List of assigned tickets.
  - `ticket-detail-view`: Reading the problem.
  - `ai-reply-area`: Where the implementation of "Generate AI Reply" happens.

---

## 📂 JavaScript (`js/`) (The Logic)
### `config.js`
- **Critical File**: Wraps the API Base URL (`http://localhost:9100/api`).
- **Why**: Changing this ONE variable updates the API endpoint for all pages.

### `auth.js`
- **Purpose**: Handles Login/Logout.
- **Key Function**: `login(email, password)`. It saves the JWT token to `localStorage` so other pages can use it.

### `customer.js`
- **Purpose**: Drives `customer.html`.
- **Key Functions**:
  - `loadProducts()`: Fetches product list.
  - `createTicket()`: Sends the fetch request to create a ticket.
  - `loadChat()`: Polls for new messages.

### `admin.js`
- **Purpose**: Drives `admin.html`.
- **Key Functions**:
  - `loadDashboardStats()`: Draws the charts.
  - `addProduct()`: Handles the AI generation request and saving.

### `executive.js`
- **Purpose**: Drives `executive.html`.
- **Key Functions**:
  - `generateAiReply()`: Calls the AI endpoint and types the text into the box.
  - `sendMessage()`: Submits the reply.

---

## 📂 CSS (`css/`) (The Style)
### `main.css`
- **Purpose**: The core design system.
- **Features**:
  - CSS Variables for colors (`--primary-color`, etc.).
  - Glassmorphism effects (translucent cards).
  - Flexbox/Grid layouts for responsiveness.

### `mobile.css`
- **Purpose**: Responsive overrides.
- **Why**: Ensures the site looks good on phones (hiding sidebars, adjusting font sizes).
