package mssu.in.customer_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class TicketServiceClient {

    private final RestTemplate restTemplate;

    @Value("${ticket.service.url}")
    private String ticketServiceUrl;

    public TicketServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> createTicket(Map<String, Object> ticketRequest) {
        String url = ticketServiceUrl + "/api/tickets";
        return restTemplate.postForObject(url, ticketRequest, Map.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getTicketById(Long ticketId) {
        String url = ticketServiceUrl + "/api/tickets/" + ticketId;
        return restTemplate.getForObject(url, Map.class);
    }

    @SuppressWarnings("unchecked")
    public Object[] getTicketsByCustomer(Long customerId) {
        String url = ticketServiceUrl + "/api/tickets/customer/" + customerId;
        return restTemplate.getForObject(url, Object[].class);
    }

    public void updateTicketStatus(Long ticketId, String status) {
        String url = ticketServiceUrl + "/api/tickets/" + ticketId + "/status";
        restTemplate.patchForObject(url, Map.of("status", status), Map.class);
    }

    public void rateTicket(Long ticketId, Integer rating) {
        String url = ticketServiceUrl + "/api/tickets/" + ticketId + "/rating";
        restTemplate.patchForObject(url, Map.of("rating", rating), Map.class);
    }
}
