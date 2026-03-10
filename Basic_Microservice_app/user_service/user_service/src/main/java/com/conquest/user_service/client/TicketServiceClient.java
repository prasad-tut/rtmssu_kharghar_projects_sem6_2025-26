package com.conquest.user_service.client;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.conquest.user_service.entity.TicketDTO;

@Service
public class TicketServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    public List<TicketDTO> getTicketsByUserId(Integer userId) {
        TicketDTO[] tickets = restTemplate.getForObject("http://ticket-service/tickets/users/" + userId, TicketDTO[].class);
        return tickets != null ? Arrays.asList(tickets) : List.of();
    }
}
