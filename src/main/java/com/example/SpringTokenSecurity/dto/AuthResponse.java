package com.example.SpringTokenSecurity.dto;

import com.example.SpringTokenSecurity.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)// Exclude null fields from JSON
public class AuthResponse {
    private String token;
    private long expirationTime; // in milliseconds
    private User user;
}
