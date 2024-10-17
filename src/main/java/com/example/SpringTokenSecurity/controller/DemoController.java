package com.example.SpringTokenSecurity.controller;

import com.example.SpringTokenSecurity.dto.APIResponse;
import com.example.SpringTokenSecurity.dto.CustomUser;
import com.example.SpringTokenSecurity.model.User;
import com.example.SpringTokenSecurity.service.UserDetailsServiceImpl;
import com.example.SpringTokenSecurity.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DemoController {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping("/me")
    public ResponseEntity<APIResponse<CustomUser>> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser currentUser = (CustomUser) authentication.getPrincipal();
        return ResponseUtil.createSuccessResponse(currentUser, HttpStatus.OK);
    }

    @GetMapping("/allUser")
    public ResponseEntity<APIResponse<List<User>>> allUsers() {
        List<User> users = userDetailsService.allUsers();
        return ResponseUtil.createSuccessResponse(users, HttpStatus.OK);
    }
}

