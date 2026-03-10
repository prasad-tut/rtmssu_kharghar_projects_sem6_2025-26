package mssu.in.executive_service.client;

import mssu.in.executive_service.dto.MessageResponse;
import mssu.in.executive_service.dto.SendMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class CustomerServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceClient.class);

    private final RestTemplate restTemplate;
    private final String customerServiceUrl;

    public CustomerServiceClient(RestTemplate restTemplate,
                                @Value("${customer.service.url}") String customerServiceUrl) {
        this.restTemplate = restTemplate;
        this.customerServiceUrl = customerServiceUrl;
    }

    public List<MessageResponse> getMessagesForTicket(Long ticketId) {
        try {
            String url = customerServiceUrl + "/api/customer/messages/ticket/" + ticketId;
            ResponseEntity<List<MessageResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MessageResponse>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching messages for ticket {}: {}", ticketId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public MessageResponse sendMessage(SendMessageRequest request) {
        try {
            String url = customerServiceUrl + "/api/customer/messages";
            return restTemplate.postForObject(url, request, MessageResponse.class);
        } catch (Exception e) {
            logger.error("Error sending message: {}", e.getMessage());
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }
    }

    public long getUnreadCountForTicket(Long ticketId) {
        try {
            String url = customerServiceUrl + "/api/customer/messages/ticket/" + ticketId + "/unread-count";
            Long count = restTemplate.getForObject(url, Long.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("Error getting unread count for ticket {}: {}", ticketId, e.getMessage());
            return 0;
        }
    }

    public void markMessagesAsRead(Long ticketId, Long executiveId) {
        try {
            String url = customerServiceUrl + "/api/customer/messages/ticket/" + ticketId + "/mark-read?readerId=" + executiveId;
            restTemplate.postForObject(url, null, Void.class);
        } catch (Exception e) {
            logger.error("Error marking messages as read for ticket {}: {}", ticketId, e.getMessage());
        }
    }
}
