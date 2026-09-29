package com.example.Booking.Service;

import com.example.Booking.Dto.ReservationRequestDto;
import com.example.Booking.Dto.ReservationResponseDto;
import com.example.Booking.Dto.AdminReservationUpdateDto;
import com.example.Booking.Entity.Reservation;
import com.example.Booking.Entity.Resources;
import com.example.Booking.Entity.User;
import com.example.Booking.Enum.Role;
import com.example.Booking.Enum.Status;
import com.example.Booking.Repository.ReservationRepository;
import com.example.Booking.Repository.ResourceRepository;
import com.example.Booking.Repository.UserRepository;
import com.example.Booking.Mapper.ReservationMapper;
import com.example.Booking.Exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import com.example.Booking.Repository.ReservationSpecification;

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
                                new ResourceNotFoundException(
                                        "Resource not found with id: " + request.getResourceId()));

        if (!Boolean.TRUE.equals(resource.getAvailable())) {
            throw new IllegalArgumentException("Resource is not available");
        }

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        Reservation reservation =
                new Reservation();

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

        Reservation saved =
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

        Specification<Reservation> specification =
                Specification
                        .where(ReservationSpecification.hasStatus(status))
                        .and(ReservationSpecification.priceGreaterThanOrEqualTo(minPrice))
                        .and(ReservationSpecification.priceLessThanOrEqualTo(maxPrice));

        // ADMIN → all reservations
        // USER → only own reservations
        if (user.getRole() != Role.ADMIN) {

            specification =
                    specification.and(
                            ReservationSpecification.belongsToUser(user.getId()));
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
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

        if (user.getRole() != Role.ADMIN
                && !reservation.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Reservation not found with id: " + reservationId);
        }

        return mapToResponse(reservation);
    }

    // UPDATE RESERVATION
    // Owner or ADMIN may update reservation details.
    @Transactional
    public ReservationResponseDto updateReservation(
            Long reservationId,
            ReservationRequestDto request,
            UserDetails userDetails) {

        Reservation reservation =
                reservationRepository
                        .findById(reservationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Reservation not found with id: " + reservationId));

        User user = getLoggedInUser(userDetails);
        if (user.getRole() != Role.ADMIN
                && !reservation.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to update this reservation");
        }

        Resources resource =
                resourceRepository
                        .findById(request.getResourceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resource not found with id: " + request.getResourceId()));

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        if (!Boolean.TRUE.equals(resource.getAvailable())) {
            throw new IllegalArgumentException("Resource is not available");
        }
        if (reservationRepository.existsByResourceIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                resource.getId(), reservationId, request.getEndTime(), request.getStartTime())) {
            throw new IllegalArgumentException("Resource is already reserved for the requested time");
        }

        reservation.setResource(resource);

        reservation.setStartTime(
                request.getStartTime());

        reservation.setEndTime(
                request.getEndTime());

        reservation.setPrice(
                resource.getPrice());

        Reservation updated =
                reservationRepository.save(
                        reservation);

        return mapToResponse(updated);
    }

    @Transactional
    public ReservationResponseDto updateReservationStatus(Long reservationId, AdminReservationUpdateDto request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));
        reservation.setStatus(request.getStatus());
        return mapToResponse(reservationRepository.save(reservation));
    }


    // DELETE RESERVATION
    // ADMIN operation
    @Transactional
    public void deleteReservation(
            Long reservationId) {
        Reservation reservation =
                reservationRepository
                        .findById(reservationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Reservation not found with id: " + reservationId));

        reservationRepository.delete(
                reservation);
    }

    // GET LOGGED-IN USER (Unified to look up by Username OR Email)
    // Identity convention: USERNAME (same as the JWT subject and Userdetails.getUsername()).
    private User getLoggedInUser(UserDetails userDetails) {
        String username = userDetails.getUsername();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in user not found: " + username));
    }


    // ENTITY → DTO
    private ReservationResponseDto mapToResponse(
            Reservation reservation) {

        return reservationMapper.toDto(reservation);
    }
}
