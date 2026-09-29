package com.example.Booking.Repository;

import com.example.Booking.Entity.Resources;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resources,Long> {
}
