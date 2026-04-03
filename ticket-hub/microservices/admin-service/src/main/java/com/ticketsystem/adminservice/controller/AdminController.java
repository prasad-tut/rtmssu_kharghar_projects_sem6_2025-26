package com.ticketsystem.adminservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/stats")
    public String getStats() {
        return "Admin Statistics for Ticket System";
    }
}
