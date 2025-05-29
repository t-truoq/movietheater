package org.example.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.movie.dto.request.BookingSearchRequest;
import org.example.movie.dto.request.SelectSeatsRequest;
import org.example.movie.dto.request.TicketConfirmationRequest;
import org.example.movie.dto.response.BookingConfirmationResponse;
import org.example.movie.dto.response.BookingListResponse;
import org.example.movie.dto.response.TicketConfirmationResponse;
import org.example.movie.dto.response.TicketInfoResponse;
import org.example.movie.service.TicketBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/bookings")
@Tag(name = "Booking API for employee", description = "APIs for managing movie ticket bookings")
public class TicketBookingController {

    @Autowired
    private TicketBookingService ticketBookingService;

    @Operation(summary = "Confirm a booking", description = "Confirms a booking and optionally converts it to a ticket using member score with identity card or phone number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient score or member not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/confirm")
    public ResponseEntity<TicketInfoResponse> confirmBooking(
            @RequestBody TicketConfirmationRequest request,
            @RequestParam(required = false) String identityCard,
            @RequestParam(required = false) String phoneNumber) {
        TicketInfoResponse response = ticketBookingService.confirmBooking(request, identityCard, phoneNumber);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Search bookings", description = "Searches bookings by keyword (e.g., movie name, seat, identity card, phone number)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of bookings returned successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<List<BookingListResponse>> searchBookings(@ModelAttribute BookingSearchRequest request) {
        List<BookingListResponse> responses = ticketBookingService.searchBookings(request);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(summary = "Get all bookings", description = "Retrieves a list of all bookings for the authenticated member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of bookings returned successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<BookingListResponse>> getBookingList() {
        List<BookingListResponse> responses = ticketBookingService.getBookingList();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(summary = "Get ticket info", description = "Retrieves detailed ticket information for a specific booking ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket information returned successfully"),
            @ApiResponse(responseCode = "400", description = "Booking ID not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{bookingId}")
    public ResponseEntity<TicketInfoResponse> getTicketInfo(@PathVariable Long bookingId) {
        TicketInfoResponse response = ticketBookingService.getTicketInfo(bookingId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Select seats for booking", description = "Selects available seats for a movie schedule and creates a booking invoice")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seats selected and booking created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request, seat not found, or seat limit exceeded"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/select-seats")
    public ResponseEntity<TicketConfirmationResponse> selectSeats(@RequestBody SelectSeatsRequest request) {
        TicketConfirmationResponse response = ticketBookingService.selectSeats(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}