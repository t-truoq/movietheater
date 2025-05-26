package org.example.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.movie.dto.request.AddMovieRequest;
import org.example.movie.dto.request.SelectSeatsRequest;
import org.example.movie.dto.request.TicketConfirmationRequest;
import org.example.movie.dto.response.*;
import org.example.movie.entity.Movie;
import org.example.movie.exception.AppException;
import org.example.movie.exception.ErrorCode;
import org.example.movie.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@Tag(name = "Booking API", description = "APIs for managing movie ticket bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;


    @Operation(summary = "Get list of movies", description = "Retrieves a list of current movies, optionally filtered by search query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of movies returned successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/public/movies")
    public ResponseEntity<List<MovieResponse>> getMovies(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(bookingService.getMovies(q));
    }

    @Operation(summary = "Get available show dates for a movie", description = "Retrieves a list of available show dates for a specific movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of show dates returned successfully"),
            @ApiResponse(responseCode = "400", description = "Movie not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/public/show-dates")
    public ResponseEntity<List<LocalDate>> getShowDates(@RequestParam Long movieId) {
        return ResponseEntity.ok(bookingService.getAvailableShowDates(movieId));
    }

    @Operation(summary = "Get showtimes for a movie", description = "Retrieves showtimes for a specific movie, optionally filtered by date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of showtimes returned successfully"),
            @ApiResponse(responseCode = "400", description = "Movie not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/public/showtimes")
    public ResponseEntity<List<ShowtimeResponse>> getShowtimes(
            @RequestParam Long movieId,
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(bookingService.getShowtimes(movieId, date));
    }

    @Operation(summary = "Get showtimes for a movie on a specific date", description = "Retrieves showtimes for a specific movie on a given date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of showtimes returned successfully"),
            @ApiResponse(responseCode = "400", description = "Movie not found or invalid date format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/public/showtimes-by-date")
    public ResponseEntity<List<ShowtimeResponse>> getShowtimesByDate(
            @RequestParam Long movieId,
            @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            return ResponseEntity.ok(bookingService.getShowtimesByDate(movieId, localDate));
        } catch (DateTimeParseException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Invalid date format. Expected yyyy-MM-dd");
        }
    }

    @Operation(summary = "Get available seats (public)", description = "Retrieves the seat map for a specific showtime without authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seat map returned successfully"),
            @ApiResponse(responseCode = "400", description = "Showtime not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/public/seats")
    public ResponseEntity<List<SeatResponse>> getPublicSeats(@RequestParam Long scheduleId) {
        return ResponseEntity.ok(bookingService.getPublicSeats(scheduleId));
    }

    @Operation(summary = "Get available seats", description = "Retrieves the seat map for a specific showtime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seat map returned successfully"),
            @ApiResponse(responseCode = "400", description = "Showtime not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/member/seats")
    public ResponseEntity<List<SeatResponse>> getSeats(@RequestParam Long scheduleId) {
        return ResponseEntity.ok(bookingService.getSeats(scheduleId));
    }

    @Operation(summary = "Select seats", description = "Selects seats for a showtime and proceeds to confirmation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seat selection successful, returns invoice ID"),
            @ApiResponse(responseCode = "400", description = "Invalid seat selection"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/api/member/select-seats")
    public ResponseEntity<Long> selectSeats(@RequestBody SelectSeatsRequest request) {
        return ResponseEntity.ok(bookingService.selectSeats(request));
    }

    @Operation(summary = "Confirm a booking", description = "Confirms a booking and optionally applies promotions or member score")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient score"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/api/member/confirm-booking")
    public ResponseEntity<TicketConfirmationResponse> confirmBooking(@RequestBody TicketConfirmationRequest request) {
        return ResponseEntity.ok(bookingService.confirmBooking(request));
    }

    @Operation(summary = "Get ticket info", description = "Retrieves detailed ticket information for a specific invoice ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket information returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invoice ID not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/member/ticket-info")
    public ResponseEntity<TicketInfoResponse> getTicketInfo(@RequestParam Long invoiceId) {
        return ResponseEntity.ok(bookingService.getTicketInfo(invoiceId));
    }
}