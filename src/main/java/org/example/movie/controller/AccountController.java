package org.example.movie.controller;

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
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/public/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(accountService.authenticate(request));
    }

    @PostMapping("/public/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(accountService.register(request));
    }

    @GetMapping("/member/account")
    public ResponseEntity<MemberAccountResponse> getMemberAccount() {
        return ResponseEntity.ok(accountService.getCurrentUserAccount());
    }

    @PutMapping("/member/account")
    public ResponseEntity<MemberAccountResponse> updateMemberAccount(@Valid @RequestBody UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.updateCurrentUserAccount(request));
    }

    @GetMapping("/employee/account")
    public ResponseEntity<MemberAccountResponse> getEmployeeAccount() {
        return ResponseEntity.ok(accountService.getCurrentUserAccount());
    }

    @PutMapping("/employee/account")
    public ResponseEntity<MemberAccountResponse> updateEmployeeAccount(@Valid @RequestBody UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.updateCurrentUserAccount(request));
    }

    @GetMapping("/admin/members/{memberId}/account")
    public ResponseEntity<MemberAccountResponse> getMemberAccount(@PathVariable Long memberId) {
        return ResponseEntity.ok(accountService.getMemberAccount(memberId));
    }

    @PutMapping("/admin/members/{memberId}/account")
    public ResponseEntity<MemberAccountResponse> updateMemberAccount(@PathVariable Long memberId, @Valid @RequestBody UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.updateMemberAccount(memberId, request));
    }

    @PutMapping("/admin/accounts/{accountId}/role/{roleId}")
    public ResponseEntity<Void> changeRole(@PathVariable Long accountId, @PathVariable Long roleId) {
        accountService.changeRole(accountId, roleId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/public/accounts/{id}")
    public ResponseEntity<MemberAccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @GetMapping("/admin/members")
    public ResponseEntity<List<MemberListResponse>> getAllMembers() {
        return ResponseEntity.ok(accountService.getAllMembers());
    }


    @GetMapping("/member/booked-tickets")
    public ResponseEntity<List<BookedTicketResponse>> getBookedTickets() {
        return ResponseEntity.ok(accountService.getBookedTicketsForCurrentUser());
    }

    @GetMapping("/member/score-history")
    public ResponseEntity<List<ScoreHistoryResponse>> getScoreHistory(
            @RequestParam String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(accountService.getScoreHistory(type, fromDate, toDate));
    }
}