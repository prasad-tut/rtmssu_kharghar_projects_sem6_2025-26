package mssu.in.admin_service.client;

import mssu.in.admin_service.dto.CreateUserRequest;
import mssu.in.admin_service.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserServiceClient(RestTemplate restTemplate,
                            @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public List<UserResponse> getAllUsers() {
        try {
            String url = userServiceUrl + "/api/users";
            ResponseEntity<List<UserResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserResponse>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching all users: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<UserResponse> getUsersByRole(String role) {
        try {
            String url = userServiceUrl + "/api/users/role/" + role;
            ResponseEntity<List<UserResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserResponse>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching users by role {}: {}", role, e.getMessage());
            return Collections.emptyList();
        }
    }

    public UserResponse getUserById(Long userId) {
        try {
            String url = userServiceUrl + "/api/users/" + userId;
            return restTemplate.getForObject(url, UserResponse.class);
        } catch (Exception e) {
            logger.error("Error fetching user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    public UserResponse createUser(CreateUserRequest request) {
        try {
            String url = userServiceUrl + "/auth/signup";
            Map<String, Object> signupRequest = Map.of(
                "name", request.getName(),
                "email", request.getEmail(),
                "password", request.getPassword(),
                "phone", request.getPhone() != null ? request.getPhone() : "",
                "role", request.getRole()
            );
            return restTemplate.postForObject(url, signupRequest, UserResponse.class);
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    public void updateUserStatus(Long userId, boolean active) {
        try {
            String url = userServiceUrl + "/api/users/" + userId + "/status?active=" + active;
            restTemplate.put(url, null);
        } catch (Exception e) {
            logger.error("Error updating user status: {}", e.getMessage());
            throw new RuntimeException("Failed to update user status: " + e.getMessage());
        }
    }

    public void deleteUser(Long userId) {
        try {
            String url = userServiceUrl + "/api/users/" + userId;
            restTemplate.delete(url);
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage());
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }
    }
}
