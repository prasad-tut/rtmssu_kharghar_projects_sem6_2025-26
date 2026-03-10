package com.conquest.ticket_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.OPEN;
    
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;
    
    @Column(nullable = false)
    private Long userId;
    
    private Long assignedTo;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum TicketStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }
    
    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}