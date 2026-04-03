package com.ticketsystem.ticketservice.repository;

import com.ticketsystem.ticketservice.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCustomerId(Long customerId);
    List<Ticket> findByExecutiveId(Long executiveId);
}
