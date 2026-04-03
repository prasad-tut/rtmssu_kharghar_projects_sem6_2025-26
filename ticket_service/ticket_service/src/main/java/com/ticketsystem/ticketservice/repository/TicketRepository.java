package com.ticketsystem.ticketservice.repository;

import com.ticketsystem.ticketservice.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
