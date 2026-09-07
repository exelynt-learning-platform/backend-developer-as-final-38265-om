package com.example.Booking.Controller;

import com.example.Booking.Dto.ReservationRequestDto;
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

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService) {

        this.reservationService = reservationService;
    }

    // USER + ADMIN
    // CREATE RESERVATION
    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(
            @Valid @RequestBody ReservationRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        reservationService.createReservation(
                                request,
                                userDetails
                        )
                );
    }

    // USER → OWN RESERVATIONS
    // ADMIN → ALL RESERVATIONS
    //
    // Filtering:
    // status
    // minPrice
    // maxPrice
    //
    // Pagination:
    // page
    // size
    //
    // Sorting:
    // sortBy
    // sortDirection
    @GetMapping
    public ResponseEntity<Page<ReservationResponseDto>> getReservations(

            @AuthenticationPrincipal UserDetails userDetails,

            @RequestParam(required = false)
            Status status,

            @RequestParam(required = false)
            BigDecimal minPrice,

            @RequestParam(required = false)
            BigDecimal maxPrice,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "startTime")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        sort
                );

        return ResponseEntity.ok(
                reservationService.getReservations(
                        userDetails,
                        status,
                        minPrice,
                        maxPrice,
                        pageable
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> getReservationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(reservationService.getReservationById(id, userDetails));
    }

    // ADMIN ONLY
    // UPDATE RESERVATION
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationResponseDto> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequestDto request) {

        return ResponseEntity.ok(
                reservationService.updateReservation(
                        id,
                        request
                )
        );
    }

    // ADMIN ONLY
    // DELETE RESERVATION
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id) {

        reservationService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }
}
