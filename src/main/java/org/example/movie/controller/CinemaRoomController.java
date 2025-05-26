package org.example.movie.controller;

import org.example.movie.dto.request.AddCinemaRoomRequest;
import org.example.movie.dto.request.UpdateSeatTypeRequest;
import org.example.movie.dto.response.CinemaRoomResponse;
import org.example.movie.dto.response.SeatDetailResponse; // Thay SeatResponse bằng SeatDetailResponse
import org.example.movie.service.CinemaRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cinema-room")
public class CinemaRoomController {

    @Autowired
    private CinemaRoomService cinemaRoomService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CinemaRoomResponse>> getCinemaRoomList(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(cinemaRoomService.getCinemaRoomList(search));
    }

    @GetMapping("/detail/{cinemaRoomId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CinemaRoomResponse> getCinemaRoomDetail(@PathVariable Long cinemaRoomId) {
        return ResponseEntity.ok(cinemaRoomService.getCinemaRoomDetail(cinemaRoomId));
    }

    @GetMapping("/detail/{cinemaRoomId}/seats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SeatDetailResponse>> getSeatsByCinemaRoom(@PathVariable Long cinemaRoomId) { // Thay SeatResponse bằng SeatDetailResponse
        return ResponseEntity.ok(cinemaRoomService.getSeatsByCinemaRoom(cinemaRoomId));
    }

    @PutMapping("/detail/{cinemaRoomId}/update-seat-type")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateSeatType(@PathVariable Long cinemaRoomId, @RequestBody UpdateSeatTypeRequest request) {
        return ResponseEntity.ok(cinemaRoomService.updateSeatType(cinemaRoomId, request));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addCinemaRoom(@RequestBody AddCinemaRoomRequest request) {
        return ResponseEntity.ok(cinemaRoomService.addCinemaRoom(request));
    }
}