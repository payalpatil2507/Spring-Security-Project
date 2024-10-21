package com.example.SpringTokenSecurity.config;

import com.example.SpringTokenSecurity.constants.APIConstant;
import com.example.SpringTokenSecurity.dto.APIResponse;
import com.example.SpringTokenSecurity.dto.CustomUser;
import com.example.SpringTokenSecurity.dto.LoginRequest;
import com.example.SpringTokenSecurity.model.User;
import com.example.SpringTokenSecurity.utils.JwtTokenUtil;
import com.example.SpringTokenSecurity.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ResponseUtil responseUtil;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword());
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        CustomUser userDetails = (CustomUser) authResult.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails); // Generate JWT token

        // Create a response object using the success method
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("user", userDetails);

        ResponseEntity<APIResponse<Map<String, Object>>> apiResponse = responseUtil.createSuccessResponse(responseData, HttpStatus.OK);
        response.setContentType("application/json");
        response.setStatus(apiResponse.getStatusCode().value());
        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse.getBody());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        ResponseEntity<APIResponse<Object>> apiResponse = responseUtil.createErrorResponse(APIConstant.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        response.setContentType("application/json");
        response.setStatus(apiResponse.getStatusCode().value());
        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse.getBody());
    }

}

