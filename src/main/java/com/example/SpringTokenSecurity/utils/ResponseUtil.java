package com.example.SpringTokenSecurity.utils;

import com.example.SpringTokenSecurity.dto.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseUtil {

    public static <T> ResponseEntity<APIResponse<T>> createSuccessResponse(T data, HttpStatus status) {
        APIResponse<T> apiResponse = new APIResponse<>(true, data, null, status.value());
        return ResponseEntity.status(status).body(apiResponse);
    }

    public static <T> ResponseEntity<APIResponse<T>> createErrorResponse(String message, HttpStatus status) {
        APIResponse<T> apiErrorResponse = new APIResponse<>();
        apiErrorResponse.setSuccess(false);
        apiErrorResponse.setMessage(message);
        apiErrorResponse.setStatus(status.value());
        return ResponseEntity.status(status).body(apiErrorResponse);
    }
}
