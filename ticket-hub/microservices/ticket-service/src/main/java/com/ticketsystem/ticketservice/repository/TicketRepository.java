package com.ticketsystem.ticketservice.repository;

import com.ticketsystem.ticketservice.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCustomerId(String customerId);
    List<Ticket> findByAssignedTo(String assignedTo);
}
