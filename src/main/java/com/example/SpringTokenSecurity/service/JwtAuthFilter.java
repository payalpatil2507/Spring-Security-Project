package com.example.SpringTokenSecurity.service;

import com.example.SpringTokenSecurity.constants.APIConstant;
import com.example.SpringTokenSecurity.utils.JwtUtil;
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
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(HandlerExceptionResolver handlerExceptionResolver, JwtUtil jwtService, UserDetailsServiceImpl userDetailsService) {
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
            try {
                final String jwt = authHeader.substring(APIConstant.BEARER_PREFIX.length()).trim();
                if (jwtUtil.validateToken(jwt)) {
                    final String userEmail = jwtUtil.extractUsername(jwt);

                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    if (userEmail != null && authentication == null) {
                        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

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
                    filterChain.doFilter(request, response);
                } else {
                    AuthenticationCredentialsNotFoundException exception = new AuthenticationCredentialsNotFoundException("Authorization header is invalid.");
                    handlerExceptionResolver.resolveException(request, response, null, exception);
                    return;
                }
            } catch (Exception exception) {
                handlerExceptionResolver.resolveException(request, response, null, exception);
                return;
            }
        }
    }

}
