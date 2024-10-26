package com.example.SpringTokenSecurity.handler;

import com.example.SpringTokenSecurity.dto.APIResponse;
import com.example.SpringTokenSecurity.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

import static com.example.SpringTokenSecurity.constants.APIConstant.LOGOUT_SUCCESS_MASSAGE;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON converter

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {

        ResponseEntity<APIResponse<Object>> apiResponse =
                ResponseUtil.createSuccessResponse(LOGOUT_SUCCESS_MASSAGE, HttpStatus.OK);

        response.setContentType("application/json");
        response.setStatus(apiResponse.getStatusCode().value());

        // Write the standardized success response body as JSON
        objectMapper.writeValue(response.getOutputStream(), apiResponse.getBody());
        response.getOutputStream().flush();
    }
}
