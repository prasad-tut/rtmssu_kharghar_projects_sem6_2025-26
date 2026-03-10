package mssu.in.ticket_service.dto;

public class ExecutiveWorkloadResponse {
    
    private Long executiveId;
    private String executiveName;
    private long activeTicketCount;
    private long resolvedTicketCount;
    private double averageRating;

    public ExecutiveWorkloadResponse() {}

    public ExecutiveWorkloadResponse(Long executiveId, long activeTicketCount) {
        this.executiveId = executiveId;
        this.activeTicketCount = activeTicketCount;
    }

    public Long getExecutiveId() { return executiveId; }
    public void setExecutiveId(Long executiveId) { this.executiveId = executiveId; }

    public String getExecutiveName() { return executiveName; }
    public void setExecutiveName(String executiveName) { this.executiveName = executiveName; }

    public long getActiveTicketCount() { return activeTicketCount; }
    public void setActiveTicketCount(long activeTicketCount) { this.activeTicketCount = activeTicketCount; }

    public long getResolvedTicketCount() { return resolvedTicketCount; }
    public void setResolvedTicketCount(long resolvedTicketCount) { this.resolvedTicketCount = resolvedTicketCount; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
}
