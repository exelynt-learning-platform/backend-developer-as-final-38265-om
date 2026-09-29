package com.example.Booking.Repository;

import com.example.Booking.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long>,
        JpaSpecificationExecutor<Reservation> {
    boolean existsByResourceId(Long resourceId);

    boolean existsByResourceIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long resourceId, Long reservationId, java.time.LocalDateTime endTime,
            java.time.LocalDateTime startTime);
}
