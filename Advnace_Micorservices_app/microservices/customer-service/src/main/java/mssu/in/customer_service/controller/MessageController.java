package mssu.in.customer_service.controller;

import mssu.in.customer_service.dto.MessageResponse;
import mssu.in.customer_service.dto.SendMessageRequest;
import mssu.in.customer_service.entity.Message;
import mssu.in.customer_service.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        Message message = messageService.saveMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(message));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<MessageResponse>> getMessagesByTicket(@PathVariable Long ticketId) {
        List<MessageResponse> messages = messageService.getMessagesByTicketId(ticketId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/ticket/{ticketId}/read")
    public ResponseEntity<Map<String, String>> markAsRead(
            @PathVariable Long ticketId,
            @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        messageService.markMessagesAsRead(ticketId, userId);
        return ResponseEntity.ok(Map.of("message", "Messages marked as read"));
    }

    @GetMapping("/ticket/{ticketId}/unread")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @PathVariable Long ticketId,
            @RequestParam Long userId) {
        long count = messageService.getUnreadCount(ticketId, userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "customer-service"));
    }
}
