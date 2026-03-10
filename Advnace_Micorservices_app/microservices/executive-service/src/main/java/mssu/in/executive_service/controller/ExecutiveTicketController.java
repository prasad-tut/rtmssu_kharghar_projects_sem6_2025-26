package mssu.in.executive_service.controller;

import mssu.in.executive_service.dto.ExecutiveWorkloadStats;
import mssu.in.executive_service.dto.TicketResponse;
import mssu.in.executive_service.dto.UpdateTicketStatusRequest;
import mssu.in.executive_service.service.ExecutiveTicketService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/executive")
@CrossOrigin(origins = "*")
public class ExecutiveTicketController {

    private static final Logger logger = LoggerFactory.getLogger(ExecutiveTicketController.class);

    private final ExecutiveTicketService executiveTicketService;

    public ExecutiveTicketController(ExecutiveTicketService executiveTicketService) {
        this.executiveTicketService = executiveTicketService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Executive Service is running");
    }

    @GetMapping("/{executiveId}/tickets")
    public ResponseEntity<List<TicketResponse>> getMyTickets(@PathVariable Long executiveId) {
        logger.info("Getting all tickets for executive: {}", executiveId);
        return ResponseEntity.ok(executiveTicketService.getMyTickets(executiveId));
    }

    @GetMapping("/{executiveId}/tickets/open")
    public ResponseEntity<List<TicketResponse>> getMyOpenTickets(@PathVariable Long executiveId) {
        logger.info("Getting open tickets for executive: {}", executiveId);
        return ResponseEntity.ok(executiveTicketService.getMyOpenTickets(executiveId));
    }

    @GetMapping("/{executiveId}/tickets/resolved")
    public ResponseEntity<List<TicketResponse>> getMyResolvedTickets(@PathVariable Long executiveId) {
        logger.info("Getting resolved tickets for executive: {}", executiveId);
        return ResponseEntity.ok(executiveTicketService.getMyResolvedTickets(executiveId));
    }

    @GetMapping("/{executiveId}/workload")
    public ResponseEntity<ExecutiveWorkloadStats> getMyWorkloadStats(@PathVariable Long executiveId) {
        logger.info("Getting workload stats for executive: {}", executiveId);
        return ResponseEntity.ok(executiveTicketService.getMyWorkloadStats(executiveId));
    }

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketDetails(@PathVariable Long ticketId) {
        logger.info("Getting ticket details: {}", ticketId);
        TicketResponse ticket = executiveTicketService.getTicketDetails(ticketId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/tickets/{ticketId}/start")
    public ResponseEntity<TicketResponse> startWorkingOnTicket(@PathVariable Long ticketId) {
        logger.info("Starting work on ticket: {}", ticketId);
        return ResponseEntity.ok(executiveTicketService.startWorkingOnTicket(ticketId));
    }

    @PostMapping("/tickets/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable Long ticketId,
            @Valid @RequestBody UpdateTicketStatusRequest request) {
        logger.info("Updating ticket {} status to: {}", ticketId, request.getStatus());
        return ResponseEntity.ok(executiveTicketService.updateTicketStatus(ticketId, request.getStatus()));
    }

    @PostMapping("/tickets/{ticketId}/resolve")
    public ResponseEntity<TicketResponse> resolveTicket(
            @PathVariable Long ticketId,
            @RequestBody(required = false) String resolutionNotes) {
        logger.info("Resolving ticket: {}", ticketId);
        return ResponseEntity.ok(executiveTicketService.resolveTicket(ticketId, resolutionNotes));
    }
}
