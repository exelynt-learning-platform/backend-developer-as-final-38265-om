package com.example.Booking;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ActiveProfiles("test")
class BookingApplicationTests {
	@DynamicPropertySource
	static void jwtSecret(DynamicPropertyRegistry registry) {
		byte[] key = new byte[32];
		new SecureRandom().nextBytes(key);
		registry.add("jwt.secret", () -> Base64.getEncoder().encodeToString(key));
	}

	@Test
	void contextLoads() {
	}

}
