package mssu.in.ticket_service.service;

import mssu.in.ticket_service.dto.CreateTicketRequest;
import mssu.in.ticket_service.dto.ExecutiveWorkloadResponse;
import mssu.in.ticket_service.dto.TicketResponse;
import mssu.in.ticket_service.entity.Ticket;
import mssu.in.ticket_service.entity.TicketStatus;
import mssu.in.ticket_service.entity.Priority;
import mssu.in.ticket_service.exception.InvalidStatusTransitionException;
import mssu.in.ticket_service.exception.TicketNotFoundException;
import mssu.in.ticket_service.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    public TicketService(TicketRepository ticketRepository, RestTemplate restTemplate) {
        this.ticketRepository = ticketRepository;
        this.restTemplate = restTemplate;
    }

    public Ticket createTicket(CreateTicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setCustomerId(request.getCustomerId());
        ticket.setProductId(request.getProductId());
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        ticket.setCategory(request.getCategory());
        ticket.setStatus(TicketStatus.PENDING);

        Ticket savedTicket = ticketRepository.save(ticket);

        // Send initial description to customer-service messages
        try {
            Map<String, Object> messageRequest = Map.of(
                    "ticketId", savedTicket.getId(),
                    "senderId", savedTicket.getCustomerId(),
                    "senderType", "CUSTOMER",
                    "content", savedTicket.getDescription());
            restTemplate.postForObject(customerServiceUrl + "/api/customer/messages", messageRequest, Map.class);
        } catch (Exception e) {
            // Log but don't fail ticket creation if message fails
            System.err.println("Failed to send initial message: " + e.getMessage());
        }

        // Removed automatic assignment on creation as per user request
        // autoAssignTicket(savedTicket);

        return savedTicket;
    }

    private void autoAssignTicket(Ticket ticket) {
        try {
            // Get executives from user service
            List<Map<String, Object>> executives = getExecutives();

            if (executives.isEmpty()) {
                return; // No executives available
            }

            // Find executive with minimum active tickets
            Long bestExecutiveId = null;
            long minTickets = Long.MAX_VALUE;

            for (Map<String, Object> exec : executives) {
                Long execId = ((Number) exec.get("id")).longValue();
                long activeCount = ticketRepository.countActiveTicketsByExecutiveId(execId);

                if (activeCount < minTickets) {
                    minTickets = activeCount;
                    bestExecutiveId = execId;
                }
            }

            if (bestExecutiveId != null) {
                ticket.setExecutiveId(bestExecutiveId);
                ticket.setStatus(TicketStatus.OPEN);
            }
        } catch (Exception e) {
            // If user service is not available, leave ticket as PENDING
            System.err.println("Could not auto-assign ticket: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getExecutives() {
        try {
            String url = userServiceUrl + "/api/users/role/EXECUTIVE";
            List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
            return response != null ? response : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error fetching executives: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Ticket> findAll() {
        return ticketRepository.findAllOrderByPriorityAndCreatedAt();
    }

    @Transactional(readOnly = true)
    public List<Ticket> findByCustomerId(Long customerId) {
        return ticketRepository.findByCustomerIdAndDeleted(customerId, false);
    }

    @Transactional(readOnly = true)
    public List<Ticket> findByExecutiveId(Long executiveId) {
        return ticketRepository.findByExecutiveIdAndDeleted(executiveId, false);
    }

    @Transactional(readOnly = true)
    public List<Ticket> findActiveByExecutiveId(Long executiveId) {
        return ticketRepository.findActiveTicketsByExecutiveId(executiveId);
    }

    @Transactional(readOnly = true)
    public List<Ticket> findByStatus(TicketStatus status) {
        return ticketRepository.findByStatusAndDeleted(status, false);
    }

    public Ticket updateStatus(Long ticketId, TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        validateStatusTransition(ticket.getStatus(), newStatus);

        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        } else if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        return ticketRepository.save(ticket);
    }

    private void validateStatusTransition(TicketStatus from, TicketStatus to) {
        // Define valid transitions
        boolean valid = switch (from) {
            case PENDING -> to == TicketStatus.OPEN || to == TicketStatus.CLOSED;
            case OPEN -> to == TicketStatus.IN_PROGRESS || to == TicketStatus.CLOSED || to == TicketStatus.RESOLVED;
            case IN_PROGRESS -> to == TicketStatus.RESOLVED || to == TicketStatus.OPEN;
            case RESOLVED -> to == TicketStatus.CLOSED || to == TicketStatus.REOPENED;
            case CLOSED -> to == TicketStatus.REOPENED;
            case REOPENED -> to == TicketStatus.IN_PROGRESS || to == TicketStatus.CLOSED;
        };

        if (!valid) {
            throw new InvalidStatusTransitionException(from.name(), to.name());
        }
    }

    public Ticket assignToExecutive(Long ticketId, Long executiveId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        ticket.setExecutiveId(executiveId);
        if (ticket.getStatus() == TicketStatus.PENDING) {
            ticket.setStatus(TicketStatus.OPEN);
        }

        return ticketRepository.save(ticket);
    }

    public Ticket addRating(Long ticketId, Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getStatus() != TicketStatus.RESOLVED && ticket.getStatus() != TicketStatus.CLOSED) {
            throw new InvalidStatusTransitionException("Can only rate resolved or closed tickets");
        }

        ticket.setRating(rating);
        return ticketRepository.save(ticket);
    }

    public Ticket addResolutionNotes(Long ticketId, String notes) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        ticket.setResolutionNotes(notes);
        return ticketRepository.save(ticket);
    }

    public Ticket resolveTicket(Long ticketId, String resolutionNotes) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (resolutionNotes != null && !resolutionNotes.isBlank()) {
            ticket.setResolutionNotes(resolutionNotes);
        }

        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolvedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<ExecutiveWorkloadResponse> getExecutiveWorkloads() {
        List<Object[]> workloads = ticketRepository.findExecutiveWorkloads();

        return workloads.stream()
                .map(row -> {
                    Long execId = ((Number) row[0]).longValue();
                    long activeCount = ((Number) row[1]).longValue();

                    ExecutiveWorkloadResponse response = new ExecutiveWorkloadResponse(execId, activeCount);
                    response.setResolvedTicketCount(ticketRepository.countResolvedTicketsByExecutiveId(execId));

                    Double avgRating = ticketRepository.findAverageRatingByExecutiveId(execId);
                    response.setAverageRating(avgRating != null ? avgRating : 0.0);

                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getTicketStats() {
        return Map.of(
                "pending", ticketRepository.countByStatus(TicketStatus.PENDING),
                "open", ticketRepository.countByStatus(TicketStatus.OPEN),
                "inProgress", ticketRepository.countByStatus(TicketStatus.IN_PROGRESS),
                "resolved", ticketRepository.countByStatus(TicketStatus.RESOLVED),
                "closed", ticketRepository.countByStatus(TicketStatus.CLOSED),
                "total", ticketRepository.countAllActive());
    }

    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        ticket.setDeleted(true);
        ticketRepository.save(ticket);
    }

    public TicketResponse toTicketResponse(Ticket ticket) {
        return new TicketResponse(ticket);
    }
}
