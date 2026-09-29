package com.example.Booking.Repository;

import com.example.Booking.Entity.Reservation;
import com.example.Booking.Enum.Status;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Reservation query specifications.
 * Methods explicitly return Specification.unrestricted() instead of ambiguous null definitions
 * to perfectly align with modern Spring Data JPA criteria building.
 */
public class ReservationSpecification {

    private ReservationSpecification() {
        // Private constructor to prevent instantiation of utility class
    }

    public static Specification<Reservation> hasStatus(Status status) {
        if (status == null) {
            return Specification.unrestricted(); // Safe, unambiguous no-op query filter
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Reservation> priceGreaterThanOrEqualTo(BigDecimal minPrice) {
        if (minPrice == null) {
            return Specification.unrestricted();
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Reservation> priceLessThanOrEqualTo(BigDecimal maxPrice) {
        if (maxPrice == null) {
            return Specification.unrestricted();
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Reservation> belongsToUser(Long userId) {
        if (userId == null) {
            return Specification.unrestricted();
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user").get("id"), userId);
    }
}
