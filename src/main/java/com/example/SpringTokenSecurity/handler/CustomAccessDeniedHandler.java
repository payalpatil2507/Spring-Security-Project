package com.example.SpringTokenSecurity.handler;

import com.example.SpringTokenSecurity.dto.APIResponse;
import com.example.SpringTokenSecurity.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.SpringTokenSecurity.constants.APIConstant.ACCESS_DENIED_MASSAGE;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON converter

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {

        ResponseEntity<APIResponse<Object>> apiResponse =
                ResponseUtil.createSuccessResponse(ACCESS_DENIED_MASSAGE, HttpStatus.FORBIDDEN);

        response.setContentType("application/json");
        response.setStatus(apiResponse.getStatusCode().value());

        // Write the standardized success response body as JSON
        objectMapper.writeValue(response.getOutputStream(), apiResponse.getBody());
        response.getOutputStream().flush();

    }
}
