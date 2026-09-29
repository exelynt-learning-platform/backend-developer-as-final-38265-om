
package com.example.Booking.Repository;

import com.example.Booking.Entity.Reservation;
import com.example.Booking.Enum.Status;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Reservation query specifications. Optional filter factories return {@code null}
 * when their input is absent; Spring Data JPA treats the resulting predicate as unrestricted.
 */
public class ReservationSpecification {
    public static Specification<Reservation> hasStatus(Status status) {

        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return null;
            }

            return criteriaBuilder.equal(
                    root.get("status"),
                    status
            );
        };
    }


    public static Specification<Reservation> priceGreaterThanOrEqualTo(
            BigDecimal minPrice) {

        return (root, query, criteriaBuilder) -> {

            if (minPrice == null) {
                return null;
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"),
                    minPrice
            );
        };
    }


    public static Specification<Reservation> priceLessThanOrEqualTo(
            BigDecimal maxPrice) {

        return (root, query, criteriaBuilder) -> {

            if (maxPrice == null) {
                return null;
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"),
                    maxPrice
            );
        };
    }


    public static Specification<Reservation> belongsToUser(
           Long userId) {

        return (root, query, criteriaBuilder) ->

                criteriaBuilder.equal(
                        root.get("user").get("id"),
                        userId
                );
    }
}
