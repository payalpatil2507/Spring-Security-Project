package com.example.SpringTokenSecurity.handler;

import com.example.SpringTokenSecurity.dto.APIResponse;
import com.example.SpringTokenSecurity.dto.CustomUser;
import com.example.SpringTokenSecurity.utils.JwtTokenUtil;
import com.example.SpringTokenSecurity.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final ResponseUtil responseUtil;
    @Autowired
    private TokenBasedRememberMeServices tokenBasedRememberMeServices;

    @Autowired
    public JwtAuthenticationSuccessHandler(JwtTokenUtil jwtTokenUtil, ResponseUtil responseUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.responseUtil = responseUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomUser userDetails = (CustomUser) authentication.getPrincipal(); // Get user details
        String token = jwtTokenUtil.generateToken(userDetails); // Generate JWT token
        // Handle remember-me functionality
        if (request.getParameter("remember-me") != null) {
            tokenBasedRememberMeServices.loginSuccess(request, response, authentication); // Set remember-me cookie
        }

        // Create a response object
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        userDetails.setPassword(null);
        responseData.put("user", userDetails); // Add user details to the response

        // Create the API response
        ResponseEntity<APIResponse<Map<String, Object>>> apiResponse = responseUtil.createSuccessResponse(responseData, HttpStatus.OK);
        response.setContentType("application/json");
        response.setStatus(apiResponse.getStatusCode().value());

        // Write the response to the output stream
        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse.getBody());
    }
}

