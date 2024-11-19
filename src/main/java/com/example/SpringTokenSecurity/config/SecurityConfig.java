package com.example.SpringTokenSecurity.config;

import com.example.SpringTokenSecurity.handler.CustomAccessDeniedHandler;
import com.example.SpringTokenSecurity.handler.CustomLogoutSuccessHandler;
import com.example.SpringTokenSecurity.handler.JwtAuthenticationFailureHandler;
import com.example.SpringTokenSecurity.handler.JwtAuthenticationSuccessHandler;
import com.example.SpringTokenSecurity.service.UserDetailsServiceImpl;
import com.example.SpringTokenSecurity.utils.JwtTokenUtil;
import com.example.SpringTokenSecurity.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true) // Enable method security
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ResponseUtil responseUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception {
        JwtAuthenticationSuccessHandler authSuccessHandler = new JwtAuthenticationSuccessHandler(jwtTokenUtil, responseUtil);
        JwtAuthenticationFailureHandler authenticationFailureHandler = new JwtAuthenticationFailureHandler();

        // Create the authentication filter
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authConfig.getAuthenticationManager(), jwtTokenUtil);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login"); // Define custom login endpoint
        jwtAuthenticationFilter.setAuthenticationSuccessHandler(authSuccessHandler); // Set the success handler
        jwtAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                            try {
                                auth
                                        .requestMatchers("/login", "/register", "/logout").permitAll() // Public endpoints
                                        .requestMatchers("/api/v1/access/**").authenticated() // Protected API
                                        .requestMatchers("/api/switchUser", "/api/exitSwitchUser").hasRole("ADMIN") // Admin-specific routes
                                        .requestMatchers("/api/**").hasRole("Normal")
                                        .requestMatchers(HttpMethod.GET, "/api/**").hasAuthority("read") // Read access authority
                                        .anyRequest().authenticated(); // Require authentication for all other requests
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session management
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler()) // Set custom handler
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID"))
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeServices(tokenBasedRememberMeServices()) // Set the remember-me services
                        .key("uniqueAndSecret") // Set a unique key for remember-me
                        .tokenValiditySeconds(86400)// Set the token validity period (1 day)
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)) // Set custom AccessDeniedHandler
                .addFilter(jwtAuthenticationFilter)// Add JWT authentication filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)// Validate JWT for all requests
                .addFilterAfter(switchUserFilter(), SwitchUserFilter.class); // Add SwitchUserFilter

        return http.build();
    }
    @Bean
    public SwitchUserFilter switchUserFilter() {
        SwitchUserFilter switchUserFilter = new SwitchUserFilter();
        switchUserFilter.setUserDetailsService(userDetailsService);
        switchUserFilter.setSwitchUserUrl("/api/switchUser");
        switchUserFilter.setExitUserUrl("/api/exitSwitchUser");
        switchUserFilter.setTargetUrl("/api/current-user");
        return switchUserFilter;
    }
    @Bean
    public TokenBasedRememberMeServices tokenBasedRememberMeServices() {
        TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices("uniqueAndSecret", userDetailsService);
        rememberMeServices.setParameter("remember-me"); // Set the remember-me parameter name to check in requests
        rememberMeServices.setTokenValiditySeconds(86400); // Set token validity to 1 day
        rememberMeServices.setAlwaysRemember(true);
        return rememberMeServices;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Password encoding
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
