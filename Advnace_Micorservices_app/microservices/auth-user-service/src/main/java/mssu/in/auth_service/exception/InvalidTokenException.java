package mssu.in.auth_service.exception;

public class InvalidTokenException extends RuntimeException {
    
    public InvalidTokenException() {
        super("Invalid or expired token");
    }
    
    public InvalidTokenException(String message) {
        super(message);
    }
}
