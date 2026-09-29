package com.example.Booking.Controller;

import com.example.Booking.Dto.ReservationRequestDto;
import com.example.Booking.Dto.AdminReservationUpdateDto;
import com.example.Booking.Dto.ReservationResponseDto;
import com.example.Booking.Enum.Status;
import com.example.Booking.Service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    // Must match the field names in the Reservation entity exactly
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("startTime", "endTime", "status", "price");

    private static final int MAX_PAGE_SIZE = 100;

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // USER + ADMIN: create reservation
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ReservationResponseDto> createReservation(
            @Valid @RequestBody ReservationRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationService.createReservation(request, userDetails));
    }

    // USER -> own reservations, ADMIN -> all (enforced in the service)
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<ReservationResponseDto>> getReservations(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice must be less than or equal to maxPrice");
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sortBy '" + sortBy + "'. Allowed values: "
                            + String.join(", ", new TreeSet<>(ALLOWED_SORT_FIELDS)));
        }

        // Throws IllegalArgumentException for anything other than asc/desc (case-insensitive)
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        if (page < 0) {
            throw new IllegalArgumentException("page must be 0 or greater");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("size must be between 1 and " + MAX_PAGE_SIZE);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(
                reservationService.getReservations(
                        userDetails, status, minPrice, maxPrice, pageable));
    }

    // USER -> own only, ADMIN -> any (enforced in the service)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ReservationResponseDto> getReservationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(reservationService.getReservationById(id, userDetails));
    }

    // USER may update an owned reservation; ADMIN may update any reservation.
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ReservationResponseDto> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(reservationService.updateReservation(id, request, userDetails));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationResponseDto> updateReservationStatus(
            @PathVariable Long id, @Valid @RequestBody AdminReservationUpdateDto request) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, request));
    }

    // ADMIN ONLY
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
