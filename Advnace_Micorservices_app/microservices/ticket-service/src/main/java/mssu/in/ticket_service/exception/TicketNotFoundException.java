package mssu.in.ticket_service.exception;

public class TicketNotFoundException extends RuntimeException {
    
    public TicketNotFoundException(String message) {
        super(message);
    }
    
    public TicketNotFoundException(Long id) {
        super("Ticket not found with id: " + id);
    }
}
