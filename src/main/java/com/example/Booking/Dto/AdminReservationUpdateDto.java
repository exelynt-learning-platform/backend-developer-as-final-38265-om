package com.example.Booking.Dto;

import com.example.Booking.Enum.Status;
import jakarta.validation.constraints.NotNull;

public class AdminReservationUpdateDto {
    @NotNull(message = "Status is required")
    private Status status;

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
