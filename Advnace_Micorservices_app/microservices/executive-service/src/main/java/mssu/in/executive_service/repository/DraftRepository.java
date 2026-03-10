package mssu.in.executive_service.repository;

import mssu.in.executive_service.model.Draft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DraftRepository extends JpaRepository<Draft, Long> {
    List<Draft> findByTicketId(Long ticketId);

    List<Draft> findByExecutiveIdAndTicketId(Long executiveId, Long ticketId);
}
