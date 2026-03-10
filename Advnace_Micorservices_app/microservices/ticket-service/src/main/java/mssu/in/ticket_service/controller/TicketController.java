package mssu.in.ticket_service.controller;

import mssu.in.ticket_service.dto.CreateTicketRequest;
import mssu.in.ticket_service.dto.ExecutiveWorkloadResponse;
import mssu.in.ticket_service.dto.TicketResponse;
import mssu.in.ticket_service.entity.Ticket;
import mssu.in.ticket_service.entity.TicketStatus;
import mssu.in.ticket_service.exception.TicketNotFoundException;
import mssu.in.ticket_service.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        Ticket ticket = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TicketResponse(ticket));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        List<TicketResponse> tickets = ticketService.findAll().stream()
                .map(TicketResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        Ticket ticket = ticketService.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        return ResponseEntity.ok(new TicketResponse(ticket));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TicketResponse>> getTicketsByCustomer(@PathVariable Long customerId) {
        List<TicketResponse> tickets = ticketService.findByCustomerId(customerId).stream()
                .map(TicketResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/executive/{executiveId}")
    public ResponseEntity<List<TicketResponse>> getTicketsByExecutive(@PathVariable Long executiveId) {
        List<TicketResponse> tickets = ticketService.findByExecutiveId(executiveId).stream()
                .map(TicketResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/executive/{executiveId}/active")
    public ResponseEntity<List<TicketResponse>> getActiveTicketsByExecutive(@PathVariable Long executiveId) {
        List<TicketResponse> tickets = ticketService.findActiveByExecutiveId(executiveId).stream()
                .map(TicketResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketResponse>> getTicketsByStatus(@PathVariable TicketStatus status) {
        List<TicketResponse> tickets = ticketService.findByStatus(status).stream()
                .map(TicketResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        TicketStatus newStatus = TicketStatus.valueOf(request.get("status"));
        Ticket ticket = ticketService.updateStatus(id, newStatus);
        return ResponseEntity.ok(new TicketResponse(ticket));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        Long executiveId = request.get("executiveId");
        Ticket ticket = ticketService.assignToExecutive(id, executiveId);
        return ResponseEntity.ok(new TicketResponse(ticket));
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<TicketResponse> rateTicket(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer rating = request.get("rating");
        Ticket ticket = ticketService.addRating(id, rating);
        return ResponseEntity.ok(new TicketResponse(ticket));
    }

    @PostMapping("/{id}/resolution")
    public ResponseEntity<TicketResponse> addResolutionNotes(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String notes = request.get("notes");
        Ticket ticket = ticketService.addResolutionNotes(id, notes);
        return ResponseEntity.ok(new TicketResponse(ticket));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<TicketResponse> resolveTicket(
            @PathVariable Long id,
            @RequestBody(required = false) String resolutionNotes) {
        Ticket ticket = ticketService.resolveTicket(id, resolutionNotes);
        return ResponseEntity.ok(new TicketResponse(ticket));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(Map.of("message", "Ticket deleted successfully"));
    }

    @GetMapping("/workloads")
    public ResponseEntity<List<ExecutiveWorkloadResponse>> getExecutiveWorkloads() {
        return ResponseEntity.ok(ticketService.getExecutiveWorkloads());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getTicketStats() {
        return ResponseEntity.ok(ticketService.getTicketStats());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "ticket-service"));
    }
}
