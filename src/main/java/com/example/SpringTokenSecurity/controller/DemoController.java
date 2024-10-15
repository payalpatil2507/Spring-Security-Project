package com.example.SpringTokenSecurity.controller;

import com.example.SpringTokenSecurity.constants.APIConstant;
import com.example.SpringTokenSecurity.dto.APIResponse;
import com.example.SpringTokenSecurity.model.User;
import com.example.SpringTokenSecurity.utils.JwtUtil;
import com.example.SpringTokenSecurity.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<APIResponse<User>> getProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        // Check if the Authorization header is present and has the correct format
        if (authorizationHeader != null && authorizationHeader.startsWith(APIConstant.BEARER_PREFIX)) {
            String token = authorizationHeader.substring(7); // Extract token

            // Extract user details from token
            User userDetails = jwtUtil.extractUserDetails(token);

            if (userDetails != null) {
                return ResponseUtil.createSuccessResponse(userDetails, HttpStatus.OK);
            } else {
                return ResponseUtil.createErrorResponse(APIConstant.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
            }
        } else {
            // Invalid token format: 400 Bad Request
            return ResponseUtil.createErrorResponse(APIConstant.INVALID_TOKEN_FORMAT, HttpStatus.BAD_REQUEST);
        }
    }


}

