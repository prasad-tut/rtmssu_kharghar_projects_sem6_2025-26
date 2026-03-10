package mssu.in.customer_service.repository;

import mssu.in.customer_service.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByTicketIdOrderByCreatedAtAsc(Long ticketId);

    List<Message> findBySenderId(Long senderId);

    @Query("SELECT m FROM Message m WHERE m.ticketId = :ticketId AND m.isRead = false AND m.senderId != :userId")
    List<Message> findUnreadMessages(@Param("ticketId") Long ticketId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.ticketId = :ticketId AND m.senderId != :userId")
    void markMessagesAsRead(@Param("ticketId") Long ticketId, @Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.ticketId = :ticketId AND m.isRead = false AND m.senderId != :userId")
    long countUnreadMessages(@Param("ticketId") Long ticketId, @Param("userId") Long userId);

    void deleteByTicketId(Long ticketId);
}
