package mssu.in.ticket_service.repository;

import mssu.in.ticket_service.entity.Ticket;
import mssu.in.ticket_service.entity.TicketStatus;
import mssu.in.ticket_service.entity.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

       List<Ticket> findByCustomerId(Long customerId);

       List<Ticket> findByExecutiveId(Long executiveId);

       List<Ticket> findByStatus(TicketStatus status);

       List<Ticket> findByPriority(Priority priority);

       List<Ticket> findByCustomerIdAndStatus(Long customerId, TicketStatus status);

       List<Ticket> findByExecutiveIdAndStatus(Long executiveId, TicketStatus status);

       @Query("SELECT t FROM Ticket t WHERE t.executiveId = :executiveId AND t.deleted = false AND t.status NOT IN ('CLOSED', 'RESOLVED')")
       List<Ticket> findActiveTicketsByExecutiveId(@Param("executiveId") Long executiveId);

       @Query("SELECT COUNT(t) FROM Ticket t WHERE t.executiveId = :executiveId AND t.deleted = false AND t.status NOT IN ('CLOSED', 'RESOLVED')")
       long countActiveTicketsByExecutiveId(@Param("executiveId") Long executiveId);

       @Query("SELECT t.executiveId, COUNT(t) as ticketCount FROM Ticket t " +
                     "WHERE t.status NOT IN ('CLOSED', 'RESOLVED') AND t.executiveId IS NOT NULL " +
                     "GROUP BY t.executiveId ORDER BY ticketCount ASC")
       List<Object[]> findExecutiveWorkloads();

       @Query("SELECT AVG(t.rating) FROM Ticket t WHERE t.executiveId = :executiveId AND t.rating IS NOT NULL")
       Double findAverageRatingByExecutiveId(@Param("executiveId") Long executiveId);

       @Query("SELECT COUNT(t) FROM Ticket t WHERE t.executiveId = :executiveId AND t.status IN ('RESOLVED', 'CLOSED')")
       long countResolvedTicketsByExecutiveId(@Param("executiveId") Long executiveId);

       @Query("SELECT t FROM Ticket t WHERE t.deleted = false ORDER BY " +
                     "CASE t.priority WHEN 'CRITICAL' THEN 1 WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 3 WHEN 'LOW' THEN 4 END, "
                     +
                     "t.createdAt ASC")
       List<Ticket> findAllOrderByPriorityAndCreatedAt();

       @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status AND t.deleted = false")
       long countByStatus(@Param("status") TicketStatus status);

       @Query("SELECT COUNT(t) FROM Ticket t WHERE t.deleted = false")
       long countAllActive();

       List<Ticket> findByCustomerIdAndDeleted(Long customerId, boolean deleted);

       List<Ticket> findByExecutiveIdAndDeleted(Long executiveId, boolean deleted);

       List<Ticket> findByStatusAndDeleted(TicketStatus status, boolean deleted);
}
