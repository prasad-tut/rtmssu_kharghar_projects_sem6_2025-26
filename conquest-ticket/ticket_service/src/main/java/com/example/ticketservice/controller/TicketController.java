package com.example.ticketservice.controller;

import com.example.ticketservice.model.Ticket;
import com.example.ticketservice.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @GetMapping("/{id}")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Ticket createTicket(@RequestBody Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @PutMapping("/{id}")
    public Ticket updateTicket(@PathVariable Long id, @RequestBody Ticket ticketDetails) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket != null) {
            ticket.setIssue(ticketDetails.getIssue());
            ticket.setRaisedBy(ticketDetails.getRaisedBy());
            ticket.setAssignedTo(ticketDetails.getAssignedTo());
            ticket.setStatus(ticketDetails.getStatus());
            ticket.setAssignedOn(ticketDetails.getAssignedOn());
            return ticketRepository.save(ticket);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable Long id) {
        ticketRepository.deleteById(id);
    }

    @PatchMapping("/{id}")
    public Ticket closeTicket(@PathVariable Long id) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket != null) {
            ticket.setStatus("CLOSED");
            return ticketRepository.save(ticket);
        }
        return null;
    }

    @GetMapping("/user/{userId}")
    public List<Ticket> getTicketsByUserId(@PathVariable Long userId) {
        return ticketRepository.findByRaisedBy(userId);
    }

    @GetMapping("/userdto/{id}")
    public Map<String, Object> getTicketWithUser(@PathVariable Long id) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null) return null;

        // Inter-service call to User Service
        String url = "http://user-micro-service/users/by-id/" + ticket.getRaisedBy();
        Object user = restTemplate.getForObject(url, Object.class);

        Map<String, Object> response = new HashMap<>();
        response.put("ticket", ticket);
        response.put("user", user);
        return response;
    }
}
