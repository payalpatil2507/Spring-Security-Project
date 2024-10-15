package com.example.SpringTokenSecurity.service;

import com.example.SpringTokenSecurity.dto.CustomUser;
import com.example.SpringTokenSecurity.model.User;
import com.example.SpringTokenSecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new CustomUser(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getRole());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }
}

