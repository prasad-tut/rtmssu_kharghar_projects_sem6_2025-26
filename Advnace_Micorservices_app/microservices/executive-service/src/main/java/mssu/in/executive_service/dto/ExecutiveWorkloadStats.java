package mssu.in.executive_service.dto;

import java.util.Map;

public class ExecutiveWorkloadStats {
    private Long executiveId;
    private String executiveName;
    private long totalAssigned;
    private long openTickets;
    private long inProgressTickets;
    private long resolvedTickets;
    private long closedTickets;
    private Map<String, Long> ticketsByPriority;
    private Map<String, Long> ticketsByCategory;
    private double averageRating;
    private double averageResolutionTimeHours;

    public ExecutiveWorkloadStats() {}

    public Long getExecutiveId() { return executiveId; }
    public void setExecutiveId(Long executiveId) { this.executiveId = executiveId; }

    public String getExecutiveName() { return executiveName; }
    public void setExecutiveName(String executiveName) { this.executiveName = executiveName; }

    public long getTotalAssigned() { return totalAssigned; }
    public void setTotalAssigned(long totalAssigned) { this.totalAssigned = totalAssigned; }

    public long getOpenTickets() { return openTickets; }
    public void setOpenTickets(long openTickets) { this.openTickets = openTickets; }

    public long getInProgressTickets() { return inProgressTickets; }
    public void setInProgressTickets(long inProgressTickets) { this.inProgressTickets = inProgressTickets; }

    public long getResolvedTickets() { return resolvedTickets; }
    public void setResolvedTickets(long resolvedTickets) { this.resolvedTickets = resolvedTickets; }

    public long getClosedTickets() { return closedTickets; }
    public void setClosedTickets(long closedTickets) { this.closedTickets = closedTickets; }

    public Map<String, Long> getTicketsByPriority() { return ticketsByPriority; }
    public void setTicketsByPriority(Map<String, Long> ticketsByPriority) { this.ticketsByPriority = ticketsByPriority; }

    public Map<String, Long> getTicketsByCategory() { return ticketsByCategory; }
    public void setTicketsByCategory(Map<String, Long> ticketsByCategory) { this.ticketsByCategory = ticketsByCategory; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public double getAverageResolutionTimeHours() { return averageResolutionTimeHours; }
    public void setAverageResolutionTimeHours(double averageResolutionTimeHours) { this.averageResolutionTimeHours = averageResolutionTimeHours; }
}
