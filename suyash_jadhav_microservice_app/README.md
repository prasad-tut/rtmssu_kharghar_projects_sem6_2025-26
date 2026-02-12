# Contributor

- suyash vishwas Jadhav

---

# Conquest Microservice Ticket Management System

A comprehensive microservices-based ticket management system built with Spring Boot, featuring a professional white-themed frontend interface.

## Architecture

This project demonstrates a complete microservices architecture with:

- **Eureka Server** - Service discovery and registration
- **API Gateway** - Single entry point for all client requests
- **User Service** - Manages user data and operations
- **Ticket Service** - Handles ticket creation, assignment, and tracking
- **Frontend** - Professional web interface for system interaction

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

## Features

### User Management
- Create, Read, Update, Delete users
- Role-based user types (Customer, Executive, Admin)
- User search and filtering
- Real-time user statistics

### Ticket Management
- Create and track support tickets
- Assign tickets to executives
- Update ticket status (Open, Assigned, Closed)
- Search and filter tickets
- View ticket history and details

### Dashboard
- Real-time statistics
- Professional UI with modern design
- Responsive layout for all devices
- Smooth animations and transitions

## Installation & Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Python 3 (for frontend server)
- Git

### Clone the Repository
```bash
git clone <your-repo-url>
cd Basic_Microservice_ticket
```

### Start Backend Services

The project includes a convenient startup script:

```bash
./start.sh
```

This will start all services in the following order:
1. **Eureka Server** (Port 9000)
2. **User Service** (Port 9001)
3. **Ticket Service** (Port 9002)
4. **API Gateway** (Port 9090)

Wait for all services to start (approximately 1-2 minutes).

### Start Frontend

```bash
cd Basic-frontend
./start-frontend.sh
```

The frontend will be available at: **http://localhost:8000**

## Service Endpoints

### API Gateway (Port 9090)
All client requests should go through the API Gateway:

#### User Service Endpoints
```
GET    /user-micro-service/users              - Get all users
GET    /user-micro-service/users/by-id/{id}   - Get user by ID
POST   /user-micro-service/users              - Create new user
PUT    /user-micro-service/users/{id}         - Update user
DELETE /user-micro-service/users/{id}         - Delete user
GET    /user-micro-service/users/{id}/tickets - Get user's tickets
```

#### Ticket Service Endpoints
```
GET    /ticket-service/tickets                - Get all tickets
GET    /ticket-service/tickets/{id}           - Get ticket by ID
POST   /ticket-service/tickets                - Create new ticket
PUT    /ticket-service/tickets/{id}           - Update ticket
DELETE /ticket-service/tickets/{id}           - Delete ticket
PATCH  /ticket-service/tickets/{id}           - Close ticket
GET    /ticket-service/tickets/userdto/{id}   - Get ticket with user details
```

## Frontend Features

### Professional Design
- Clean white theme with modern aesthetics
- Inter font family for professional typography
- Industry-standard spacing and layout
- Smooth hover effects and transitions
- Fully responsive design

### User Interface Components
- **Dashboard Cards** - Real-time statistics
- **Data Tables** - Sortable and searchable
- **Modal Forms** - Create and edit operations
- **Toast Notifications** - User feedback
- **Status Badges** - Visual status indicators
- **Loading States** - Better UX during API calls

## Project Structure

```
Basic_Microservice_ticket/
├── eureka_server_app/          # Service discovery server
├── api_gateway/                # API Gateway
├── user_service/               # User microservice
├── ticket_service/             # Ticket microservice
├── Basic-frontend/             # Web frontend
│   ├── index.html             # Main HTML file
│   ├── styles.css             # Professional styling
│   ├── script.js              # JavaScript logic
│   └── start-frontend.sh      # Frontend server script
├── start.sh                    # Backend startup script
├── AdvanceTicketHub.md         # Reference to Advance Ticket Hub
└── README.md                   # This file
```

## Testing

### Using the Frontend
1. Open http://localhost:8000
2. Use the UI to create users and tickets
3. Test search, edit, and delete operations

### Using Postman/cURL

**Example: Create a User**
```bash
curl -X POST http://localhost:9090/user-micro-service/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890",
    "role": "CUSTOMER"
  }'
```

**Example: Create a Ticket**
```bash
curl -X POST http://localhost:9090/ticket-service/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "issue": "Cannot login to dashboard",
    "raisedBy": 1,
    "status": "OPEN"
  }'
```

## Configuration

### Backend Services
Each service can be configured via `application.properties` or `application.yml` in their respective `src/main/resources` directories.

### Frontend
API endpoints are configured in `Basic-frontend/script.js`:
```javascript
const API_BASE_URL = 'http://localhost:9090';
```

## Troubleshooting

### Services Won't Start
- Ensure ports 9000, 9001, 9002, 9090 are available
- Check Java version: `java -version`
- View service logs in the project root directory

### Frontend Can't Connect
- Verify all backend services are running
- Check browser console (F12) for errors
- Ensure API Gateway is accessible: `curl http://localhost:9090/user-micro-service/users`

### CORS Errors
- The services should have CORS enabled
- If issues persist, check `@CrossOrigin` annotations in controllers

## Deployment

### Production Considerations
1. Replace H2 with production database (PostgreSQL, MySQL)
2. Configure proper security (Spring Security, JWT)
3. Set up environment-specific configurations
4. Use Docker for containerization
5. Implement proper logging and monitoring
6. Set up CI/CD pipeline

## License

This project is created for educational purposes.

## Acknowledgments

- Spring Boot team for the excellent framework
- Netflix OSS for Eureka and other cloud components

---

## Detailed Service Interactions

### 1. User Service calling Ticket Service
*Use this when you want to see all tickets belonging to a user THROUGH the User Service.*
- **Method:** `GET`
- **URL:** `http://localhost:9001/users/1/tickets`
- **Result:** You get a JSON list of tickets from the Ticket Service.

### 2. Ticket Service calling User Service
*Use this when you want to see the details of the User who raised a ticket THROUGH the Ticket Service.*
- **Method:** `GET`
- **URL:** `http://localhost:9002/tickets/userdto/1`
- **Result:** You get a JSON object with User details (Name, Email) from the User Service.

---

## Postman POST Commands (with JSON Examples)

### 1. Create a Customer (User Service)
- **URL:** `http://localhost:9001/users`
- **Method:** `POST`
- **Headers:** `Content-Type: application/json`
- **Body (JSON):**
```json
{
  "name": "Jane Smith",
  "email": "jane@example.com",
  "phone": "9876543210",
  "role": "CUSTOMER"
}
```

### 2. Create an Executive (User Service)
- **URL:** `http://localhost:9001/users`
- **Method:** `POST`
- **Body (JSON):**
```json
{
  "name": "Admin Mike",
  "email": "mike.admin@example.com",
  "phone": "5550123456",
  "role": "EXECUTIVE"
}
```

### 3. Raise a New Ticket (Ticket Service)
- **URL:** `http://localhost:9002/tickets`
- **Method:** `POST`
- **Body (JSON):**
```json
{
  "issue": "I cannot access my billing settings",
  "raisedBy": 1,
  "status": "OPEN",
  "raisedOn": "2024-02-12"
}
```

### 4. Update/Assign a Ticket (Ticket Service)
- **URL:** `http://localhost:9002/tickets`
- **Method:** `PUT`
- **Body (JSON):**
```json
{
  "id": 1,
  "issue": "I cannot access my billing settings",
  "raisedBy": 1,
  "assignedTo": 2,
  "assignedOn": "2024-02-12",
  "status": "ASSIGNED"
}
```

---

## Full Service Endpoints

### User Microservice (Port 9001)
| Method | URL | Action |
| :--- | :--- | :--- |
| `GET` | `http://localhost:9001/users` | Get All Users |
| `GET` | `http://localhost:9001/users/by-id/1` | Get User by ID |
| `DELETE` | `http://localhost:9001/users/1` | Delete User |

### Ticket Microservice (Port 9002)
| Method | URL | Action |
| :--- | :--- | :--- |
| `GET` | `http://localhost:9002/tickets` | Get All Tickets |
| `GET` | `http://localhost:9002/tickets/1` | Get Ticket by ID |
| `PATCH` | `http://localhost:9002/tickets/1` | Close Ticket (Status -> CLOSED) |

---

## API Gateway (Port 9090)
Test through the gateway to simulate a real production environment:
- **POST User:** `http://localhost:9090/user-micro-service/users`
- **POST Ticket:** `http://localhost:9090/ticket-service/tickets`
- **GET User Tickets:** `http://localhost:9090/user-micro-service/users/1/tickets`
- **GET Ticket User Info:** `http://localhost:9090/ticket-service/tickets/userdto/1`
