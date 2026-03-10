package mssu.in.executive_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "drafts")
public class Draft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ticketId;
    private Long executiveId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    public Draft() {
    }

    public Draft(Long ticketId, Long executiveId, String content) {
        this.ticketId = ticketId;
        this.executiveId = executiveId;
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getExecutiveId() {
        return executiveId;
    }

    public void setExecutiveId(Long executiveId) {
        this.executiveId = executiveId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
