package com.example.SpringTokenSecurity.dto;

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
public class ErrorResponse {
    private String message; // Error message
    private int status;     // HTTP status code
}
