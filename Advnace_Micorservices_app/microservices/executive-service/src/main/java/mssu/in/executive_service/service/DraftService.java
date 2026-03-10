package mssu.in.executive_service.service;

import mssu.in.executive_service.model.Draft;
import mssu.in.executive_service.repository.DraftRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DraftService {

    private final DraftRepository draftRepository;

    public DraftService(DraftRepository draftRepository) {
        this.draftRepository = draftRepository;
    }

    public Draft saveDraft(Long ticketId, Long executiveId, String content) {
        Draft draft = new Draft(ticketId, executiveId, content);
        return draftRepository.save(draft);
    }

    public List<Draft> getDraftsForTicket(Long ticketId) {
        return draftRepository.findByTicketId(ticketId);
    }

    public List<Draft> getDraftsForExecutiveAndTicket(Long executiveId, Long ticketId) {
        return draftRepository.findByExecutiveIdAndTicketId(executiveId, ticketId);
    }

    public void deleteDraft(Long id) {
        draftRepository.deleteById(id);
    }
}
