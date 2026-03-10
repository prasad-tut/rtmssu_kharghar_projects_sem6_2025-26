package mssu.in.executive_service.client;

import mssu.in.executive_service.dto.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AdminProductClient {

    private static final Logger logger = LoggerFactory.getLogger(AdminProductClient.class);

    private final RestTemplate restTemplate;
    private final String adminServiceUrl;

    public AdminProductClient(RestTemplate restTemplate,
                              @Value("${admin.service.url:http://localhost:9104}") String adminServiceUrl) {
        this.restTemplate = restTemplate;
        this.adminServiceUrl = adminServiceUrl;
    }

    public ProductResponse getProductById(Long productId) {
        try {
            String url = adminServiceUrl + "/api/admin/products/" + productId;
            return restTemplate.getForObject(url, ProductResponse.class);
        } catch (Exception e) {
            logger.error("Error fetching product {} from admin-service: {}", productId, e.getMessage());
            return null;
        }
    }
}

