package org.example.hotelreservation.security;

import org.example.hotelreservation.entity.Role;
import org.example.hotelreservation.entity.User;
import org.example.hotelreservation.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner createAdmin(UserService userService) {
        return args -> {
            if (userService.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setRole(Role.ADMIN);
                userService.registerUser(admin);
                System.out.println(">>> Admin account created: admin / admin123");
            }
        };
    }

    @Bean
    public CommandLineRunner createUser(UserService userService) {
        return args -> {
            if (userService.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setPassword("user123");
                user.setRole(Role.USER);
                userService.registerUser(user);
                System.out.println(">>> User account created: user / user123");
            }
        };
    }
}