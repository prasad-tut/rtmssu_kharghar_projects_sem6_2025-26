# Postman API Queries for Ticket Microservices

This document contains 100 API queries to test the various microservices.
**Base URLs:**
- Auth Service: `http://localhost:9101`
- Ticket Service: `http://localhost:9102`
- Customer Service: `http://localhost:9103`
- Admin Service: `http://localhost:9104`
- Executive Service: `http://localhost:9105`

---

## Auth Service (Port 9101)

### Authentication
**1. Login (Success)**
- **Method:** `POST`
- **URL:** `http://localhost:9101/auth/login`
- **Body:**
  ```json
  {
    "email": "admin@example.com",
    "password": "password123"
  }
  ```

**2. Login (Failure - Wrong Password)**
- **Method:** `POST`
- **URL:** `http://localhost:9101/auth/login`
- **Body:**
  ```json
  {
    "email": "admin@example.com",
    "password": "wrongpassword"
  }
  ```

**3. Login (Missing Fields)**
- **Method:** `POST`
- **URL:** `http://localhost:9101/auth/login`
- **Body:**
  ```json
  {
    "email": "admin@example.com"
  }
  ```

**4. Signup (Customer)**
- **Method:** `POST`
- **URL:** `http://localhost:9101/auth/signup`
- **Body:**
  ```json
  {
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "role": "CUSTOMER"
  }
  ```

**5. Signup (Duplicate Email)**
- **Method:** `POST`
- **URL:** `http://localhost:9101/auth/signup`
- **Body:**
  ```json
  {
    "name": "Jane Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "role": "CUSTOMER"
  }
  ```

**6. Register (Alias for Signup)**
- **Method:** `POST`
- **URL:** `http://localhost:9101/auth/register`
- **Body:**
  ```json
  {
    "name": "Another User",
    "email": "another@example.com",
    "password": "password123",
    "role": "CUSTOMER"
  }
  ```

**7. Validate Token (Valid)**
- **Method:** `POST`
- **URL:** `http://localhost:9101/auth/validate-token`
- **Body:**
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

**8. Validate Token (Invalid)**
- **Method:** `POST`
- **URL:** `http://localhost:9101/auth/validate-token`
- **Body:**
  ```json
  {
    "token": "invalid-token-string"
  }
  ```

**9. Logout**
- **Method:** `POST`
- **URL:** `http://localhost:9101/auth/logout?email=john.doe@example.com`

**10. Health Check**
- **Method:** `GET`
- **URL:** `http://localhost:9101/auth/health`

### User Management
**11. Get All Users**
- **Method:** `GET`
- **URL:** `http://localhost:9101/api/users`

**12. Get User by ID**
- **Method:** `GET`
- **URL:** `http://localhost:9101/api/users/1`

**13. Get User by Email**
- **Method:** `GET`
- **URL:** `http://localhost:9101/api/users/email/john.doe@example.com`

**14. Get Users by Role (CUSTOMER)**
- **Method:** `GET`
- **URL:** `http://localhost:9101/api/users/role/CUSTOMER`

**15. Get Users by Role (EXECUTIVE)**
- **Method:** `GET`
- **URL:** `http://localhost:9101/api/users/role/EXECUTIVE`

**16. Get Users by Role (Invalid Role)**
- **Method:** `GET`
- **URL:** `http://localhost:9101/api/users/role/SUPERHERO`

**17. Get Current User (Me)**
- **Method:** `GET`
- **URL:** `http://localhost:9101/api/users/me`
- **Headers:** `Authorization: Bearer <token>`

**18. Update User**
- **Method:** `PUT`
- **URL:** `http://localhost:9101/api/users/1`
- **Body:**
  ```json
  {
    "name": "John Updated",
    "phone": "1234567890"
  }
  ```

**19. Deactivate User**
- **Method:** `PATCH`
- **URL:** `http://localhost:9101/api/users/1/deactivate`

**20. Activate User**
- **Method:** `PATCH`
- **URL:** `http://localhost:9101/api/users/1/activate`

**21. Delete User**
- **Method:** `DELETE`
- **URL:** `http://localhost:9101/api/users/1`

**22. Count Users by Role**
- **Method:** `GET`
- **URL:** `http://localhost:9101/api/users/count/CUSTOMER`

**23. Check Email Exists**
- **Method:** `GET`
- **URL:** `http://localhost:9101/api/users/exists/john.doe@example.com`

---

## Ticket Service (Port 9102)

### Ticket Operations
**24. Create Ticket**
- **Method:** `POST`
- **URL:** `http://localhost:9102/api/tickets`
- **Body:**
  ```json
  {
    "title": "Login Issue",
    "description": "Cannot login to the portal",
    "priority": "HIGH",
    "category": "TECHNICAL",
    "customerId": 1
  }
  ```

**25. Create Ticket (Invalid Priority)**
- **Method:** `POST`
- **URL:** `http://localhost:9102/api/tickets`
- **Body:**
  ```json
  {
    "title": "Bad Request",
    "priority": "SUPER_HIGH",
    "customerId": 1
  }
  ```

**26. Get All Tickets**
- **Method:** `GET`
- **URL:** `http://localhost:9102/api/tickets`

**27. Get Ticket by ID**
- **Method:** `GET`
- **URL:** `http://localhost:9102/api/tickets/1`

**28. Get Ticket by ID (Non-Existent)**
- **Method:** `GET`
- **URL:** `http://localhost:9102/api/tickets/99999`

**29. Get Tickets by Customer**
- **Method:** `GET`
- **URL:** `http://localhost:9102/api/tickets/customer/1`

**30. Get Tickets by Executive**
- **Method:** `GET`
- **URL:** `http://localhost:9102/api/tickets/executive/2`

**31. Get Active Tickets by Executive**
- **Method:** `GET`
- **URL:** `http://localhost:9102/api/tickets/executive/2/active`

**32. Get Tickets by Status (OPEN)**
- **Method:** `GET`
- **URL:** `http://localhost:9102/api/tickets/status/OPEN`

**33. Get Tickets by Status (RESOLVED)**
- **Method:** `GET`
- **URL:** `http://localhost:9102/api/tickets/status/RESOLVED`

**34. Update Ticket Status**
- **Method:** `POST`
- **URL:** `http://localhost:9102/api/tickets/1/status`
- **Body:**
  ```json
  {
    "status": "IN_PROGRESS"
  }
  ```

**35. Assign Ticket**
- **Method:** `POST`
- **URL:** `http://localhost:9102/api/tickets/1/assign`
- **Body:**
  ```json
  {
    "executiveId": 2
  }
  ```

**36. Rate Ticket**
- **Method:** `POST`
- **URL:** `http://localhost:9102/api/tickets/1/rating`
- **Body:**
  ```json
  {
    "rating": 5
  }
  ```

**37. Rate Ticket (Invalid Rating)**
- **Method:** `POST`
- **URL:** `http://localhost:9102/api/tickets/1/rating`
- **Body:**
  ```json
  {
    "rating": 10
  }
  ```

**38. Add Resolution Notes**
- **Method:** `POST`
- **URL:** `http://localhost:9102/api/tickets/1/resolution`
- **Body:**
  ```json
  {
    "notes": "Reset the user password and guided them through login."
  }
  ```

**39. Resolve Ticket**
- **Method:** `POST`
- **URL:** `http://localhost:9102/api/tickets/1/resolve`
- **Body:** `Resolved successfully via remote session.` (Raw String or as needed matching Controller)

**40. Delete Ticket**
- **Method:** `DELETE`
- **URL:** `http://localhost:9102/api/tickets/1`

**41. Get Executive Workloads**
- **Method:** `GET`
- **URL:** `http://localhost:9102/api/tickets/workloads`

**42. Get Ticket Stats**
- **Method:** `GET`
- **URL:** `http://localhost:9102/api/tickets/stats`

**43. Ticket Service Health**
- **Method:** `GET`
- **URL:** `http://localhost:9102/api/tickets/health`

---

## Customer Service (Port 9103)

### Ticket Interaction
**44. Raise Ticket (Customer)**
- **Method:** `POST`
- **URL:** `http://localhost:9103/api/customer/tickets`
- **Body:**
  ```json
  {
    "subject": "Billing Error",
    "description": "Charged twice this month",
    "customerId": 1
  }
  ```

**45. Get My Tickets**
- **Method:** `GET`
- **URL:** `http://localhost:9103/api/customer/tickets/my/1`

**46. Get Customer Ticket Details**
- **Method:** `GET`
- **URL:** `http://localhost:9103/api/customer/tickets/1`

**47. Close Ticket (Customer)**
- **Method:** `POST`
- **URL:** `http://localhost:9103/api/customer/tickets/1/close`

**48. Reopen Ticket**
- **Method:** `POST`
- **URL:** `http://localhost:9103/api/customer/tickets/1/reopen`

**49. Rate Ticket Service**
- **Method:** `POST`
- **URL:** `http://localhost:9103/api/customer/tickets/1/rate`
- **Body:**
  ```json
  {
    "rating": 4
  }
  ```

### Messaging
**50. Send Message**
- **Method:** `POST`
- **URL:** `http://localhost:9103/api/customer/messages`
- **Body:**
  ```json
  {
    "ticketId": 1,
    "senderId": 1,
    "content": "Any update on this?",
    "messageType": "TEXT"
  }
  ```

**51. Send Message (Empty Content)**
- **Method:** `POST`
- **URL:** `http://localhost:9103/api/customer/messages`
- **Body:**
  ```json
  {
    "ticketId": 1,
    "senderId": 1,
    "content": ""
  }
  ```

**52. Get Messages by Ticket**
- **Method:** `GET`
- **URL:** `http://localhost:9103/api/customer/messages/ticket/1`

**53. Get Messages (Non-Existent Ticket)**
- **Method:** `GET`
- **URL:** `http://localhost:9103/api/customer/messages/ticket/999`

**54. Mark Messages as Read**
- **Method:** `POST`
- **URL:** `http://localhost:9103/api/customer/messages/ticket/1/read`
- **Body:**
  ```json
  {
    "userId": 1
  }
  ```

**55. Get Unread Count**
- **Method:** `GET`
- **URL:** `http://localhost:9103/api/customer/messages/ticket/1/unread?userId=1`

**56. Customer Service Health**
- **Method:** `GET`
- **URL:** `http://localhost:9103/api/customer/messages/health`

---

## Admin Service (Port 9104)

### Dashboard & Analytics
**57. Get Dashboard Stats**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/dashboard`

**58. Admin Health Check**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/dashboard/health`

**59. Get Ticket Stats (Admin)**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/tickets/stats`

### Ticket Management
**60. Get All Tickets (Admin View)**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/tickets`

**61. Get Tickets by Status (OPEN)**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/tickets/status/OPEN`

**62. Get Tickets by Executive**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/tickets/executive/2`

**63. Get Unassigned Tickets**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/tickets/unassigned`

**64. Get Ticket Details (Admin)**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/tickets/1`

**65. Assign Ticket to Executive**
- **Method:** `POST`
- **URL:** `http://localhost:9104/api/admin/tickets/1/assign`
- **Body:**
  ```json
  {
    "executiveId": 2
  }
  ```

**66. Update Ticket Status (Admin)**
- **Method:** `POST`
- **URL:** `http://localhost:9104/api/admin/tickets/1/status?status=IN_PROGRESS`

**67. Close Ticket (Admin)**
- **Method:** `POST`
- **URL:** `http://localhost:9104/api/admin/tickets/1/close`

**68. Delete Ticket (Admin)**
- **Method:** `DELETE`
- **URL:** `http://localhost:9104/api/admin/tickets/1`

**69. Auto-Assign Ticket**
- **Method:** `POST`
- **URL:** `http://localhost:9104/api/admin/executives/1/auto-assign`

**70. Auto-Assign (No Executors)**
- **Method:** `POST`
- **URL:** `http://localhost:9104/api/admin/executives/99/auto-assign`

### User & Executive Management
**71. Get All Users (Admin)**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/users`

**72. Get Executives List**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/users/executives`

**73. Get Customers List**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/users/customers`

**74. Create New Executive**
- **Method:** `POST`
- **URL:** `http://localhost:9104/api/admin/users/executives`
- **Body:**
  ```json
  {
    "name": "Super Exec",
    "email": "exec@company.com",
    "password": "pass"
  }
  ```

**75. Get Executive Workload**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/executives/workload`

### Product Management
**76. Get All Products**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/products`

**77. Get Product Catalog**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/products/catalog`

**78. Get Products by Admin**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/products/by-admin/1`

**79. Get Product Details**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/products/1`

**80. Create Product (Manual)**
- **Method:** `POST`
- **URL:** `http://localhost:9104/api/admin/products`
- **Body:**
  ```json
  {
    "name": "New Service",
    "description": "A new service offered",
    "price": 99.99,
    "adminId": 1
  }
  ```

**81. Create Product (AI Generated)**
- **Method:** `POST`
- **URL:** `http://localhost:9104/api/admin/products/ai`
- **Body:**
  ```json
  {
    "name": "AI Bundle",
    "description": "Generate details automatically",
    "price": 199.99,
    "adminId": 1
  }
  ```

---

## Executive Service (Port 9105)

### Dashboard & Tickets
**82. Executive Health Check**
- **Method:** `GET`
- **URL:** `http://localhost:9105/api/executive/health`

**83. Get My Tickets**
- **Method:** `GET`
- **URL:** `http://localhost:9105/api/executive/2/tickets`

**84. Get My Open Tickets**
- **Method:** `GET`
- **URL:** `http://localhost:9105/api/executive/2/tickets/open`

**85. Get My Resolved Tickets**
- **Method:** `GET`
- **URL:** `http://localhost:9105/api/executive/2/tickets/resolved`

**86. Get Workload Stats**
- **Method:** `GET`
- **URL:** `http://localhost:9105/api/executive/2/workload`

**87. Get Ticket Details**
- **Method:** `GET`
- **URL:** `http://localhost:9105/api/executive/tickets/1`

### Workflow
**88. Start Working on Ticket**
- **Method:** `POST`
- **URL:** `http://localhost:9105/api/executive/tickets/1/start`

**89. Update Status**
- **Method:** `POST`
- **URL:** `http://localhost:9105/api/executive/tickets/1/status`
- **Body:**
  ```json
  {
    "status": "IN_PROGRESS"
  }
  ```

**90. Resolve Ticket**
- **Method:** `POST`
- **URL:** `http://localhost:9105/api/executive/tickets/1/resolve`
- **Body:** `Fixed by rebooting server.`

**91. Resolve Ticket (No Notes)**
- **Method:** `POST`
- **URL:** `http://localhost:9105/api/executive/tickets/1/resolve`

### AI & Drafts
**92. Get AI Reply Suggestions**
- **Method:** `GET`
- **URL:** `http://localhost:9105/api/executive/ai/tickets/1/reply-suggestions`

**93. Save Draft**
- **Method:** `POST`
- **URL:** `http://localhost:9105/api/executive/drafts`
- **Body:**
  ```json
  {
    "ticketId": 1,
    "executiveId": 2,
    "content": "Drafting a response..."
  }
  ```

**94. Get Drafts for Ticket**
- **Method:** `GET`
- **URL:** `http://localhost:9105/api/executive/drafts/ticket/1`

**95. Delete Draft**
- **Method:** `DELETE`
- **URL:** `http://localhost:9105/api/executive/drafts/1`

### Communication
**96. Get Ticket Messages**
- **Method:** `GET`
- **URL:** `http://localhost:9105/api/executive/tickets/1/messages`

**97. Send Message to Customer**
- **Method:** `POST`
- **URL:** `http://localhost:9105/api/executive/tickets/1/messages`
- **Body:**
  ```json
  {
    "senderId": 2,
    "content": "We have looked into your issue.",
    "messageType": "text"
  }
  ```

**98. Get Unread Messages Count**
- **Method:** `GET`
- **URL:** `http://localhost:9105/api/executive/tickets/1/messages/unread-count`

**99. Mark Messages as Read**
- **Method:** `POST`
- **URL:** `http://localhost:9105/api/executive/tickets/1/messages/read?executiveId=2`

### Miscellaneous
**100. Check Server Status (Gateway/Proxy if checked indirectly)**
- **Method:** `GET`
- **URL:** `http://localhost:9104/api/admin/dashboard/health`
