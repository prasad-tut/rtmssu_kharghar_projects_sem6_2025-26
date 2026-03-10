package com.conquest.ticket_service.repository;

import com.conquest.ticket_service.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByAssignedTo(Long assignedTo);
    List<Ticket> findByStatus(Ticket.TicketStatus status);
    List<Ticket> findByPriority(Ticket.Priority priority);
}