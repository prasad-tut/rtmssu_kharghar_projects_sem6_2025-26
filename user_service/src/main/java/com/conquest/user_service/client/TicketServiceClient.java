package com.conquest.user_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.conquest.user_service.entity.TicketDTO;

@FeignClient(name = "ticket-service")
public interface TicketServiceClient {
    
    @GetMapping("/tickets/users/{userId}")
    List<TicketDTO> getTicketsRaisedByUser(@PathVariable("userId") Integer userId);
    
}
