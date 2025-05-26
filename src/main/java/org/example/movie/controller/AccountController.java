package org.example.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.movie.dto.request.LoginRequest;
import org.example.movie.dto.request.RegisterRequest;
import org.example.movie.dto.request.UpdateAccountRequest;
import org.example.movie.dto.response.*;
import org.example.movie.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "Account Management", description = "APIs for managing user accounts, authentication, and member-related operations")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/public/login")
    @Operation(summary = "User login", description = "Authenticates a user with username and password, returning a JWT token upon success.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, returns JWT token and user details",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody @Schema(description = "Login credentials") LoginRequest request) {
        return ResponseEntity.ok(accountService.authenticate(request));
    }

    @PostMapping("/public/register")
    @Operation(summary = "User registration", description = "Registers a new user account with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful, returns user details",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body or duplicate username/email", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody @Schema(description = "User registration details") RegisterRequest request) {
        return ResponseEntity.ok(accountService.register(request));
    }

    @GetMapping("/member/account")
    @Operation(summary = "Get current member's account", description = "Retrieves the account details of the currently authenticated member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberAccountResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have member role", content = @Content)
    })
    public ResponseEntity<MemberAccountResponse> getMemberAccount() {
        return ResponseEntity.ok(accountService.getCurrentUserAccount());
    }

    @PutMapping("/member/account")
    @Operation(summary = "Update current member's account", description = "Updates the account details of the currently authenticated member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberAccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have member role", content = @Content)
    })
    public ResponseEntity<MemberAccountResponse> updateMemberAccount(
            @Valid @RequestBody @Schema(description = "Updated account details") UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.updateCurrentUserAccount(request));
    }

    @GetMapping("/employee/account")
    @Operation(summary = "Get current employee's account", description = "Retrieves the account details of the currently authenticated employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account details retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberAccountResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have employee role", content = @Content)
    })
    public ResponseEntity<MemberAccountResponse> getEmployeeAccount() {
        return ResponseEntity.ok(accountService.getCurrentUserAccount());
    }

    @PutMapping("/employee/account")
    @Operation(summary = "Update current employee's account", description = "Updates the account details of the currently authenticated employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberAccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have employee role", content = @Content)
    })
    public ResponseEntity<MemberAccountResponse> updateEmployeeAccount(
            @Valid @RequestBody @Schema(description = "Updated account details") UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.updateCurrentUserAccount(request));
    }

    @GetMapping("/admin/members/{memberId}/account")
    @Operation(summary = "Get member account by ID", description = "Retrieves the account details of a specific member by their ID. Requires admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member account retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberAccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have admin role", content = @Content)
    })
    public ResponseEntity<MemberAccountResponse> getMemberAccount(
            @Parameter(description = "ID of the member to retrieve") @PathVariable Long memberId) {
        return ResponseEntity.ok(accountService.getMemberAccount(memberId));
    }

    @PutMapping("/admin/members/{memberId}/account")
    @Operation(summary = "Update member account by ID", description = "Updates the account details of a specific member by their ID. Requires admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member account updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberAccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have admin role", content = @Content)
    })
    public ResponseEntity<MemberAccountResponse> updateMemberAccount(
            @Parameter(description = "ID of the member to update") @PathVariable Long memberId,
            @Valid @RequestBody @Schema(description = "Updated account details") UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.updateMemberAccount(memberId, request));
    }

    @PutMapping("/admin/accounts/{accountId}/role/{roleId}")
    @Operation(summary = "Change user role", description = "Changes the role of a specific account by their ID. Requires admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role changed successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account or role not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have admin role", content = @Content)
    })
    public ResponseEntity<Void> changeRole(
            @Parameter(description = "ID of the account to change role") @PathVariable Long accountId,
            @Parameter(description = "ID of the new role") @PathVariable Long roleId) {
        accountService.changeRole(accountId, roleId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/accounts/{id}")
    @Operation(summary = "Get account by ID", description = "Retrieves the account details of a specific user by their ID. Publicly accessible.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberAccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    public ResponseEntity<MemberAccountResponse> getAccount(
            @Parameter(description = "ID of the account to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @GetMapping("/admin/members")
    @Operation(summary = "Get all members", description = "Retrieves a list of all member accounts. Requires admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of members retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberListResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have admin role", content = @Content)
    })
    public ResponseEntity<List<MemberListResponse>> getAllMembers() {
        return ResponseEntity.ok(accountService.getAllMembers());
    }

    @GetMapping("/member/booked-tickets")
    @Operation(summary = "Get booked tickets for current user", description = "Retrieves a list of booked tickets for the currently authenticated member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of booked tickets retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookedTicketResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have member role", content = @Content)
    })
    public ResponseEntity<List<BookedTicketResponse>> getBookedTickets() {
        return ResponseEntity.ok(accountService.getBookedTicketsForCurrentUser());
    }

    @GetMapping("/member/score-history")
    @Operation(summary = "Get score history for current user", description = "Retrieves the score history (e.g., points earned or used) for the currently authenticated member, filtered by type and date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Score history retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScoreHistoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid type or date range", content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have member role", content = @Content)
    })
    public ResponseEntity<List<ScoreHistoryResponse>> getScoreHistory(
            @Parameter(description = "Type of score history (e.g., 'earned', 'used')") @RequestParam String type,
            @Parameter(description = "Start date of the history (optional)", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "End date of the history (optional)", example = "2025-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(accountService.getScoreHistory(type, fromDate, toDate));
    }
}