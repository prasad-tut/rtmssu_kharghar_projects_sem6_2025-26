package mssu.in.admin_service.controller;

import mssu.in.admin_service.dto.ExecutiveWorkload;
import mssu.in.admin_service.dto.TicketResponse;
import mssu.in.admin_service.service.ExecutiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/executives")
@CrossOrigin(origins = "*")
public class ExecutiveManagementController {

    private static final Logger logger = LoggerFactory.getLogger(ExecutiveManagementController.class);

    private final ExecutiveService executiveService;

    public ExecutiveManagementController(ExecutiveService executiveService) {
        this.executiveService = executiveService;
    }

    @GetMapping("/workload")
    public ResponseEntity<List<ExecutiveWorkload>> getExecutiveWorkload() {
        logger.info("Getting executive workload statistics");
        return ResponseEntity.ok(executiveService.getExecutiveWorkload());
    }

    @PostMapping("/{ticketId}/auto-assign")
    public ResponseEntity<TicketResponse> autoAssignTicket(@PathVariable Long ticketId) {
        logger.info("Auto-assigning ticket: {}", ticketId);
        return ResponseEntity.ok(executiveService.autoAssignTicket(ticketId));
    }
}
