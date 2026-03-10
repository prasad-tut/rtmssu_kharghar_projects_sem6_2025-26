package mssu.in.admin_service.dto;

import java.util.Map;

public class DashboardStats {
    private long totalUsers;
    private long totalCustomers;
    private long totalExecutives;
    private long totalAdmins;
    private long totalTickets;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long closedTickets;
    private long pendingTickets;
    private Map<String, Long> ticketsByCategory;
    private Map<String, Long> ticketsByPriority;
    private Map<String, Long> executiveWorkload;
    private double averageResolutionTime;
    private double averageRating;

    public DashboardStats() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getTotalExecutives() { return totalExecutives; }
    public void setTotalExecutives(long totalExecutives) { this.totalExecutives = totalExecutives; }

    public long getTotalAdmins() { return totalAdmins; }
    public void setTotalAdmins(long totalAdmins) { this.totalAdmins = totalAdmins; }

    public long getTotalTickets() { return totalTickets; }
    public void setTotalTickets(long totalTickets) { this.totalTickets = totalTickets; }

    public long getOpenTickets() { return openTickets; }
    public void setOpenTickets(long openTickets) { this.openTickets = openTickets; }

    public long getInProgressTickets() { return inProgressTickets; }
    public void setInProgressTickets(long inProgressTickets) { this.inProgressTickets = inProgressTickets; }

    public long getResolvedTickets() { return resolvedTickets; }
    public void setResolvedTickets(long resolvedTickets) { this.resolvedTickets = resolvedTickets; }

    public long getClosedTickets() { return closedTickets; }
    public void setClosedTickets(long closedTickets) { this.closedTickets = closedTickets; }

    public long getPendingTickets() { return pendingTickets; }
    public void setPendingTickets(long pendingTickets) { this.pendingTickets = pendingTickets; }

    public Map<String, Long> getTicketsByCategory() { return ticketsByCategory; }
    public void setTicketsByCategory(Map<String, Long> ticketsByCategory) { this.ticketsByCategory = ticketsByCategory; }

    public Map<String, Long> getTicketsByPriority() { return ticketsByPriority; }
    public void setTicketsByPriority(Map<String, Long> ticketsByPriority) { this.ticketsByPriority = ticketsByPriority; }

    public Map<String, Long> getExecutiveWorkload() { return executiveWorkload; }
    public void setExecutiveWorkload(Map<String, Long> executiveWorkload) { this.executiveWorkload = executiveWorkload; }

    public double getAverageResolutionTime() { return averageResolutionTime; }
    public void setAverageResolutionTime(double averageResolutionTime) { this.averageResolutionTime = averageResolutionTime; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
}
