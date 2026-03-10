package mssu.in.customer_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    private final Map<Long, String> nameCache = new java.util.concurrent.ConcurrentHashMap<>();

    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getUserById(Long userId) {
        try {
            String url = userServiceUrl + "/api/users/" + userId;
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getUserName(Long userId) {
        if (userId == null)
            return "Unknown";

        // Return from cache if available
        if (nameCache.containsKey(userId)) {
            return nameCache.get(userId);
        }

        Map<String, Object> user = getUserById(userId);
        if (user != null) {
            String name = (String) user.get("name");
            if (name != null) {
                nameCache.put(userId, name);
                return name;
            }
        }
        return "Unknown";
    }
}
