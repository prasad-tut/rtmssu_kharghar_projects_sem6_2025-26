# 🔐 Auth User Service Codebase Guide

The `auth-user-service` is responsible for Identity Management. It handles Users, Passwords, and Tokens.

## 📂 Root & Configuration
### `src/main/resources/application.properties`
- **Purpose**: Configures **Port 9101**, Supabase (PostgreSQL) connection, and JWT Secret Key.

### `SecurityConfig.java`
- **Purpose**: Sets up Spring Security.
- **Why**: It tells Spring *not* to use the default session-based login and instead use our custom JWT filter. It also defines which endpoints (like `/login`) are public.

---

## 📂 Controllers (`controller/`)
### `AuthController.java`
- **Endpoints**: `POST /auth/login`, `POST /auth/signup`.
- **Purpose**: The door for entering the system. Accepts email/password and returns a JWT String.

### `UserController.java`
- **Endpoints**: `GET /users/{id}`, `PUT /users/update`.
- **Purpose**: Management of user profiles. Allows other services (like Admin) to fetch user details using an internal API call.

---

## 📂 Entity & Repository (`entity/`, `repository/`)
### `User.java`
- **Table**: `users` (in Supabase).
- **Columns**: `email`, `password` (hashed), `role` (ADMIN/CUSTOMER/EXECUTIVE).
- **Why**: The central definition of a "person" in our system.

### `Role.java`
- **Type**: Enum.
- **Values**: `ADMIN`, `CUSTOMER`, `EXECUTIVE`.
- **Why**: Strict typing for permissions.

### `UserRepository.java`
- **Purpose**: SQL queries for finding users by Email or ID.

---

## 📂 Security (`security/`)
### `JwtTokenProvider.java`
- **What**: The Encryption/Decryption engine.
- **Why**: It takes a User ID and "signs" it with a secret key to create a token. Later, it verifies if a token is valid and hasn't been tampered with.

### `JwtAuthenticationFilter.java`
- **What**: A Filter that runs before every request.
- **Why**: It checks the HTTP Header `Authorization: Bearer <token>`. If the token is valid, it lets the request proceed; otherwise, it blocks it.

---

## 📂 Service (`service/`)
### `AuthenticationService.java`
- **What**: Business logic for logging in.
- **Why**: Checks if the password matches the hash in the database.

### `UserService.java`
- **What**: Logic for creating and updating users.
- **Why**: Ensures no two users have the same email and handles password encoding before saving.
