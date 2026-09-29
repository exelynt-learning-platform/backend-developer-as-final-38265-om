package com.example.Booking.Config;

import com.example.Booking.Entity.User;
import com.example.Booking.Enum.Role;
import com.example.Booking.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev") // FIX: Gates the entire data seeding configuration under the 'dev' profile
public class DataInitializer {

    // FIX: Externalised passwords via environment variables with default values for local safety
    @Value("${SEED_ADMIN_PASSWORD:Admin@123}")
    private String adminPassword;

    @Value("${SEED_USER_PASSWORD:User@123}")
    private String userPassword;

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

                // FIX: Used externalised variable reference
                admin.setPassword(
                        passwordEncoder.encode(adminPassword)
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

                // FIX: Used externalised variable reference
                user.setPassword(
                        passwordEncoder.encode(userPassword)
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
