package mssu.in.admin_service.controller;

import mssu.in.admin_service.dto.AssignTicketRequest;
import mssu.in.admin_service.dto.TicketResponse;
import mssu.in.admin_service.service.TicketManagementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tickets")
@CrossOrigin(origins = "*")
public class TicketManagementController {

    private static final Logger logger = LoggerFactory.getLogger(TicketManagementController.class);

    private final TicketManagementService ticketManagementService;

    public TicketManagementController(TicketManagementService ticketManagementService) {
        this.ticketManagementService = ticketManagementService;
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        logger.info("Getting all tickets");
        return ResponseEntity.ok(ticketManagementService.getAllTickets());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getTicketStats() {
        logger.info("Getting ticket statistics");
        return ResponseEntity.ok(ticketManagementService.getTicketStats());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketResponse>> getTicketsByStatus(@PathVariable String status) {
        logger.info("Getting tickets by status: {}", status);
        return ResponseEntity.ok(ticketManagementService.getTicketsByStatus(status.toUpperCase()));
    }

    @GetMapping("/executive/{executiveId}")
    public ResponseEntity<List<TicketResponse>> getTicketsByExecutive(@PathVariable Long executiveId) {
        logger.info("Getting tickets for executive: {}", executiveId);
        return ResponseEntity.ok(ticketManagementService.getTicketsByExecutive(executiveId));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<TicketResponse>> getUnassignedTickets() {
        logger.info("Getting unassigned tickets");
        return ResponseEntity.ok(ticketManagementService.getUnassignedTickets());
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long ticketId) {
        logger.info("Getting ticket by ID: {}", ticketId);
        TicketResponse ticket = ticketManagementService.getTicketById(ticketId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{ticketId}/assign")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody AssignTicketRequest request) {
        logger.info("Assigning ticket {} to executive {}", ticketId, request.getExecutiveId());
        TicketResponse ticket = ticketManagementService.assignTicketToExecutive(ticketId, request.getExecutiveId());
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam String status) {
        logger.info("Updating ticket {} status to {}", ticketId, status);
        TicketResponse ticket = ticketManagementService.updateTicketStatus(ticketId, status.toUpperCase());
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/{ticketId}/close")
    public ResponseEntity<TicketResponse> closeTicket(@PathVariable Long ticketId) {
        logger.info("Closing ticket {}", ticketId);
        TicketResponse ticket = ticketManagementService.closeTicket(ticketId);
        return ResponseEntity.ok(ticket);
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId) {
        logger.info("Deleting ticket {}", ticketId);
        ticketManagementService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}
