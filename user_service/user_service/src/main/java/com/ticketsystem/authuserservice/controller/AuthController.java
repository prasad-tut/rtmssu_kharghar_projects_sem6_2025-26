package com.ticketsystem.authuserservice.controller;

import com.ticketsystem.authuserservice.entity.User;
import com.ticketsystem.authuserservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return userRepository.findByUsername(user.getUsername())
                .filter(u -> u.getPassword().equals(user.getPassword()))
                .map(u -> "Login Successful for " + u.getRole())
                .orElse("Invalid Credentials");
    }
}
