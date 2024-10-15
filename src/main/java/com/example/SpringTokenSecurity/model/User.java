package com.example.SpringTokenSecurity.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String role;

}
