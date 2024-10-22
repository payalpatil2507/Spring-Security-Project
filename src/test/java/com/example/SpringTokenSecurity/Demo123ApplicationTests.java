package com.example.SpringTokenSecurity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class Demo123ApplicationTests {

    @Test
    public void testInheritableThreadLocalSecurityContext() throws InterruptedException {
        // Set the strategy to InheritableThreadLocal for the SecurityContext
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);


        Authentication parentAuthentication = new UsernamePasswordAuthenticationToken("parentUser", "password");
        SecurityContext parentContext = SecurityContextHolder.createEmptyContext();
        parentContext.setAuthentication(parentAuthentication);
        SecurityContextHolder.setContext(parentContext);

        // Print out the authentication in the parent thread
        System.out.println("Parent Thread Authentication: " + SecurityContextHolder.getContext().getAuthentication());

        // Create a child thread that should inherit the SecurityContext from the parent thread
        Thread childThread = new Thread(() -> {

            Authentication childAuthentication = SecurityContextHolder.getContext().getAuthentication();

            if (childAuthentication != null) {
                System.out.println("Child Thread Authentication: " + childAuthentication.getName());
            } else {
                System.out.println("Child Thread Authentication: No Authentication");
            }
        });

        // Start the child thread and wait for it to complete
        childThread.start();
        childThread.join();


        System.out.println("Parent Thread Authentication After Child: " + SecurityContextHolder.getContext().getAuthentication().getName());

        // Clear the SecurityContextHolder to avoid side effects on other tests
        SecurityContextHolder.clearContext();
    }
}