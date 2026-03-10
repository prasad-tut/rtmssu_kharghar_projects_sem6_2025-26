package mssu.in.admin_service.dto;

import jakarta.validation.constraints.NotNull;

public class AssignTicketRequest {
    @NotNull(message = "Executive ID is required")
    private Long executiveId;

    public AssignTicketRequest() {}

    public Long getExecutiveId() { return executiveId; }
    public void setExecutiveId(Long executiveId) { this.executiveId = executiveId; }
}
