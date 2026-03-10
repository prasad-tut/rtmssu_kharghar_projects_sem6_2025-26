package mssu.in.executive_service.service;

import mssu.in.executive_service.client.TicketServiceClient;
import mssu.in.executive_service.client.UserServiceClient;
import mssu.in.executive_service.dto.ExecutiveWorkloadStats;
import mssu.in.executive_service.dto.TicketResponse;
import mssu.in.executive_service.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExecutiveTicketService {

    private static final Logger logger = LoggerFactory.getLogger(ExecutiveTicketService.class);

    private final TicketServiceClient ticketServiceClient;
    private final UserServiceClient userServiceClient;

    public ExecutiveTicketService(TicketServiceClient ticketServiceClient,
            UserServiceClient userServiceClient) {
        this.ticketServiceClient = ticketServiceClient;
        this.userServiceClient = userServiceClient;
    }

    // Helper method to get all tickets including deleted ones (for stats)
    private List<TicketResponse> getAllRawTickets(Long executiveId) {
        logger.info("Getting all raw tickets for executive: {}", executiveId);
        List<TicketResponse> tickets = ticketServiceClient.getTicketsByExecutive(executiveId);

        // Enrich with customer names
        for (TicketResponse ticket : tickets) {
            if (ticket.getCustomerId() != null) {
                UserResponse customer = userServiceClient.getCustomerById(ticket.getCustomerId());
                if (customer != null) {
                    ticket.setCustomerName(customer.getName());
                }
            }
        }
        return tickets;
    }

    // Returns only ACTIVE (non-deleted) tickets for UI lists
    public List<TicketResponse> getMyTickets(Long executiveId) {
        return getAllRawTickets(executiveId).stream()
                .filter(t -> !t.isDeleted())
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getMyOpenTickets(Long executiveId) {
        return getMyTickets(executiveId).stream()
                .filter(t -> "OPEN".equals(t.getStatus()) || "IN_PROGRESS".equals(t.getStatus()))
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getMyResolvedTickets(Long executiveId) {
        return getMyTickets(executiveId).stream()
                .filter(t -> "RESOLVED".equals(t.getStatus()) || "CLOSED".equals(t.getStatus()))
                .collect(Collectors.toList());
    }

    public TicketResponse getTicketDetails(Long ticketId) {
        TicketResponse ticket = ticketServiceClient.getTicketById(ticketId);
        if (ticket != null && ticket.getCustomerId() != null) {
            UserResponse customer = userServiceClient.getCustomerById(ticket.getCustomerId());
            if (customer != null) {
                ticket.setCustomerName(customer.getName());
            }
        }
        return ticket;
    }

    public TicketResponse startWorkingOnTicket(Long ticketId) {
        logger.info("Starting work on ticket: {}", ticketId);
        return ticketServiceClient.updateTicketStatus(ticketId, "IN_PROGRESS");
    }

    public TicketResponse resolveTicket(Long ticketId, String resolutionNotes) {
        logger.info("Resolving ticket: {} with notes: {}", ticketId, resolutionNotes);
        return ticketServiceClient.resolveTicket(ticketId, resolutionNotes);
    }

    public TicketResponse updateTicketStatus(Long ticketId, String status) {
        logger.info("Updating ticket {} status to: {}", ticketId, status);
        return ticketServiceClient.updateTicketStatus(ticketId, status);
    }

    public ExecutiveWorkloadStats getMyWorkloadStats(Long executiveId) {
        logger.info("Getting workload stats for executive: {}", executiveId);

        // Use raw tickets for stats so counts don't decrease on delete
        List<TicketResponse> tickets = getAllRawTickets(executiveId);
        UserResponse executive = userServiceClient.getExecutiveById(executiveId);

        ExecutiveWorkloadStats stats = new ExecutiveWorkloadStats();
        stats.setExecutiveId(executiveId);
        stats.setExecutiveName(executive != null ? executive.getName() : "Unknown");
        stats.setTotalAssigned(tickets.size());

        // Count by status
        stats.setOpenTickets(tickets.stream().filter(t -> "OPEN".equals(t.getStatus())).count());
        stats.setInProgressTickets(tickets.stream().filter(t -> "IN_PROGRESS".equals(t.getStatus())).count());
        stats.setResolvedTickets(tickets.stream().filter(t -> "RESOLVED".equals(t.getStatus())).count());
        stats.setClosedTickets(tickets.stream().filter(t -> "CLOSED".equals(t.getStatus())).count());

        // Group by priority
        Map<String, Long> byPriority = tickets.stream()
                .filter(t -> t.getPriority() != null)
                .collect(Collectors.groupingBy(TicketResponse::getPriority, Collectors.counting()));
        stats.setTicketsByPriority(byPriority);

        // Group by category
        Map<String, Long> byCategory = tickets.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(TicketResponse::getCategory, Collectors.counting()));
        stats.setTicketsByCategory(byCategory);

        // Calculate average rating
        double avgRating = tickets.stream()
                .filter(t -> t.getRating() != null)
                .mapToInt(TicketResponse::getRating)
                .average()
                .orElse(0.0);
        stats.setAverageRating(avgRating);

        return stats;
    }
}
