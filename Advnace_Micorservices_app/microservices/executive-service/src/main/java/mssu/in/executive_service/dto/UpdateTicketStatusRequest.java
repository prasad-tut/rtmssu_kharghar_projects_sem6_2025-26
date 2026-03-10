package mssu.in.executive_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateTicketStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;

    @Size(max = 500, message = "Resolution notes must not exceed 500 characters")
    private String resolutionNotes;

    public UpdateTicketStatusRequest() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
}
