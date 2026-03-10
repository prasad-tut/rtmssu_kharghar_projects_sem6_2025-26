package mssu.in.executive_service.client;

import mssu.in.executive_service.dto.TicketResponse;
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
import java.util.Map;

@Component
public class TicketServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceClient.class);

    private final RestTemplate restTemplate;
    private final String ticketServiceUrl;

    public TicketServiceClient(RestTemplate restTemplate,
            @Value("${ticket.service.url}") String ticketServiceUrl) {
        this.restTemplate = restTemplate;
        this.ticketServiceUrl = ticketServiceUrl;
    }

    public List<TicketResponse> getTicketsByExecutive(Long executiveId) {
        try {
            String url = ticketServiceUrl + "/api/tickets/executive/" + executiveId;
            ResponseEntity<List<TicketResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<TicketResponse>>() {
                    });
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching tickets for executive {}: {}", executiveId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public TicketResponse getTicketById(Long ticketId) {
        try {
            String url = ticketServiceUrl + "/api/tickets/" + ticketId;
            return restTemplate.getForObject(url, TicketResponse.class);
        } catch (Exception e) {
            logger.error("Error fetching ticket {}: {}", ticketId, e.getMessage());
            return null;
        }
    }

    public TicketResponse updateTicketStatus(Long ticketId, String status) {
        try {
            String url = ticketServiceUrl + "/api/tickets/" + ticketId + "/status";
            Map<String, String> body = Map.of("status", status);
            return restTemplate.postForObject(url, body, TicketResponse.class);
        } catch (Exception e) {
            logger.error("Error updating ticket {} status: {}", ticketId, e.getMessage());
            throw new RuntimeException("Failed to update ticket status: " + e.getMessage());
        }
    }

    public TicketResponse resolveTicket(Long ticketId, String resolutionNotes) {
        try {
            String url = ticketServiceUrl + "/api/tickets/" + ticketId + "/resolve";
            return restTemplate.postForObject(url, resolutionNotes, TicketResponse.class);
        } catch (Exception e) {
            logger.error("Error resolving ticket {}: {}", ticketId, e.getMessage());
            throw new RuntimeException("Failed to resolve ticket: " + e.getMessage());
        }
    }

    public List<TicketResponse> getTicketsByStatus(String status) {
        try {
            String url = ticketServiceUrl + "/api/tickets/status/" + status;
            ResponseEntity<List<TicketResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<TicketResponse>>() {
                    });
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching tickets by status {}: {}", status, e.getMessage());
            return Collections.emptyList();
        }
    }
}
