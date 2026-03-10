package mssu.in.ticket_service.entity;

public enum TicketStatus {
    PENDING,      // Just created, not yet assigned
    OPEN,         // Assigned to executive
    IN_PROGRESS,  // Executive working on it
    RESOLVED,     // Executive resolved, waiting customer confirmation
    CLOSED,       // Customer confirmed resolution
    REOPENED      // Customer reopened after resolution
}
