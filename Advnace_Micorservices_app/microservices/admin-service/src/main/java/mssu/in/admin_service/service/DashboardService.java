package mssu.in.admin_service.service;

import mssu.in.admin_service.client.TicketServiceClient;
import mssu.in.admin_service.client.UserServiceClient;
import mssu.in.admin_service.dto.DashboardStats;
import mssu.in.admin_service.dto.TicketResponse;
import mssu.in.admin_service.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    private final UserServiceClient userServiceClient;
    private final TicketServiceClient ticketServiceClient;

    public DashboardService(UserServiceClient userServiceClient, TicketServiceClient ticketServiceClient) {
        this.userServiceClient = userServiceClient;
        this.ticketServiceClient = ticketServiceClient;
    }

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        // Get user statistics (exclude admin from total count)
        List<UserResponse> allUsers = userServiceClient.getAllUsers();
        long nonAdminUsers = allUsers.stream().filter(u -> !"ADMIN".equals(u.getRole())).count();
        stats.setTotalUsers(nonAdminUsers);
        stats.setTotalCustomers(allUsers.stream().filter(u -> "CUSTOMER".equals(u.getRole())).count());
        stats.setTotalExecutives(allUsers.stream().filter(u -> "EXECUTIVE".equals(u.getRole())).count());
        stats.setTotalAdmins(allUsers.stream().filter(u -> "ADMIN".equals(u.getRole())).count());

        // Get ticket statistics
        Map<String, Long> ticketStats = ticketServiceClient.getTicketStats();
        stats.setOpenTickets(ticketStats.getOrDefault("OPEN", 0L));
        stats.setInProgressTickets(ticketStats.getOrDefault("IN_PROGRESS", 0L));
        stats.setResolvedTickets(ticketStats.getOrDefault("RESOLVED", 0L));
        stats.setClosedTickets(ticketStats.getOrDefault("CLOSED", 0L));
        stats.setPendingTickets(ticketStats.getOrDefault("PENDING", 0L));

        // Get all tickets for detailed analysis
        List<TicketResponse> allTickets = ticketServiceClient.getAllTickets();
        // Set total tickets from actual ticket count, not sum of statuses
        stats.setTotalTickets((long) allTickets.size());

        // Tickets by category
        Map<String, Long> ticketsByCategory = allTickets.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(TicketResponse::getCategory, Collectors.counting()));
        stats.setTicketsByCategory(ticketsByCategory);

        // Tickets by priority
        Map<String, Long> ticketsByPriority = allTickets.stream()
                .filter(t -> t.getPriority() != null)
                .collect(Collectors.groupingBy(TicketResponse::getPriority, Collectors.counting()));
        stats.setTicketsByPriority(ticketsByPriority);

        // Executive workload
        Map<String, Long> executiveWorkload = new HashMap<>();
        for (TicketResponse ticket : allTickets) {
            if (ticket.getExecutiveId() != null &&
                    ("OPEN".equals(ticket.getStatus()) || "IN_PROGRESS".equals(ticket.getStatus()))) {
                String executiveKey = "Executive_" + ticket.getExecutiveId();
                executiveWorkload.merge(executiveKey, 1L, Long::sum);
            }
        }
        stats.setExecutiveWorkload(executiveWorkload);

        // Calculate average rating
        double avgRating = allTickets.stream()
                .filter(t -> t.getRating() != null)
                .mapToInt(TicketResponse::getRating)
                .average()
                .orElse(0.0);
        stats.setAverageRating(Math.round(avgRating * 100.0) / 100.0);

        logger.info("Dashboard stats generated: {} total users, {} total tickets",
                stats.getTotalUsers(), stats.getTotalTickets());

        return stats;
    }
}
