package mssu.in.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authFallback() {
        return createFallbackResponse("Auth Service", "Authentication service is currently unavailable");
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> usersFallback() {
        return createFallbackResponse("User Service", "User service is currently unavailable");
    }

    @GetMapping("/tickets")
    public ResponseEntity<Map<String, Object>> ticketsFallback() {
        return createFallbackResponse("Ticket Service", "Ticket service is currently unavailable");
    }

    @GetMapping("/customer")
    public ResponseEntity<Map<String, Object>> customerFallback() {
        return createFallbackResponse("Customer Service", "Customer service is currently unavailable");
    }

    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> messagesFallback() {
        return createFallbackResponse("Messages Service", "Messages service is currently unavailable");
    }

    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> adminFallback() {
        return createFallbackResponse("Admin Service", "Admin service is currently unavailable");
    }

    @GetMapping("/executive")
    public ResponseEntity<Map<String, Object>> executiveFallback() {
        return createFallbackResponse("Executive Service", "Executive service is currently unavailable");
    }

    private ResponseEntity<Map<String, Object>> createFallbackResponse(String service, String message) {
        Map<String, Object> response = Map.of(
                "status", "SERVICE_UNAVAILABLE",
                "service", service,
                "message", message,
                "timestamp", LocalDateTime.now().toString(),
                "suggestion", "Please try again later or contact support"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
