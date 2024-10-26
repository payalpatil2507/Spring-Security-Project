package com.example.SpringTokenSecurity.config;

import com.example.SpringTokenSecurity.handler.CustomLogoutSuccessHandler;
import com.example.SpringTokenSecurity.handler.JwtAuthenticationFailureHandler;
import com.example.SpringTokenSecurity.handler.JwtAuthenticationSuccessHandler;
import com.example.SpringTokenSecurity.utils.JwtTokenUtil;
import com.example.SpringTokenSecurity.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ResponseUtil responseUtil;
    @Autowired
    private UserDetailsService userDetailsService;

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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**", "/login", "/register", "/logout").permitAll()
                        .anyRequest().authenticated()
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
                        .tokenValiditySeconds(86400) // Set the token validity period (1 day)
                )
                .addFilter(jwtAuthenticationFilter)// Add JWT authentication filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Validate JWT for all requests
              return http.build();
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
