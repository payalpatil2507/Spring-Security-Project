package com.example.SpringTokenSecurity.handler;

import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationEventListener {
    @EventListener
    public void handleAuthorizationDeniedEvent(AuthorizationDeniedEvent event) {
        System.out.println("\n\n\n\n Access denied: " + event + "\n\n\n\n");
    }
}

