package com.conquest.ticket_service.dto;

import com.conquest.ticket_service.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private Long id;
    private String title;
    private String description;
    private Ticket.TicketStatus status;
    private Ticket.Priority priority;
    private Long userId;
    private Long assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}