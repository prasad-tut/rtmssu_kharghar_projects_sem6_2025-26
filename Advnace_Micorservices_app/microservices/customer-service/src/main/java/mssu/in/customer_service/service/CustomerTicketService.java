package mssu.in.customer_service.service;

import mssu.in.customer_service.client.TicketServiceClient;
import mssu.in.customer_service.dto.CreateTicketRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerTicketService {

    private final TicketServiceClient ticketServiceClient;
    private final mssu.in.customer_service.client.UserServiceClient userServiceClient;

    public CustomerTicketService(TicketServiceClient ticketServiceClient,
            mssu.in.customer_service.client.UserServiceClient userServiceClient) {
        this.ticketServiceClient = ticketServiceClient;
        this.userServiceClient = userServiceClient;
    }

    public Map<String, Object> raiseTicket(CreateTicketRequest request) {
        Map<String, Object> ticketRequest = new HashMap<>();
        ticketRequest.put("customerId", request.getCustomerId());
        ticketRequest.put("productId", request.getProductId());
        ticketRequest.put("title", request.getTitle());
        ticketRequest.put("description", request.getDescription());
        ticketRequest.put("priority", request.getPriority());
        ticketRequest.put("category", request.getCategory());

        return ticketServiceClient.createTicket(ticketRequest);
    }

    @SuppressWarnings("unchecked")
    public Object[] getMyTickets(Long customerId) {
        Object[] tickets = ticketServiceClient.getTicketsByCustomer(customerId);
        if (tickets != null) {
            for (Object obj : tickets) {
                enrichTicket((Map<String, Object>) obj);
            }
        }
        return tickets;
    }

    public Map<String, Object> getTicketDetails(Long ticketId) {
        Map<String, Object> ticket = ticketServiceClient.getTicketById(ticketId);
        if (ticket != null) {
            enrichTicket(ticket);
        }
        return ticket;
    }

    private void enrichTicket(Map<String, Object> ticket) {
        if (ticket == null)
            return;

        Number customerId = (Number) ticket.get("customerId");
        if (customerId != null) {
            ticket.put("customerName", userServiceClient.getUserName(customerId.longValue()));
        }

        Number executiveId = (Number) ticket.get("executiveId");
        if (executiveId != null) {
            ticket.put("executiveName", userServiceClient.getUserName(executiveId.longValue()));
        }
    }

    public void closeTicket(Long ticketId) {
        // First check if ticket is in RESOLVED status
        Map<String, Object> ticket = ticketServiceClient.getTicketById(ticketId);
        String status = (String) ticket.get("status");

        if ("RESOLVED".equals(status)) {
            ticketServiceClient.updateTicketStatus(ticketId, "CLOSED");
        } else {
            throw new IllegalStateException(
                    "Can only close tickets that are in RESOLVED status. Current status: " + status);
        }
    }

    public void reopenTicket(Long ticketId) {
        Map<String, Object> ticket = ticketServiceClient.getTicketById(ticketId);
        String status = (String) ticket.get("status");

        if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
            ticketServiceClient.updateTicketStatus(ticketId, "REOPENED");
        } else {
            throw new IllegalStateException(
                    "Can only reopen tickets that are RESOLVED or CLOSED. Current status: " + status);
        }
    }

    public void rateTicket(Long ticketId, Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        ticketServiceClient.rateTicket(ticketId, rating);
    }
}
