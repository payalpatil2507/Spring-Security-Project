package com.example.SpringTokenSecurity.handler;

import com.example.SpringTokenSecurity.constants.APIConstant;
import com.example.SpringTokenSecurity.dto.APIResponse;
import com.example.SpringTokenSecurity.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    private ResponseUtil responseUtil;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ResponseEntity<APIResponse<Object>> apiResponse = responseUtil.createErrorResponse(APIConstant.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        response.setContentType("application/json");
        response.setStatus(apiResponse.getStatusCode().value());
        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse.getBody());
    }
}
