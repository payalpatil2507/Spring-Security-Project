package com.example.SpringTokenSecurity.exception;

import com.example.SpringTokenSecurity.dto.APIResponse;
import com.example.SpringTokenSecurity.utils.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleSecurityException(Exception exception) {
        // Log the exception for observability
        exception.printStackTrace();

        if (exception instanceof BadCredentialsException) {
            return ResponseUtil.createErrorResponse("Invalid username or password. Please try again.", HttpStatus.UNAUTHORIZED);
        }

        if (exception instanceof AccountStatusException) {
            return ResponseUtil.createErrorResponse("Your account is currently locked or inactive. Please contact support.", HttpStatus.FORBIDDEN);
        }

        if (exception instanceof AccessDeniedException) {
            return ResponseUtil.createErrorResponse("You do not have permission to access this resource.", HttpStatus.FORBIDDEN);
        }
        if (exception instanceof MalformedJwtException) {
            return ResponseUtil.createErrorResponse("Bearer token is invalid.", HttpStatus.UNAUTHORIZED);
        }
        if (exception instanceof ExpiredJwtException) {
            return ResponseUtil.createErrorResponse("Your session has expired. Please log in again.", HttpStatus.UNAUTHORIZED);
        }

        if (exception instanceof AuthenticationCredentialsNotFoundException) {
            return ResponseUtil.createErrorResponse("Bearer token is missing or invalid. Please provide a valid Bearer token.", HttpStatus.UNAUTHORIZED);
        }

        if (exception instanceof InsufficientAuthenticationException) {
            return ResponseUtil.createErrorResponse("Bearer token is invalid.", HttpStatus.UNAUTHORIZED);
        }

        // Handle other generic exceptions
        return ResponseUtil.createErrorResponse("An internal server error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
