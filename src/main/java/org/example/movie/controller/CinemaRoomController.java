package org.example.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.movie.dto.request.AddCinemaRoomRequest;
import org.example.movie.dto.request.UpdateSeatTypeRequest;
import org.example.movie.dto.response.CinemaRoomResponse;
import org.example.movie.dto.response.SeatDetailResponse;
import org.example.movie.service.CinemaRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cinema-room")
@Tag(name = "Cinema Room Management", description = "APIs for managing cinema rooms and their seats, restricted to ADMIN role")
@SecurityRequirement(name = "bearerAuth") // Yêu cầu Bearer token cho tất cả endpoint
public class CinemaRoomController {

    @Autowired
    private CinemaRoomService cinemaRoomService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get list of cinema rooms", description = "Retrieves a list of cinema rooms, optionally filtered by a search keyword.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of cinema rooms retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CinemaRoomResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<CinemaRoomResponse>> getCinemaRoomList(
            @Parameter(description = "Optional search keyword to filter cinema rooms by name", example = "Room 1")
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(cinemaRoomService.getCinemaRoomList(search));
    }

    @GetMapping("/detail/{cinemaRoomId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get cinema room details", description = "Retrieves detailed information about a specific cinema room by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cinema room details retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CinemaRoomResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cinema room not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<CinemaRoomResponse> getCinemaRoomDetail(
            @Parameter(description = "ID of the cinema room to retrieve", example = "1")
            @PathVariable Long cinemaRoomId) {
        return ResponseEntity.ok(cinemaRoomService.getCinemaRoomDetail(cinemaRoomId));
    }

    @GetMapping("/detail/{cinemaRoomId}/seats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get seats by cinema room", description = "Retrieves a list of seats for a specific cinema room by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of seats retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SeatDetailResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cinema room not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<SeatDetailResponse>> getSeatsByCinemaRoom(
            @Parameter(description = "ID of the cinema room to retrieve seats", example = "1")
            @PathVariable Long cinemaRoomId) {
        return ResponseEntity.ok(cinemaRoomService.getSeatsByCinemaRoom(cinemaRoomId));
    }

    @PutMapping("/detail/{cinemaRoomId}/update-seat-type")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update seat type", description = "Updates the type (e.g., REGULAR, VIP) of seats in a specific cinema room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seat type updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body or seat type", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cinema room or seats not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<String> updateSeatType(
            @Parameter(description = "ID of the cinema room to update seat types", example = "1")
            @PathVariable Long cinemaRoomId,
            @Valid @RequestBody @Schema(description = "Details for updating seat type") UpdateSeatTypeRequest request) {
        return ResponseEntity.ok(cinemaRoomService.updateSeatType(cinemaRoomId, request));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add new cinema room", description = "Creates a new cinema room with the specified details and automatically generates seats.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cinema room added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<String> addCinemaRoom(
            @Valid @RequestBody @Schema(description = "Details for creating a new cinema room") AddCinemaRoomRequest request) {
        return ResponseEntity.ok(cinemaRoomService.addCinemaRoom(request));
    }
}