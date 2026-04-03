package com.ticketsystem.ticketservice.controller;

import com.ticketsystem.ticketservice.entity.Ticket;
import com.ticketsystem.ticketservice.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    @Autowired
    private TicketRepository ticketRepository;

    @PostMapping
    public Ticket createTicket(@RequestBody Ticket ticket) {
        if (ticket.getStatus() == null) {
            ticket.setStatus("OPEN");
        }
        return ticketRepository.save(ticket);
    }

    @GetMapping("/customer/{customerId}")
    public List<Ticket> getCustomerTickets(@PathVariable String customerId) {
        return ticketRepository.findByCustomerId(customerId);
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}
