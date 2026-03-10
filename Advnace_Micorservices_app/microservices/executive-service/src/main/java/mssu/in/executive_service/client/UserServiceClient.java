package mssu.in.executive_service.client;

import mssu.in.executive_service.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    public UserResponse getUserById(Long userId) {
        try {
            String url = userServiceUrl + "/api/users/" + userId;
            return restTemplate.getForObject(url, UserResponse.class);
        } catch (Exception e) {
            logger.error("Error fetching user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    public UserResponse getExecutiveById(Long executiveId) {
        return getUserById(executiveId);
    }

    public UserResponse getCustomerById(Long customerId) {
        return getUserById(customerId);
    }
}
