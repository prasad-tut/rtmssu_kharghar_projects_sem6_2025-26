package com.conquest.ticket_service.controller;

import com.conquest.ticket_service.dto.TicketDto;
import com.conquest.ticket_service.entity.Ticket;
import com.conquest.ticket_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;
    
    @GetMapping
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        List<TicketDto> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ticket -> ResponseEntity.ok(ticket))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketDto>> getTicketsByUserId(@PathVariable Long userId) {
        List<TicketDto> tickets = ticketService.getTicketsByUserId(userId);
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/assigned/{assignedTo}")
    public ResponseEntity<List<TicketDto>> getTicketsByAssignedTo(@PathVariable Long assignedTo) {
        List<TicketDto> tickets = ticketService.getTicketsByAssignedTo(assignedTo);
        return ResponseEntity.ok(tickets);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketDto>> getTicketsByStatus(@PathVariable Ticket.TicketStatus status) {
        List<TicketDto> tickets = ticketService.getTicketsByStatus(status);
        return ResponseEntity.ok(tickets);
    }
    
    @PostMapping
    public ResponseEntity<TicketDto> createTicket(@RequestBody TicketDto ticketDto) {
        TicketDto createdTicket = ticketService.createTicket(ticketDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TicketDto> updateTicket(@PathVariable Long id, @RequestBody TicketDto ticketDto) {
        return ticketService.updateTicket(id, ticketDto)
                .map(ticket -> ResponseEntity.ok(ticket))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        if (ticketService.deleteTicket(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}