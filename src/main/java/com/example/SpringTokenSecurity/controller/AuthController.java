package com.example.SpringTokenSecurity.controller;

import com.example.SpringTokenSecurity.constants.APIConstant;
import com.example.SpringTokenSecurity.dto.APIResponse;
import com.example.SpringTokenSecurity.dto.AuthRequest;
import com.example.SpringTokenSecurity.dto.AuthResponse;
import com.example.SpringTokenSecurity.model.User;
import com.example.SpringTokenSecurity.repository.UserRepository;
import com.example.SpringTokenSecurity.utils.JwtUtil;
import com.example.SpringTokenSecurity.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // You can replace this with any password encoder
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<String>> registerUser(@RequestBody User user) {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseUtil.createErrorResponse(APIConstant.USER_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }

        // Set user properties
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        userRepository.save(user);

        return ResponseUtil.createSuccessResponse(APIConstant.USER_REGISTERED_SUCCESSFULLY, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getUsername()).get();
        if (Objects.isNull(user)) {
            return ResponseUtil.createErrorResponse(APIConstant.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user);
            // Extract user details from token
            User userDetails = jwtUtil.extractUserDetails(token);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            AuthResponse authResponse = new AuthResponse(token, jwtUtil.getExpirationTime(), userDetails);
            return ResponseUtil.createSuccessResponse(authResponse, HttpStatus.OK); // Success
        } else {
            return ResponseUtil.createErrorResponse(APIConstant.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED); // Error
        }
    }
}



