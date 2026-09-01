package com.example.Booking.Mapper;

import com.example.Booking.Dto.ReservationResponseDto;
import com.example.Booking.Entity.Reservations;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "resourceId", source = "resource.id")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    ReservationResponseDto toDto(Reservations reservation);

    List<ReservationResponseDto> toDtoList(List<Reservations> reservations);
}
