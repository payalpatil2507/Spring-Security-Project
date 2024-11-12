package com.example.SpringTokenSecurity.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SwitchUserController {

    // Endpoint to get the current authenticated user's details
    @GetMapping("/current-user")
    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "Current User: " + authentication.getName();
    }

    // Endpoint for switching to a different user
    @PostMapping("/switchUser")
    public String switchUser(@RequestParam("username") String username) {
        return "Attempting to switch to user: " + username;
    }

    // Endpoint for exiting the switched user session
    @PostMapping("/exitSwitchUser")
    public String exitSwitchUser() {
        return "Exiting switched user session";
    }
}
