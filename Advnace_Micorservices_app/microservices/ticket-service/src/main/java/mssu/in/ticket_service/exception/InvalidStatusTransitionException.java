package mssu.in.ticket_service.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
    
    public InvalidStatusTransitionException(String fromStatus, String toStatus) {
        super("Invalid status transition from " + fromStatus + " to " + toStatus);
    }
}
