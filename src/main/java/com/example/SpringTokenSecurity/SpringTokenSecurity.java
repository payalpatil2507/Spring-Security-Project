package com.example.SpringTokenSecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringTokenSecurity { //implements CommandLineRunner
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder; // Autowire the PasswordEncoder interface


    public static void main(String[] args) {
        SpringApplication.run(SpringTokenSecurity.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//        User user = new User();
//        user.setUsername("pooja@narola.gamail");
//        user.setFirstName("Pooja");
//        user.setLastName("Patil");
//        user.setPassword(this.passwordEncoder.encode("pooja"));
//        user.setRole("Normal");
//        this.userRepository.save(user);
//
//        User user1 = new User();
//        user1.setUsername("dharmesh@narola.gmail");
//        user1.setFirstName("Dharmesh");
//        user1.setLastName("Sharma");
//        user1.setPassword(this.passwordEncoder.encode("dharmesh"));
//        user1.setRole("Normal");
//        this.userRepository.save(user1);
//    }
}
