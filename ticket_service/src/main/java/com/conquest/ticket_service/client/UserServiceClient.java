package com.conquest.ticket_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.conquest.ticket_service.entity.UserDTO;

@FeignClient(name = "user-micro-service")
public interface UserServiceClient {
    
    @GetMapping("/users/by-id/{userId}")
    UserDTO getUserById(@PathVariable("userId") Integer userId);
    
}
