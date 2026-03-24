# Microservices Communication Implementation Details

This document explains everything we did step-by-step to get the two services communicating with each other. Read this before your demo so you can explain exactly how the code works!

## The Goal
The primary requirement of the assignment was to establish "Inter-Service Communication" between the `user_service` and `ticket_service` using **Spring Cloud OpenFeign** instead of the older `RestTemplate` approach. We also needed to make sure they stored their data in their own distinct local MySQL databases.

Here is the exact step-by-step breakdown of how you wrote and configured the code.

---

### Step 1: Adding the Dependencies (`pom.xml`)
To use OpenFeign, we first needed to add the library to the project.
- We added `spring-cloud-starter-openfeign` to both `user_service` and `ticket_service` `pom.xml` files.
- We also noticed a version conflict (the faculty's base template had Spring Boot `4.0.1` but a Spring Cloud version built for `3.5.x`), so we successfully **downgraded Spring Boot to `3.5.9`** to fix the `ClassNotFoundException` and make it compatible with the `eureka_server_app`.
- We also switched the broken `spring-boot-starter-webmvc` dependency to the correct `spring-boot-starter-web` module to pull in the Apache Tomcat server automatically.

### Step 2: Enabling OpenFeign
Just adding the dependency isn't enough; Spring Boot needs to know to look for OpenFeign interfaces.
- We went into `UserMicroServiceApplication.java` and `TicketServiceApplication.java`.
- We added the `@EnableFeignClients` annotation to the top of both main classes. 

### Step 3: Creating the Data Transfer Objects (DTOs)
Because the `user_service` doesn't have the `Ticket` database table, it doesn't know what a Ticket looks like. The same is true for `ticket_service` not having a `User` database table.
- We created a `TicketDTO` class inside the `user_service`. This is a plain Java class that perfectly mirrors the Ticket properties (like `issue`, `status`, `assignedTo`) so that when the ticket service sends back JSON data, Jackson can map it directly into standard Java objects.

### Step 4: Writing the OpenFeign Interfaces (The "Clients")
This is the core of the assignment! OpenFeign essentially allows us to write HTTP clients by *just writing interfaces*. We don't have to write any actual HTTP request-handling code.

**In the Ticket Service (`UserServiceClient.java`)**
- We created an interface annotated with `@FeignClient(name = "user-micro-service")`. 
- Inside it, we defined the method: `@GetMapping("/users/by-id/{id}") UserDTO getUserById(@PathVariable("id") int id);`
- By providing the logical name `"user-micro-service"`, OpenFeign knows to ask the Eureka server for the IP address of that service automatically. It completely eliminates hardcoding URLs!

**In the User Service (`TicketServiceClient.java`)**
- We created an interface annotated with `@FeignClient(name = "ticket-service")`.
- Inside it, we defined the method: `@GetMapping("/tickets/users/{userId}") List<TicketDTO> getTicketsByUserId(@PathVariable("userId") int userId);`

### Step 5: Connecting the Controllers
Once the Clients were written, we had to expose the actual endpoints required by the assignment constraints.

**In `UserController.java`:**
- We `autowired` (injected) the `TicketServiceClient`.
- We created a `@GetMapping("/users/{userId}/tickets")` endpoint. When hit, it just calls `ticketServiceClient.getTicketsByUserId(userId)` and returns the results directly to the user.

**In `TicketController.java`:**
- We `autowired` the `UserServiceClient`.
- We created a `@GetMapping("/tickets/{ticketId}/user")` endpoint. 
- When hit, it first does a `.findById(ticketId)` to find the specific ticket locally. It then takes `ticket.getRaisedBy()` and passes that ID over the network using `userServiceClient.getUserById(id)` to dynamically fetch the user details from the User Service.

### Step 6: Automating the Database Setup
Instead of writing manual SQL commands in workbench, we leveraged Hibernate's object-relational mapping (ORM).
- Found in: `src/main/resources/application.properties`
- We set the database URL to `jdbc:mysql://127.0.0.1:3306/ticket_service_db?createDatabaseIfNotExist=true`. 
- That special parameter dynamically builds the new microservice schema if you don't already have one.
- We set `spring.jpa.hibernate.ddl-auto=update`. When Spring starts, Hibernate inspects your `@Entity` classes and seamlessly creates the `user` and `ticket` tables behind the scenes for you.
  
