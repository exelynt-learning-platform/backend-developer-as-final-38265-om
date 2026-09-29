package com.example.Booking.Config;

import com.example.Booking.Entity.User;
import com.example.Booking.Enum.Role;
import com.example.Booking.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds demo users for local development ONLY.
 * Passwords are never stored in source: SEED_ADMIN_PASSWORD and SEED_USER_PASSWORD
 * must be supplied as environment variables. The app fails to start if either is
 * missing or blank while the 'dev' profile is active.
 */
@Configuration
@Profile("dev")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final String adminPassword;
    private final String userPassword;

    public DataInitializer(@Value("${SEED_ADMIN_PASSWORD}") String adminPassword,
                           @Value("${SEED_USER_PASSWORD}") String userPassword) {
        this.adminPassword = adminPassword;
        this.userPassword = userPassword;
    }

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {

        return args -> {
            requireNonBlank(adminPassword, "SEED_ADMIN_PASSWORD");
            requireNonBlank(userPassword, "SEED_USER_PASSWORD");

            createIfMissing(userRepository, passwordEncoder,
                    "admin", "admin@booking.com", adminPassword, Role.ADMIN);

            createIfMissing(userRepository, passwordEncoder,
                    "user", "user@booking.com", userPassword, Role.USER);
        };
    }

    private void createIfMissing(UserRepository repo, PasswordEncoder encoder,
                                 String username, String email,
                                 String rawPassword, Role role) {

        if (repo.findByUsername(username).isPresent()) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encoder.encode(rawPassword));
        user.setRoles(role);
        repo.save(user);

        // Log the username only, never the password
        log.info("Seeded {} user: {}", role, username);
    }

    private static void requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " must be set and non-blank");
        }
    }
}
