package com.example.Booking.Dto;

import com.example.Booking.Enum.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationResponseDto {

    private Long id;
    private Long userId;
    private Long resourceId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private BigDecimal price;
    private Status status;

    public ReservationResponseDto() {
    }

    public ReservationResponseDto(
            Long id,
            Long userId,
            Long resourceId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            BigDecimal price,
            Status status) {

        this.id = id;
        this.userId = userId;
        this.resourceId = resourceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}