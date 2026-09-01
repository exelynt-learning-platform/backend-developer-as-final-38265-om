package com.example.Booking.Config;

import com.example.Booking.Entity.User;
import com.example.Booking.Enum.Role;
import com.example.Booking.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // =========================
            // ADMIN USER
            // =========================



            if (userRepository.findByUsername("admin").isEmpty()) {

                User admin = new User();

                admin.setUsername("admin");
                admin.setEmail("admin@booking.com");

                // Password is stored using BCrypt
                admin.setPassword(
                        passwordEncoder.encode("Admin@123")
                );

                admin.setRoles(Role.ADMIN);

                userRepository.save(admin);

                System.out.println(
                        "Seeded ADMIN user: admin"
                );
            }


            // =========================
            // NORMAL USER
            // =========================

            if (userRepository.findByUsername("user").isEmpty()) {

                User user = new User();

                user.setUsername("user");
                user.setEmail("user@booking.com");

                // Password is stored using BCrypt
                user.setPassword(
                        passwordEncoder.encode("User@123")
                );

                user.setRoles(Role.USER);

                userRepository.save(user);

                System.out.println(
                        "Seeded USER user: user"
                );
            }
        };
    }
}