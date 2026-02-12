# Conquest Microservice Ticket Management - Frontend

## 🚀 Quick Start

### 1. Start Backend Services
From the project root directory:
```bash
./start.sh
```

Wait until all services are running:
- Eureka Server: http://localhost:9000
- User Service: http://localhost:9001
- Ticket Service: http://localhost:9002
- API Gateway: http://localhost:9090

### 2. Open Frontend
Simply open the `index.html` file in your browser:

**Option 1: Direct File Access**
```
file:///Users/macm2/Downloads/Basic_Microservice_ticket/frontend/index.html
```

**Option 2: Using Python HTTP Server**
```bash
cd /Users/macm2/Downloads/Basic_Microservice_ticket/frontend
python3 -m http.server 8000
```
Then open: http://localhost:8000

**Option 3: Using VS Code Live Server**
- Install "Live Server" extension
- Right-click on `index.html`
- Select "Open with Live Server"

## 📋 Features

### ✨ Professional UI Design
- Clean white theme with modern aesthetics
- Responsive design for all screen sizes
- Smooth animations and transitions
- Industry-standard layout and spacing

### 👥 User Management
- Create new users (Customer, Executive, Admin)
- View all users in a searchable table
- Edit user information
- Delete users
- Real-time stats

### 🎫 Ticket Management
- Create new tickets
- Assign tickets to users
- Update ticket status (Open, Assigned, Closed)
- Search and filter tickets
- Track ticket history

### 📊 Dashboard
- Total users count
- Open tickets count
- Assigned tickets count
- Closed tickets count

## 🔧 Configuration

The frontend connects to the API Gateway at:
```javascript
const API_BASE_URL = 'http://localhost:9090';
```

API Endpoints:
- Users: `http://localhost:9090/user-micro-service/users`
- Tickets: `http://localhost:9090/ticket-service/tickets`

## 🎨 Design System

### Colors
- Primary: #2563eb (Blue)
- Success: #10b981 (Green)
- Warning: #f59e0b (Orange)
- Error: #ef4444 (Red)
- Gray Scale: #f9fafb to #111827

### Typography
- Font Family: Inter (Google Fonts)
- Professional, clean, and modern

### Components
- Buttons with hover effects
- Modal dialogs
- Toast notifications
- Loading states
- Empty states
- Status badges

## 📱 Browser Compatibility
- Chrome (Recommended)
- Firefox
- Safari
- Edge

## 🐛 Troubleshooting

### "Failed to load users/tickets"
- Ensure all backend services are running
- Check that API Gateway is accessible at http://localhost:9090
- Open browser console (F12) to see detailed error messages

### CORS Errors
- Make sure your Spring Boot services have CORS enabled
- The frontend uses standard fetch API

### Data Not Showing
1. Refresh the page
2. Click the "Refresh" button in the header
3. Check browser console for errors
4. Verify backend services are running

## 📝 Notes

- The UI automatically loads data on page load
- All operations (Create, Update, Delete) show toast notifications
- Search is case-insensitive and searches across all fields
- Modal forms include validation
