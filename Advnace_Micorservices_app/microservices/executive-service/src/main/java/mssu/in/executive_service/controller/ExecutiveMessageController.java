package mssu.in.executive_service.controller;

import mssu.in.executive_service.dto.MessageResponse;
import mssu.in.executive_service.dto.SendMessageRequest;
import mssu.in.executive_service.service.ExecutiveMessageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/executive")
@CrossOrigin(origins = "*")
public class ExecutiveMessageController {

    private static final Logger logger = LoggerFactory.getLogger(ExecutiveMessageController.class);

    private final ExecutiveMessageService executiveMessageService;

    public ExecutiveMessageController(ExecutiveMessageService executiveMessageService) {
        this.executiveMessageService = executiveMessageService;
    }

    @GetMapping("/tickets/{ticketId}/messages")
    public ResponseEntity<List<MessageResponse>> getTicketMessages(@PathVariable Long ticketId) {
        logger.info("Getting messages for ticket: {}", ticketId);
        return ResponseEntity.ok(executiveMessageService.getMessagesForTicket(ticketId));
    }

    @PostMapping("/tickets/{ticketId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long ticketId,
            @Valid @RequestBody SendMessageRequest request) {
        logger.info("Executive {} sending message to ticket: {}", request.getSenderId(), ticketId);
        return ResponseEntity.ok(executiveMessageService.sendMessageToCustomer(
                ticketId, request.getSenderId(), request.getContent(), request.getMessageType()));
    }

    @GetMapping("/tickets/{ticketId}/messages/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount(@PathVariable Long ticketId) {
        logger.info("Getting unread message count for ticket: {}", ticketId);
        long count = executiveMessageService.getUnreadMessageCount(ticketId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PostMapping("/tickets/{ticketId}/messages/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long ticketId,
            @RequestParam Long executiveId) {
        logger.info("Marking messages as read for ticket {} by executive {}", ticketId, executiveId);
        executiveMessageService.markMessagesAsRead(ticketId, executiveId);
        return ResponseEntity.ok().build();
    }
}
