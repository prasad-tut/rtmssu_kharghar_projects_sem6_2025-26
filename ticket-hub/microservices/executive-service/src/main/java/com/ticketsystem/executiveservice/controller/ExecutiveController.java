package com.ticketsystem.executiveservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/executive")
public class ExecutiveController {
    @GetMapping("/tasks")
    public String getTasks() {
        return "Executive Tasks for Ticket System";
    }
}
