package com.ticketsystem.customerservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ticket.service.url}")
    private String ticketServiceUrl;

    @GetMapping("/tickets/my/{customerId}")
    public ResponseEntity<?> getMyTickets(@PathVariable Long customerId) {
        String url = ticketServiceUrl + "/customer/" + customerId;
        return restTemplate.getForEntity(url, List.class);
    }

    @PostMapping("/tickets")
    public ResponseEntity<?> createTicket(@RequestBody Map<String, Object> ticketRequest) {
        return restTemplate.postForEntity(ticketServiceUrl, ticketRequest, Map.class);
    }

    @GetMapping("/products/catalog")
    public ResponseEntity<?> getProductCatalog() {
        // Return a mock product catalog
        List<Map<String, String>> products = List.of(
            Map.of("id", "1", "name", "Laptop Pro", "description", "High-performance laptop"),
            Map.of("id", "2", "name", "Phone X", "description", "Latest smartphone"),
            Map.of("id", "3", "name", "Headphones", "description", "Noise-cancelling headphones")
        );
        return ResponseEntity.ok(products);
    }
}
