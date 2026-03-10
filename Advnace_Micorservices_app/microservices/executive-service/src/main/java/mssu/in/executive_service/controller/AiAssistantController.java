package mssu.in.executive_service.controller;

import mssu.in.executive_service.dto.AiReplySuggestionsResponse;
import mssu.in.executive_service.service.AiAssistantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/executive/ai")
@CrossOrigin(origins = "*")
public class AiAssistantController {

    private static final Logger logger = LoggerFactory.getLogger(AiAssistantController.class);

    private final AiAssistantService aiAssistantService;

    public AiAssistantController(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @GetMapping("/tickets/{ticketId}/reply-suggestions")
    public ResponseEntity<AiReplySuggestionsResponse> getReplySuggestions(@PathVariable Long ticketId) {
        logger.info("Generating AI reply suggestions for ticket {}", ticketId);
        List<String> suggestions = aiAssistantService.generateReplySuggestions(ticketId);
        return ResponseEntity.ok(new AiReplySuggestionsResponse(suggestions));
    }
}

