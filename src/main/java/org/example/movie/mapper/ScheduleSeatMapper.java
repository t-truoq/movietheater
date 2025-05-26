package org.example.movie.mapper;

import org.example.movie.dto.response.SeatResponse;
import org.example.movie.entity.ScheduleSeat;
import org.example.movie.enums.SeatType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface ScheduleSeatMapper {

    @Mapping(source = "scheduleSeatId", target = "scheduleSeatId")
    @Mapping(source = "seatColumn", target = "seatColumn")
    @Mapping(source = "seatRow", target = "seatRow")
    @Mapping(source = "seatStatus", target = "seatStatus")
    @Mapping(source = "seatType", target = "seatType")
    SeatResponse toSeatResponse(ScheduleSeat scheduleSeat);


    default SeatType map(Integer value) {
        if (value == null) {
            return null;
        }
        switch (value) {
            case 0: return SeatType.REGULAR;
            case 1: return SeatType.VIP;
            default: throw new IllegalArgumentException("Unknown SeatType value: " + value);
        }
    }

}