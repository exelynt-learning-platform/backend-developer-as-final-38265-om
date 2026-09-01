package com.example.Booking.Repository;

import com.example.Booking.Entity.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReservationRepository
        extends JpaRepository<Reservations, Long>,
        JpaSpecificationExecutor<Reservations> {
}