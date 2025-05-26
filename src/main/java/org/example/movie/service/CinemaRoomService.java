package org.example.movie.service;

import org.example.movie.dto.request.AddCinemaRoomRequest;
import org.example.movie.dto.request.UpdateSeatTypeRequest;
import org.example.movie.dto.response.CinemaRoomResponse;
import org.example.movie.dto.response.SeatDetailResponse; // Thay SeatResponse bằng SeatDetailResponse
import org.example.movie.entity.CinemaRoom;
import org.example.movie.entity.Seat;
import org.example.movie.enums.SeatStatus;
import org.example.movie.enums.SeatType;
import org.example.movie.mapper.CinemaRoomMapper;
import org.example.movie.repository.CinemaRoomRepository;
import org.example.movie.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CinemaRoomService {

    @Autowired
    private CinemaRoomRepository cinemaRoomRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private CinemaRoomMapper cinemaRoomMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public List<CinemaRoomResponse> getCinemaRoomList(String searchKeyword) {
        List<CinemaRoom> cinemaRooms = cinemaRoomRepository.findAll();
        return cinemaRooms.stream()
                .map(cinemaRoomMapper::toResponse)
                .filter(room -> searchKeyword == null || room.getCinemaRoomName().contains(searchKeyword))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CinemaRoomResponse getCinemaRoomDetail(Long cinemaRoomId) {
        CinemaRoom cinemaRoom = cinemaRoomRepository.findById(cinemaRoomId)
                .orElseThrow(() -> new RuntimeException("Cinema room not found"));
        return cinemaRoomMapper.toResponse(cinemaRoom);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<SeatDetailResponse> getSeatsByCinemaRoom(Long cinemaRoomId) { // Thay SeatResponse bằng SeatDetailResponse
        List<Seat> seats = seatRepository.findByCinemaRoomCinemaRoomId(cinemaRoomId);
        return seats.stream()
                .map(cinemaRoomMapper::toSeatDetailResponse) // Thay toSeatResponse bằng toSeatDetailResponse
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String updateSeatType(Long cinemaRoomId, UpdateSeatTypeRequest request) {
        CinemaRoom cinemaRoom = cinemaRoomRepository.findById(cinemaRoomId)
                .orElseThrow(() -> new RuntimeException("Cinema room not found"));

        List<Seat> seats = seatRepository.findByCinemaRoomCinemaRoomId(cinemaRoomId);
        seats.stream()
                .filter(seat -> request.getSeatIds().contains(seat.getSeatId()))
                .forEach(seat -> seat.setSeatType(SeatType.values()[request.getNewSeatType()]));

        seatRepository.saveAll(seats);

        return "Seat types updated successfully";
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String addCinemaRoom(AddCinemaRoomRequest request) {
        CinemaRoom cinemaRoom = CinemaRoom.builder()
                .cinemaRoomName(request.getCinemaRoomName())
                .seatQuantity(request.getSeatQuantity())
                .build();

        cinemaRoomRepository.save(cinemaRoom);

        List<Seat> seats = generateSeats(cinemaRoom, request.getSeatQuantity());
        seatRepository.saveAll(seats);

        return "Cinema room added successfully";
    }

    private List<Seat> generateSeats(CinemaRoom cinemaRoom, Integer seatQuantity) {
        List<Seat> seats = new ArrayList<>();
        int rows = 10;
        int cols = seatQuantity / rows;
        char[] columns = {'A', 'B', 'C', 'D', 'E', 'F'};

        for (int row = 1; row <= rows; row++) {
            for (int col = 0; col < Math.min(cols, columns.length); col++) {
                Seat seat = Seat.builder()
                        .seatColumn(String.valueOf(columns[col]))
                        .seatRow(row)
                        .seatStatus(SeatStatus.AVAILABLE)
                        .seatType(SeatType.REGULAR)
                        .cinemaRoom(cinemaRoom)
                        .build();
                seats.add(seat);
            }
        }

        return seats;
    }
}