package mssu.in.admin_service.service;

import mssu.in.admin_service.client.UserServiceClient;
import mssu.in.admin_service.dto.CreateUserRequest;
import mssu.in.admin_service.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);

    private final UserServiceClient userServiceClient;

    public UserManagementService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    public List<UserResponse> getAllUsers() {
        return userServiceClient.getAllUsers();
    }

    public List<UserResponse> getUsersByRole(String role) {
        return userServiceClient.getUsersByRole(role);
    }

    public List<UserResponse> getExecutives() {
        return userServiceClient.getUsersByRole("EXECUTIVE");
    }

    public List<UserResponse> getCustomers() {
        return userServiceClient.getUsersByRole("CUSTOMER");
    }

    public UserResponse getUserById(Long userId) {
        return userServiceClient.getUserById(userId);
    }

    public UserResponse createUser(CreateUserRequest request) {
        logger.info("Creating new user with email: {} and role: {}", request.getEmail(), request.getRole());
        return userServiceClient.createUser(request);
    }

    public UserResponse createExecutive(CreateUserRequest request) {
        request.setRole("EXECUTIVE");
        return createUser(request);
    }

    public void updateUserStatus(Long userId, boolean active) {
        logger.info("Updating user {} status to active: {}", userId, active);
        userServiceClient.updateUserStatus(userId, active);
    }

    public void deleteUser(Long userId) {
        logger.info("Deleting user: {}", userId);
        userServiceClient.deleteUser(userId);
    }
}
