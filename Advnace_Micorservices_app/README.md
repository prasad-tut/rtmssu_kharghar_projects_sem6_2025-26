# TicketHub - Microservices Ticket Management System

**Created by: Suyash Vishwas Jadhav**  
**Copyright (c) 2026 Suyash Vishwas Jadhav. All rights reserved.**

A comprehensive ticket management system built with Spring Boot microservices architecture, featuring customer support, executive management, and admin panels.

---

## Architecture

This project follows a microservices architecture with the following services:

- **API Gateway** (Port 9100) - Entry point for all requests
- **Auth User Service** (Port 9101) - Authentication and user management
- **Ticket Service** (Port 9102) - Core ticket management
- **Customer Service** (Port 9103) - Customer-specific operations
- **Admin Service** (Port 9104) - Administrative functions
- **Executive Service** (Port 9105) - Support executive operations

## Features

### Customer Portal
- Create and manage support tickets
- Real-time chat with support executives
- Product catalog browsing
- Ticket status tracking
- Rate support experience after resolution

### Executive Portal
- View assigned tickets
- Real-time messaging with customers
- AI-powered reply suggestions
- Draft message management
- Product information overview
- Ticket resolution workflow

### Admin Portal
- User management (Customers, Executives, Admins)
- Product catalog management
- System-wide ticket overview
- Analytics and reporting

### Key Capabilities
- **Real-time Communication**: Live chat between customers and executives
- **Resolution Flow**: Structured ticket resolution with customer confirmation
- **Rating System**: Customer feedback and satisfaction tracking
- **AI Integration**: Smart reply suggestions for executives
- **Responsive Design**: Modern, mobile-friendly UI

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL database
- ngrok (for external access)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/suyashvishwasjadhav/ticket-hub.git
   cd ticket-hub
   ```

2. **Configure environment variables**
   Copy `.env.example` to `.env` and update with your credentials:
   ```bash
   cp .env.example .env
   ```
   
   Required environment variables:
   - `DB_URL` - PostgreSQL database URL
   - `DB_USERNAME` - Database username
   - `DB_PASSWORD` - Database password
   - `AI_API_KEY` - OpenRouter API key for AI features

3. **Start all services**
   ```bash
   bash restart_app.sh
   ```

   This script will:
   - Stop any running services
   - Sync UI files to API Gateway
   - Start all microservices
   - Launch ngrok tunnel

4. **Access the application**
   - Local: `http://localhost:9100`
   - External: Check ngrok URL with `curl http://localhost:4040/api/tunnels`

### Stopping Services

```bash
bash stop-all-services.sh
```

## Project Structure

```
ticket-hub/
├── microservices/
│   ├── api-gateway/          # API Gateway service
│   ├── auth-user-service/    # Authentication service
│   ├── ticket-service/       # Ticket management service
│   ├── customer-service/     # Customer operations service
│   ├── admin-service/        # Admin operations service
│   ├── executive-service/    # Executive operations service
│   └── ui/                   # Frontend assets (HTML, CSS, JS)
├── restart_app.sh            # Start all services
├── stop-all-services.sh      # Stop all services
├── cleanup-for-github.sh     # Clean build artifacts
├── .env.example              # Environment variables template
├── LICENSE                   # MIT License
└── README.md                 # This file
```

## Technology Stack

### Backend
- **Spring Boot** - Microservices framework
- **Spring Cloud Gateway** - API Gateway
- **PostgreSQL** - Database
- **Maven** - Build tool
- **REST APIs** - Inter-service communication

### Frontend
- **Vanilla JavaScript** - No framework dependencies
- **HTML5 & CSS3** - Modern web standards
- **Responsive Design** - Mobile-friendly UI

## Configuration

Each microservice has its own `application.properties` file with:
- Server port configuration
- Database connection settings
- Service-specific properties

All sensitive credentials are managed via environment variables for security.

## API Endpoints

### Customer Endpoints
- `GET /customer/tickets/my/{customerId}` - Get customer's tickets
- `POST /customer/tickets` - Create new ticket
- `POST /customer/messages` - Send message

### Executive Endpoints
- `GET /executive/{executiveId}/tickets` - Get assigned tickets
- `POST /executive/tickets/{ticketId}/messages` - Send message
- `POST /executive/tickets/{ticketId}/resolve` - Resolve ticket
- `GET /executive/ai/tickets/{ticketId}/reply-suggestions` - Get AI suggestions

### Admin Endpoints
- `GET /admin/users` - Get all users
- `POST /admin/users` - Create user
- `GET /admin/products/catalog` - Get product catalog
- `POST /admin/products` - Add product

## Bug Fixes

### Recent Fixes
- **Rating UI Vanishing Issue**: Fixed bug where customer rating interface would disappear immediately after ticket resolution. The issue was caused by polling timer continuing to reload messages, which replaced the rating UI. Solution: Clear polling timer before showing rating UI and prevent duplicate rating containers.

## Contributing

This project is the intellectual property of Suyash Vishwas Jadhav. If you wish to contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

Please ensure all contributions respect the original author's copyright.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

**Copyright (c) 2026 Suyash Vishwas Jadhav**

## Author

**Suyash Vishwas Jadhav**
- GitHub: [@suyashvishwasjadhav](https://github.com/suyashvishwasjadhav)
- Project: [TicketHub](https://github.com/suyashvishwasjadhav/ticket-hub)

## Acknowledgments

- Developed by Suyash Vishwas Jadhav
- Spring Boot team for the excellent framework
- Open source community
