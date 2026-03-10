package mssu.in.customer_service.controller;

import mssu.in.customer_service.dto.CreateTicketRequest;
import mssu.in.customer_service.service.CustomerTicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customer/tickets")
@CrossOrigin(origins = "*")
public class CustomerTicketController {

    private final CustomerTicketService customerTicketService;

    public CustomerTicketController(CustomerTicketService customerTicketService) {
        this.customerTicketService = customerTicketService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> raiseTicket(@Valid @RequestBody CreateTicketRequest request) {
        Map<String, Object> ticket = customerTicketService.raiseTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @GetMapping("/my/{customerId}")
    public ResponseEntity<Object[]> getMyTickets(@PathVariable Long customerId) {
        Object[] tickets = customerTicketService.getMyTickets(customerId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Map<String, Object>> getTicketDetails(@PathVariable Long ticketId) {
        Map<String, Object> ticket = customerTicketService.getTicketDetails(ticketId);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{ticketId}/close")
    public ResponseEntity<Map<String, String>> closeTicket(@PathVariable Long ticketId) {
        customerTicketService.closeTicket(ticketId);
        return ResponseEntity.ok(Map.of("message", "Ticket closed successfully"));
    }

    @PostMapping("/{ticketId}/reopen")
    public ResponseEntity<Map<String, String>> reopenTicket(@PathVariable Long ticketId) {
        customerTicketService.reopenTicket(ticketId);
        return ResponseEntity.ok(Map.of("message", "Ticket reopened successfully"));
    }

    @PostMapping("/{ticketId}/rate")
    public ResponseEntity<Map<String, String>> rateTicket(
            @PathVariable Long ticketId,
            @RequestBody Map<String, Integer> request) {
        Integer rating = request.get("rating");
        customerTicketService.rateTicket(ticketId, rating);
        return ResponseEntity.ok(Map.of("message", "Ticket rated successfully"));
    }
}
