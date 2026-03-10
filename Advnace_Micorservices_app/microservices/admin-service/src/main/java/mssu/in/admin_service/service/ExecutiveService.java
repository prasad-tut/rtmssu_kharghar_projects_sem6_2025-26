package mssu.in.admin_service.service;

import mssu.in.admin_service.client.TicketServiceClient;
import mssu.in.admin_service.client.UserServiceClient;
import mssu.in.admin_service.dto.ExecutiveWorkload;
import mssu.in.admin_service.dto.TicketResponse;
import mssu.in.admin_service.dto.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExecutiveService {

    private final UserServiceClient userServiceClient;
    private final TicketServiceClient ticketServiceClient;

    public ExecutiveService(UserServiceClient userServiceClient, TicketServiceClient ticketServiceClient) {
        this.userServiceClient = userServiceClient;
        this.ticketServiceClient = ticketServiceClient;
    }

    public List<ExecutiveWorkload> getExecutiveWorkload() {
        List<UserResponse> executives = userServiceClient.getUsersByRole("EXECUTIVE");
        List<TicketResponse> allTickets = ticketServiceClient.getAllTickets();

        return executives.stream().map(exec -> {
            ExecutiveWorkload workload = new ExecutiveWorkload(exec.getId(), exec.getName(), exec.getEmail());
            workload.setOnline(exec.isOnline());

            List<TicketResponse> execTickets = allTickets.stream()
                    .filter(t -> exec.getId().equals(t.getExecutiveId()))
                    .collect(Collectors.toList());

            workload.setTotalAssigned(execTickets.size());
            workload.setOpenTickets(execTickets.stream()
                    .filter(t -> "OPEN".equals(t.getStatus()) || "ASSIGNED".equals(t.getStatus())).count());
            workload.setInProgressTickets(
                    execTickets.stream().filter(t -> "IN_PROGRESS".equals(t.getStatus())).count());
            workload.setResolvedTickets(execTickets.stream().filter(t -> "RESOLVED".equals(t.getStatus())).count());

            return workload;
        }).collect(Collectors.toList());
    }

    public TicketResponse autoAssignTicket(Long ticketId) {
        List<ExecutiveWorkload> workloads = getExecutiveWorkload();
        if (workloads.isEmpty()) {
            throw new RuntimeException("No executives available for assignment");
        }

        // Sorting by:
        // 1. Total Assigned (ascending)
        // 2. Executive ID (ascending, as requested for tie-break)
        ExecutiveWorkload bestExecutive = workloads.stream()
                .min((w1, w2) -> {
                    int cmp = Long.compare(w1.getTotalAssigned(), w2.getTotalAssigned());
                    if (cmp != 0)
                        return cmp;
                    return Long.compare(w1.getExecutiveId(), w2.getExecutiveId());
                })
                .orElseThrow(() -> new RuntimeException("Could not determine best executive"));

        return ticketServiceClient.assignTicketToExecutive(ticketId, bestExecutive.getExecutiveId());
    }
}
