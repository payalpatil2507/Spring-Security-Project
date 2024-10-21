package com.example.SpringTokenSecurity.config;

import com.example.SpringTokenSecurity.constants.APIConstant;
import com.example.SpringTokenSecurity.service.UserDetailsServiceImpl;
import com.example.SpringTokenSecurity.utils.JwtTokenUtil;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtTokenUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(HandlerExceptionResolver handlerExceptionResolver, JwtTokenUtil jwtService, UserDetailsServiceImpl userDetailsService) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtUtil = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        boolean isLoginRequest = request.getRequestURI().equals("/login");

        // Check if the Authorization header is missing or invalid
        if (authHeader == null || !authHeader.startsWith(APIConstant.BEARER_PREFIX)) {
            if (!isLoginRequest) {
                AuthenticationCredentialsNotFoundException exception = new AuthenticationCredentialsNotFoundException("Authorization header is missing or invalid.");
                handlerExceptionResolver.resolveException(request, response, null, exception);
                return;
            }
            filterChain.doFilter(request, response);
        } else {
            processToken(authHeader, request, response, filterChain);
        }
    }

    private void processToken(String authHeader, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            final String jwt = authHeader.substring(APIConstant.BEARER_PREFIX.length()).trim();

            if (!jwtUtil.validateToken(jwt)) {
                AuthenticationCredentialsNotFoundException exception = new AuthenticationCredentialsNotFoundException("Authorization token is invalid.");
                handlerExceptionResolver.resolveException(request, response, null, exception);
                return;
            }

            final String userEmail = jwtUtil.extractUsername(jwt);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (userEmail != null && authentication == null) {
                authenticateUser(userEmail, jwt, request);
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    private void authenticateUser(String userEmail, String jwt, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (jwtUtil.isTokenValid(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
}


