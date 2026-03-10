package mssu.in.admin_service.service;

import mssu.in.admin_service.client.TicketServiceClient;
import mssu.in.admin_service.client.UserServiceClient;
import mssu.in.admin_service.dto.TicketResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TicketManagementService {

    private static final Logger logger = LoggerFactory.getLogger(TicketManagementService.class);

    private final TicketServiceClient ticketServiceClient;
    private final UserServiceClient userServiceClient;

    public TicketManagementService(TicketServiceClient ticketServiceClient, UserServiceClient userServiceClient) {
        this.ticketServiceClient = ticketServiceClient;
        this.userServiceClient = userServiceClient;
    }

    public List<TicketResponse> getAllTickets() {
        List<TicketResponse> tickets = ticketServiceClient.getAllTickets();
        enrichTickets(tickets);
        return tickets;
    }

    private void enrichTickets(List<TicketResponse> tickets) {
        if (tickets.isEmpty())
            return;

        List<mssu.in.admin_service.dto.UserResponse> allUsers = userServiceClient.getAllUsers();
        Map<Long, String> userMap = allUsers.stream()
                .collect(java.util.stream.Collectors.toMap(mssu.in.admin_service.dto.UserResponse::getId,
                        mssu.in.admin_service.dto.UserResponse::getName, (a, b) -> a));

        for (TicketResponse ticket : tickets) {
            if (ticket.getCustomerId() != null) {
                ticket.setCustomerName(
                        userMap.getOrDefault(ticket.getCustomerId(), "Customer #" + ticket.getCustomerId()));
            }
            if (ticket.getExecutiveId() != null) {
                ticket.setExecutiveName(
                        userMap.getOrDefault(ticket.getExecutiveId(), "Executive #" + ticket.getExecutiveId()));
            }
        }
    }

    public List<TicketResponse> getTicketsByStatus(String status) {
        List<TicketResponse> tickets = ticketServiceClient.getTicketsByStatus(status);
        enrichTickets(tickets);
        return tickets;
    }

    public List<TicketResponse> getTicketsByExecutive(Long executiveId) {
        List<TicketResponse> tickets = ticketServiceClient.getTicketsByExecutive(executiveId);
        enrichTickets(tickets);
        return tickets;
    }

    public TicketResponse getTicketById(Long ticketId) {
        TicketResponse ticket = ticketServiceClient.getTicketById(ticketId);
        if (ticket != null) {
            enrichTickets(List.of(ticket));
        }
        return ticket;
    }

    public List<TicketResponse> getUnassignedTickets() {
        List<TicketResponse> tickets = ticketServiceClient.getUnassignedTickets();
        enrichTickets(tickets);
        return tickets;
    }

    public TicketResponse assignTicketToExecutive(Long ticketId, Long executiveId) {
        logger.info("Assigning ticket {} to executive {}", ticketId, executiveId);
        TicketResponse ticket = ticketServiceClient.assignTicketToExecutive(ticketId, executiveId);
        if (ticket != null) {
            enrichTickets(List.of(ticket));
        }
        return ticket;
    }

    public TicketResponse updateTicketStatus(Long ticketId, String status) {
        logger.info("Updating ticket {} status to {}", ticketId, status);
        return ticketServiceClient.updateTicketStatus(ticketId, status);
    }

    public TicketResponse closeTicket(Long ticketId) {
        logger.info("Closing ticket {}", ticketId);
        return ticketServiceClient.updateTicketStatus(ticketId, "CLOSED");
    }

    public Map<String, Long> getTicketStats() {
        return ticketServiceClient.getTicketStats();
    }

    public void deleteTicket(Long ticketId) {
        logger.info("Deleting ticket {}", ticketId);
        ticketServiceClient.deleteTicket(ticketId);
    }
}
