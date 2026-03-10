package mssu.in.admin_service.controller;

import mssu.in.admin_service.dto.CreateUserRequest;
import mssu.in.admin_service.dto.UserResponse;
import mssu.in.admin_service.service.UserManagementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        logger.info("Getting all users");
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        logger.info("Getting users by role: {}", role);
        return ResponseEntity.ok(userManagementService.getUsersByRole(role.toUpperCase()));
    }

    @GetMapping("/executives")
    public ResponseEntity<List<UserResponse>> getExecutives() {
        logger.info("Getting all executives");
        return ResponseEntity.ok(userManagementService.getExecutives());
    }

    @GetMapping("/customers")
    public ResponseEntity<List<UserResponse>> getCustomers() {
        logger.info("Getting all customers");
        return ResponseEntity.ok(userManagementService.getCustomers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        logger.info("Getting user by ID: {}", userId);
        UserResponse user = userManagementService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        logger.info("Creating new user with email: {}", request.getEmail());
        UserResponse user = userManagementService.createUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/executives")
    public ResponseEntity<UserResponse> createExecutive(@Valid @RequestBody CreateUserRequest request) {
        logger.info("Creating new executive with email: {}", request.getEmail());
        UserResponse user = userManagementService.createExecutive(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean active) {
        logger.info("Updating user {} status to active: {}", userId, active);
        userManagementService.updateUserStatus(userId, active);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        logger.info("Deleting user: {}", userId);
        userManagementService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
