package com.ticketsystem.adminservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ticket.service.url}")
    private String ticketServiceUrl;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        // In a real system, this would call Auth service's list of users
        return ResponseEntity.ok("Call to Auth service to get all users");
    }

    @GetMapping("/tickets/all")
    public ResponseEntity<?> getAllTickets() {
        return restTemplate.getForEntity(ticketServiceUrl, List.class);
    }

    @GetMapping("/analytics/summary")
    public ResponseEntity<?> getAnalyticsSummary() {
        Map<String, Object> summary = Map.of(
            "totalTickets", 150,
            "resolvedTickets", 120,
            "openTickets", 20,
            "customerSatisfaction", "4.5/5"
        );
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/products/catalog")
    public ResponseEntity<?> getProductCatalog() {
        // Admin gets the same catalog (or more details)
        return ResponseEntity.ok("Product Catalog Management");
    }
}
