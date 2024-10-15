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
public class APIResponse<T> {
    private boolean success; // Indicates whether the operation was successful
    private T data;          // Holds the success data, can be of any type
    private String message;  // Holds the error message if the operation fails
    private int status;      // HTTP status code
}
