package mssu.in.admin_service.client;

import mssu.in.admin_service.dto.TicketResponse;
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

    public List<TicketResponse> getAllTickets() {
        try {
            String url = ticketServiceUrl + "/api/tickets";
            ResponseEntity<List<TicketResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<TicketResponse>>() {
                    });
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching all tickets: {}", e.getMessage());
            return Collections.emptyList();
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

    public TicketResponse assignTicketToExecutive(Long ticketId, Long executiveId) {
        try {
            String url = ticketServiceUrl + "/api/tickets/" + ticketId + "/assign";
            Map<String, Long> body = Map.of("executiveId", executiveId);
            return restTemplate.postForObject(url, body, TicketResponse.class);
        } catch (Exception e) {
            logger.error("Error assigning ticket {} to executive {}: {}", ticketId, executiveId, e.getMessage());
            throw new RuntimeException("Failed to assign ticket: " + e.getMessage());
        }
    }

    public TicketResponse updateTicketStatus(Long ticketId, String status) {
        try {
            String url = ticketServiceUrl + "/api/tickets/" + ticketId + "/status";
            Map<String, String> body = Map.of("status", status);
            return restTemplate.postForObject(url, body, TicketResponse.class);
        } catch (Exception e) {
            logger.error("Error updating ticket {} status to {}: {}", ticketId, status, e.getMessage());
            throw new RuntimeException("Failed to update ticket status: " + e.getMessage());
        }
    }

    public Map<String, Long> getTicketStats() {
        try {
            String url = ticketServiceUrl + "/api/tickets/stats";
            ResponseEntity<Map<String, Long>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Long>>() {
                    });
            return response.getBody() != null ? response.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            logger.error("Error fetching ticket stats: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    public List<TicketResponse> getUnassignedTickets() {
        try {
            String url = ticketServiceUrl + "/api/tickets/unassigned";
            ResponseEntity<List<TicketResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<TicketResponse>>() {
                    });
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching unassigned tickets: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void deleteTicket(Long ticketId) {
        try {
            String url = ticketServiceUrl + "/api/tickets/" + ticketId;
            restTemplate.delete(url);
        } catch (Exception e) {
            logger.error("Error deleting ticket {}: {}", ticketId, e.getMessage());
            throw new RuntimeException("Failed to delete ticket: " + e.getMessage());
        }
    }
}
