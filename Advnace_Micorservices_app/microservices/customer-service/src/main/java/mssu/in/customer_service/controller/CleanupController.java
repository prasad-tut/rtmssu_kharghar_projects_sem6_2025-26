package mssu.in.customer_service.controller;

import mssu.in.customer_service.repository.MessageRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/cleanup")
public class CleanupController {

    private final MessageRepository messageRepository;

    public CleanupController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @DeleteMapping("/messages")
    public String wipeMessages() {
        messageRepository.deleteAll();
        return "All messages wiped from Supabase! 🧹";
    }
}
