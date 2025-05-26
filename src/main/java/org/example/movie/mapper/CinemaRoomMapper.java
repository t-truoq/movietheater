package org.example.movie.mapper;

import org.example.movie.dto.response.CinemaRoomResponse;
import org.example.movie.dto.response.SeatDetailResponse;
import org.example.movie.entity.CinemaRoom;
import org.example.movie.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CinemaRoomMapper {
    @Mapping(source = "cinemaRoomId", target = "cinemaRoomId")
    @Mapping(source = "cinemaRoomName", target = "cinemaRoomName")
    @Mapping(source = "seatQuantity", target = "seatQuantity")
    CinemaRoomResponse toResponse(CinemaRoom cinemaRoom);

    @Mapping(source = "seatId", target = "seatId")
    @Mapping(source = "seatColumn", target = "seatColumn")
    @Mapping(source = "seatRow", target = "seatRow")
    @Mapping(source = "seatType", target = "seatType")
    SeatDetailResponse toSeatDetailResponse(Seat seat);
}