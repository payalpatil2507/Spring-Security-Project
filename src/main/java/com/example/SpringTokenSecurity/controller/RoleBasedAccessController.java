package com.example.SpringTokenSecurity.controller;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/access")
public class RoleBasedAccessController {

    // Only users with the 'ADMIN' role can access this method
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminPage() {
        return "Admin Page";
    }

    // Only users with 'USER' role can access this method
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String userPage() {
        return "User Page";
    }

    // Method accessible only by users who are 'ADMIN' or 'USER' roles
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/profile")
    public String profilePage() {
        return "Profile Page";
    }

    // Method accessible only by users who have a specific authority (not role)
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAuthority('ROLE_USER')")
    @GetMapping("/read")
    public String readPage() {
        return "Read Page";
    }

    // Only users with the 'ADMIN' role can access this method (using @Secured)
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin-secured")
    public String adminPageSecured() {
        return "Secured Admin Page";
    }

    // Method accessible by 'ADMIN' or 'USER' roles (using @Secured)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/user-secured")
    public String userPageSecured() {
        return "Secured User Page";
    }

    // Method using @RolesAllowed, only users with the 'ADMIN' role can access it
    @RolesAllowed("ROLE_ADMIN")
    @GetMapping("/admin-roles-allowed")
    public String adminPageRolesAllowed() {
        return "RolesAllowed Admin Page";
    }

   // Post-authorization: Method accessible only by users with 'ADMIN' role after the method has executed
    @PostAuthorize("returnObject == 'Admin Page' ? hasRole('ADMIN') : hasRole('USER')")
    @GetMapping("/admin-post-authorize")
    public String adminPagePostAuthorize() {
        return "Admin Page";
    }

    // Post-authorization: Method accessible only by users with 'USER' role after the method has executed
    @PostAuthorize("returnObject == 'User Page' ? hasRole('USER') : hasRole('ADMIN')")
    @GetMapping("/user-post-authorize")
    public String userPagePostAuthorize() {
        return "User Page";
    }

    // Post-authorization: Check that only users with the 'USER' role can access the profile page after execution
    @PostAuthorize("returnObject == 'Profile Page' ? hasAnyRole('ADMIN', 'USER') : false")
    @GetMapping("/profile-post-authorize")
    public String profilePagePostAuthorize() {
        return "Profile Page";
    }

}
