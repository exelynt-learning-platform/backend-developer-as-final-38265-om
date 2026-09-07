package com.example.Booking;

import com.example.Booking.Dto.LoginRequestDto;
import com.example.Booking.Dto.ReservationRequestDto;
import com.example.Booking.Dto.ReservationResponseDto;
import com.example.Booking.Dto.ResourceRequestDto;
import com.example.Booking.Dto.ResourceResponseDto;
import com.example.Booking.Entity.Reservations;
import com.example.Booking.Entity.Resources;
import com.example.Booking.Entity.User;
import com.example.Booking.Enum.Role;
import com.example.Booking.Enum.Status;
import com.example.Booking.Repository.ReservationRepository;
import com.example.Booking.Repository.ResourceRepository;
import com.example.Booking.Repository.UserRepository;
import com.example.Booking.Security.JwtService;
import com.example.Booking.Security.Userdetails;
import com.example.Booking.Service.AuthService;
import com.example.Booking.Service.ReservationService;
import com.example.Booking.Service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class BookingServiceTests {

    @Autowired private AuthService authService;
    @Autowired private JwtService jwtService;
    @Autowired private ResourceService resourceService;
    @Autowired private ReservationService reservationService;
    @Autowired private UserRepository userRepository;
    @Autowired private ResourceRepository resourceRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        reservationRepository.deleteAll();
        resourceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void loginWithSeedStyleUsernameProducesAValidJwt() {
        User user = saveUser("user", "user@booking.com", Role.USER);
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("user");
        request.setPassword("Password@123");

        String token = authService.login(request).getToken();

        assertTrue(jwtService.validateToken(token, new Userdetails(user)));
    }

    @Test
    void resourceCrudPreservesLongIdAndData() {
        ResourceRequestDto request = resourceRequest("Room", new BigDecimal("100.00"));
        ResourceResponseDto created = resourceService.createResource(request);
        request.setName("Updated room");

        ResourceResponseDto updated = resourceService.updateResource(created.getId(), request);

        assertEquals(created.getId(), updated.getId());
        assertEquals("Updated room", resourceService.getResourceById(created.getId()).getName());
        resourceService.deleteResource(created.getId());
        assertThrows(RuntimeException.class, () -> resourceService.getResourceById(created.getId()));
    }

    @Test
    void userCanOnlyReadOwnReservationsAndFiltersAreApplied() {
        User user = saveUser("user", "user@booking.com", Role.USER);
        User other = saveUser("other", "other@booking.com", Role.USER);
        Resources resource = saveResource();
        ReservationResponseDto created = reservationService.createReservation(
                reservationRequest(resource.getId()), new Userdetails(user));
        Reservations foreign = saveReservation(other, resource, new BigDecimal("200.00"), Status.CONFIRMED);

        Page<ReservationResponseDto> result = reservationService.getReservations(
                new Userdetails(user), Status.PENDING, new BigDecimal("50.00"),
                new BigDecimal("150.00"), PageRequest.of(0, 1));

        assertEquals(1, result.getTotalElements());
        assertEquals(user.getId(), created.getUserId());
        assertEquals(created.getId(), result.getContent().get(0).getId());
        assertThrows(AccessDeniedException.class,
                () -> reservationService.getReservationById(foreign.getId(), new Userdetails(user)));
    }

    @Test
    void adminCanUpdateReservationStatus() {
        User admin = saveUser("admin", "admin@booking.com", Role.ADMIN);
        Resources resource = saveResource();
        Reservations reservation = saveReservation(admin, resource, new BigDecimal("100.00"), Status.PENDING);
        ReservationRequestDto request = reservationRequest(resource.getId());
        request.setStatus(Status.CONFIRMED);

        ReservationResponseDto updated = reservationService.updateReservation(reservation.getId(), request);

        assertEquals(Status.CONFIRMED, updated.getStatus());
    }

    private User saveUser(String username, String email, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("Password@123"));
        user.setRoles(role);
        return userRepository.save(user);
    }

    private Resources saveResource() {
        Resources resource = new Resources();
        resource.setName("Room");
        resource.setAvailable(true);
        resource.setPrice(new BigDecimal("100.00"));
        return resourceRepository.save(resource);
    }

    private Reservations saveReservation(User user, Resources resource, BigDecimal price, Status status) {
        Reservations reservation = new Reservations();
        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartTime(LocalDateTime.now().plusDays(1));
        reservation.setEndTime(LocalDateTime.now().plusDays(2));
        reservation.setPrice(price);
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }

    private ResourceRequestDto resourceRequest(String name, BigDecimal price) {
        ResourceRequestDto request = new ResourceRequestDto();
        request.setName(name);
        request.setAvailable(true);
        request.setPrice(price);
        return request;
    }

    private ReservationRequestDto reservationRequest(Long resourceId) {
        ReservationRequestDto request = new ReservationRequestDto();
        request.setResourceId(resourceId);
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(2));
        return request;
    }
}
