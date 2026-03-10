package mssu.in.executive_service.controller;

import mssu.in.executive_service.model.Draft;
import mssu.in.executive_service.service.DraftService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/executive/drafts")
@CrossOrigin(origins = "*")
public class DraftController {

    private final DraftService draftService;

    public DraftController(DraftService draftService) {
        this.draftService = draftService;
    }

    @PostMapping
    public ResponseEntity<Draft> saveDraft(@RequestBody Map<String, Object> payload) {
        Long ticketId = Long.valueOf(payload.get("ticketId").toString());
        Long executiveId = Long.valueOf(payload.get("executiveId").toString());
        String content = payload.get("content").toString();

        return ResponseEntity.ok(draftService.saveDraft(ticketId, executiveId, content));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<Draft>> getDraftsForTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(draftService.getDraftsForTicket(ticketId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDraft(@PathVariable Long id) {
        draftService.deleteDraft(id);
        return ResponseEntity.noContent().build();
    }
}
