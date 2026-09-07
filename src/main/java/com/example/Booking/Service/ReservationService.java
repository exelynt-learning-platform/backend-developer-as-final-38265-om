package com.example.Booking.Service;

import com.example.Booking.Dto.ReservationRequestDto;
import com.example.Booking.Dto.ReservationResponseDto;
import com.example.Booking.Entity.Reservations;
import com.example.Booking.Entity.Resources;
import com.example.Booking.Entity.User;
import com.example.Booking.Enum.Role;
import com.example.Booking.Enum.Status;
import com.example.Booking.Repository.ReservationRepository;
import com.example.Booking.Repository.ResourceRepository;
import com.example.Booking.Repository.UserRepository;
import com.example.Booking.Mapper.ReservationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.example.Booking.Repository.ReservationSpecification.*;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final ReservationMapper reservationMapper;

    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            ResourceRepository resourceRepository,
            ReservationMapper reservationMapper) {

        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.reservationMapper = reservationMapper;
    }

    // CREATE RESERVATION
    @Transactional
    public ReservationResponseDto createReservation(
            ReservationRequestDto request,
            UserDetails userDetails) {

        User user = getLoggedInUser(userDetails);

        Resources resource =
                resourceRepository
                        .findById(request.getResourceId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resource not found"));

        if (!resource.getAvailable()) {
            throw new RuntimeException(
                    "Resource is not available");
        }

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        Reservations reservation =
                new Reservations();

        // USER comes from JWT
        reservation.setUser(user);

        reservation.setResource(resource);

        reservation.setStartTime(
                request.getStartTime());

        reservation.setEndTime(
                request.getEndTime());

        reservation.setPrice(
                resource.getPrice());

        reservation.setStatus(
                Status.PENDING);

        Reservations saved =
                reservationRepository.save(
                        reservation);

        return mapToResponse(saved);
    }

    // GET RESERVATIONS
    @Transactional(readOnly = true)
    public Page<ReservationResponseDto> getReservations(
            UserDetails userDetails,
            Status status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        User user =
                getLoggedInUser(userDetails);

        Specification<Reservations> specification =
                Specification
                        .where(hasStatus(status))
                        .and(priceGreaterThanOrEqualTo(minPrice))
                        .and(priceLessThanOrEqualTo(maxPrice));

        // ADMIN → all reservations
        // USER → only own reservations
        if (user.getRoles() != Role.ADMIN) {

            specification =
                    specification.and(
                            belongsToUser(user.getId()));
        }

        return reservationRepository
                .findAll(
                        specification,
                        pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ReservationResponseDto getReservationById(
            Long reservationId,
            UserDetails userDetails) {

        User user = getLoggedInUser(userDetails);
        Reservations reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (user.getRoles() != Role.ADMIN
                && !reservation.getUser().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not allowed to access this reservation");
        }

        return mapToResponse(reservation);
    }

    // UPDATE RESERVATION
    // ADMIN operation
    @Transactional
    public ReservationResponseDto updateReservation(
            Long reservationId,
            ReservationRequestDto request) {

        Reservations reservation =
                reservationRepository
                        .findById(reservationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reservation not found"));

        Resources resource =
                resourceRepository
                        .findById(request.getResourceId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resource not found"));

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        reservation.setResource(resource);

        reservation.setStartTime(
                request.getStartTime());

        reservation.setEndTime(
                request.getEndTime());

        reservation.setPrice(
                resource.getPrice());

        if (request.getStatus() != null) {
            reservation.setStatus(request.getStatus());
        }

        Reservations updated =
                reservationRepository.save(
                        reservation);

        return mapToResponse(updated);
    }

    // DELETE RESERVATION
    // ADMIN operation
    @Transactional
    public void deleteReservation(
            Long reservationId) {

        Reservations reservation =
                reservationRepository
                        .findById(reservationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reservation not found"));

        reservationRepository.delete(
                reservation);
    }

    // GET LOGGED-IN USER
    private User getLoggedInUser(
            UserDetails userDetails) {

        return userRepository
                .findByEmail(
                        userDetails.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"));
    }

    // ENTITY → DTO
    private ReservationResponseDto mapToResponse(
            Reservations reservation) {

        return reservationMapper.toDto(reservation);
    }
}
