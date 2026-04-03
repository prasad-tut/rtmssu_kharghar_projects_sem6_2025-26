package com.ticketsystem.ticketservice.controller;

import com.ticketsystem.ticketservice.entity.Ticket;
import com.ticketsystem.ticketservice.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketRepository.save(ticket));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long id) {
        return ticketRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Ticket>> getCustomerTickets(@PathVariable Long customerId) {
        return ResponseEntity.ok(ticketRepository.findByCustomerId(customerId));
    }

    @GetMapping("/executive/{executiveId}")
    public ResponseEntity<List<Ticket>> getExecutiveTickets(@PathVariable Long executiveId) {
        return ResponseEntity.ok(ticketRepository.findByExecutiveId(executiveId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Ticket> updateStatus(@PathVariable Long id, @RequestBody Ticket.Status status) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setStatus(status);
                    return ResponseEntity.ok(ticketRepository.save(ticket));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/assign/{executiveId}")
    public ResponseEntity<Ticket> assignTicket(@PathVariable Long id, @PathVariable Long executiveId) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setExecutiveId(executiveId);
                    ticket.setStatus(Ticket.Status.IN_PROGRESS);
                    return ResponseEntity.ok(ticketRepository.save(ticket));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
