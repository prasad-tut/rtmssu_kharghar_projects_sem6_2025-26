package com.ticketsystem.customerservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Customer Service (Ticket System)";
    }
}
