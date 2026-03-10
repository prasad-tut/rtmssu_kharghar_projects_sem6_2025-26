package com.conquest.ticket_service.service;

import com.conquest.ticket_service.dto.TicketDto;
import com.conquest.ticket_service.entity.Ticket;
import com.conquest.ticket_service.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final TicketRepository ticketRepository;
    
    public List<TicketDto> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<TicketDto> getTicketById(Long id) {
        return ticketRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public List<TicketDto> getTicketsByUserId(Long userId) {
        return ticketRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<TicketDto> getTicketsByAssignedTo(Long assignedTo) {
        return ticketRepository.findByAssignedTo(assignedTo).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<TicketDto> getTicketsByStatus(Ticket.TicketStatus status) {
        return ticketRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public TicketDto createTicket(TicketDto ticketDto) {
        Ticket ticket = convertToEntity(ticketDto);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket savedTicket = ticketRepository.save(ticket);
        return convertToDto(savedTicket);
    }
    
    public Optional<TicketDto> updateTicket(Long id, TicketDto ticketDto) {
        return ticketRepository.findById(id)
                .map(existingTicket -> {
                    existingTicket.setTitle(ticketDto.getTitle());
                    existingTicket.setDescription(ticketDto.getDescription());
                    existingTicket.setStatus(ticketDto.getStatus());
                    existingTicket.setPriority(ticketDto.getPriority());
                    existingTicket.setAssignedTo(ticketDto.getAssignedTo());
                    existingTicket.setUpdatedAt(LocalDateTime.now());
                    return convertToDto(ticketRepository.save(existingTicket));
                });
    }
    
    public boolean deleteTicket(Long id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private TicketDto convertToDto(Ticket ticket) {
        return new TicketDto(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getUserId(),
                ticket.getAssignedTo(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
    
    private Ticket convertToEntity(TicketDto ticketDto) {
        return new Ticket(
                ticketDto.getId(),
                ticketDto.getTitle(),
                ticketDto.getDescription(),
                ticketDto.getStatus(),
                ticketDto.getPriority(),
                ticketDto.getUserId(),
                ticketDto.getAssignedTo(),
                ticketDto.getCreatedAt(),
                ticketDto.getUpdatedAt()
        );
    }
}