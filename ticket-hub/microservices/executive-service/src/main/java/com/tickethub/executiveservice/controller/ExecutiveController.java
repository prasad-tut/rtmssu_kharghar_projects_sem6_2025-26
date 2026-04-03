package com.ticketsystem.executiveservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/executive")
public class ExecutiveController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ticket.service.url}")
    private String ticketServiceUrl;

    @GetMapping("/{executiveId}/tickets")
    public ResponseEntity<?> getMyAssignedTickets(@PathVariable Long executiveId) {
        String url = ticketServiceUrl + "/executive/" + executiveId;
        return restTemplate.getForEntity(url, List.class);
    }

    @PostMapping("/tickets/{ticketId}/resolve")
    public ResponseEntity<?> resolveTicket(@PathVariable Long ticketId) {
        String url = ticketServiceUrl + "/" + ticketId + "/status";
        restTemplate.put(url, "RESOLVED");
        return ResponseEntity.ok("Ticket " + ticketId + " marked as RESOLVED");
    }

    @GetMapping("/ai/tickets/{ticketId}/reply-suggestions")
    public ResponseEntity<?> getReplySuggestions(@PathVariable Long ticketId) {
        // Mock AI suggestions
        List<String> suggestions = List.of(
            "Hello, I am looking into your issue. Could you please provide more details?",
            "I have resolved the issue with the system. Please check and let me know.",
            "Thank you for contacting us. We are currently experiencing high volume but will get back to you soon."
        );
        return ResponseEntity.ok(Map.of("ticketId", ticketId, "suggestions", suggestions));
    }
}
