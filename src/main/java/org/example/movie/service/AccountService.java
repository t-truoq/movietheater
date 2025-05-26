package org.example.movie.service;

import org.example.movie.configuration.JwtUtil;
import org.example.movie.dto.request.LoginRequest;
import org.example.movie.dto.request.RegisterRequest;
import org.example.movie.dto.request.UpdateAccountRequest;
import org.example.movie.dto.response.*;
import org.example.movie.entity.Account;
import org.example.movie.entity.Invoice;
import org.example.movie.entity.Member;
import org.example.movie.entity.Role;
import org.example.movie.enums.AccountStatus;
import org.example.movie.enums.InvoiceStatus;
import org.example.movie.enums.SeatStatus;
import org.example.movie.exception.AppException;
import org.example.movie.exception.ErrorCode;
import org.example.movie.mapper.AccountMapper;
import org.example.movie.repository.AccountRepository;
import org.example.movie.repository.InvoiceRepository;
import org.example.movie.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AccountService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AccountMapper accountMapper;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logger.error("Authentication is null in SecurityContextHolder");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        logger.info("Authentication found - IsAuthenticated: " + authentication.isAuthenticated() + ", Principal: " + authentication.getPrincipal() + ", Credentials: " + authentication.getCredentials());

        if (!authentication.isAuthenticated()) {
            logger.error("Authentication is not authenticated");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Object credentials = authentication.getCredentials();
        if (credentials == null || !(credentials instanceof String)) {
            logger.error("Credentials not found or not a String: " + credentials);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String token = (String) credentials;
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Long accountId = jwtUtil.getAccountIdFromToken(token);
            logger.info("Successfully retrieved accountId: " + accountId);
            return accountId;
        } catch (Exception e) {
            logger.error("Failed to extract accountId from token: " + e.getMessage());
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        Account account = accountRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), account.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (account.getStatus() == AccountStatus.LOCKED) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        String token = jwtUtil.generateToken(account.getUsername(), account.getRole().getRoleName(), account.getAccountId());
        LoginResponse response = accountMapper.toLoginResponse(account);
        response.setToken(token);
        return response;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.USERNAME_EXISTS);
        }
        if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty() &&
                accountRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new AppException(ErrorCode.PHONE_EXISTS);
        }
        // Thêm đoạn kiểm tra identityCard
        if (request.getIdentityCard() != null && !request.getIdentityCard().trim().isEmpty() &&
                accountRepository.findByIdentityCard(request.getIdentityCard()).isPresent()) {
            throw new AppException(ErrorCode.IDENTITY_CARD_EXISTS);
        }

        Role memberRole = roleService.getOrCreateDefaultRole("MEMBER");

        Account account = accountMapper.toAccount(request);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setStatus(AccountStatus.ACTIVE);
        account.setRole(memberRole);
        account.setRegisterDate(LocalDateTime.now().toLocalDate());

        account = accountRepository.save(account);

        Member member = new Member();
        member.setAccount(account);
        member.setScore(0);
        memberRepository.save(member);

        return accountMapper.toRegisterResponse(account);
    }


    public MemberAccountResponse getCurrentUserAccount() {
        Long accountId = getCurrentUserId();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        return accountMapper.toMemberAccountResponse(account);
    }

    public MemberAccountResponse updateCurrentUserAccount(UpdateAccountRequest request) {
        Long accountId = getCurrentUserId();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        // Capture accountId before any modifications to account
        Long currentAccountId = account.getAccountId();

        if (accountRepository.findByEmail(request.getEmail()).filter(a -> !a.getAccountId().equals(currentAccountId)).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty() &&
                accountRepository.findByPhoneNumber(request.getPhoneNumber()).filter(a -> !a.getAccountId().equals(currentAccountId)).isPresent()) {
            throw new AppException(ErrorCode.PHONE_EXISTS);
        }

        accountMapper.updateAccountFromRequest(account, request);

        if (request.getCurrentPassword() != null && request.getNewPassword() != null && request.getConfirmPassword() != null) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
                throw new AppException(ErrorCode.INVALID_CURRENT_PASSWORD);
            }
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new AppException(ErrorCode.PASSWORD_MISMATCH);
            }
            account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        account = accountRepository.save(account);
        return accountMapper.toMemberAccountResponse(account);
    }

    public MemberAccountResponse updateMemberAccount(Long memberId, UpdateAccountRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        Account account = member.getAccount();
        // Capture accountId before any modifications to account
        Long currentAccountId = account.getAccountId();

        if (accountRepository.findByEmail(request.getEmail()).filter(a -> !a.getAccountId().equals(currentAccountId)).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty() &&
                accountRepository.findByPhoneNumber(request.getPhoneNumber()).filter(a -> !a.getAccountId().equals(currentAccountId)).isPresent()) {
            throw new AppException(ErrorCode.PHONE_EXISTS);
        }

        accountMapper.updateAccountFromRequest(account, request);

        if (request.getCurrentPassword() != null && request.getNewPassword() != null && request.getConfirmPassword() != null) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
                throw new AppException(ErrorCode.INVALID_CURRENT_PASSWORD);
            }
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new AppException(ErrorCode.PASSWORD_MISMATCH);
            }
            account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        account = accountRepository.save(account);
        return accountMapper.toMemberAccountResponse(account);
    }

    public MemberAccountResponse getMemberAccount(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        return accountMapper.toMemberAccountResponse(member.getAccount());
    }

    public void changeRole(Long accountId, Long roleId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Role role = roleService.findById(roleId);
        account.setRole(role);
        accountRepository.save(account);
    }

    public MemberAccountResponse findById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        return accountMapper.toMemberAccountResponse(account);
    }

    public List<MemberListResponse> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(member -> {
                    Account account = member.getAccount();
                    MemberListResponse response = new MemberListResponse();
                    response.setMemberId(Long.valueOf(member.getMemberId()));
                    response.setFullName(account.getFullName());
                    response.setIdentityCard(account.getIdentityCard());
                    response.setEmail(account.getEmail());
                    response.setPhoneNumber(account.getPhoneNumber());
                    response.setAddress(account.getAddress());
                    response.setTicketManagementLink("/api/admin/members/" + member.getMemberId() + "/tickets");
                    response.setMemberManagementLink("/api/admin/members/" + member.getMemberId() + "/account");
                    response.setStatisticLink("/api/admin/members/" + member.getMemberId() + "/statistics");
                    response.setEditLink("/api/admin/members/" + member.getMemberId() + "/account");
                    return response;
                })
                .collect(Collectors.toList());
    }


    public List<BookedTicketResponse> getBookedTicketsForCurrentUser() {
        Long accountId = getCurrentUserId();
        List<Invoice> invoices = invoiceRepository.findByAccount_AccountIdAndStatus(accountId, InvoiceStatus.PENDING); // Giả định 1 là trạng thái "đã đặt"
        return invoices.stream()
                .map(invoice -> {
                    BookedTicketResponse response = new BookedTicketResponse();
                    response.setMovieName(invoice.getMovieName());
                    response.setBookingDate(invoice.getBookingDate().toLocalDate());
                    response.setTotalAmount(invoice.getTotalMoney());
                    response.setStatus(invoice.getStatus().ordinal());
                    response.setInvoiceId(Long.valueOf(invoice.getInvoiceId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<ScoreHistoryResponse> getScoreHistory(String type, LocalDate fromDate, LocalDate toDate) {
        Long accountId = getCurrentUserId();
        if ("using".equals(type)) {
            List<Invoice> invoices;
            if (fromDate != null && toDate != null) {
                invoices = invoiceRepository.findByAccount_AccountIdAndUseScoreGreaterThanAndBookingDateBetween(accountId, 0, fromDate, toDate);
            } else {
                invoices = invoiceRepository.findByAccount_AccountIdAndUseScoreGreaterThan(accountId, 0);
            }
            return invoices.stream()
                    .map(invoice -> {
                        ScoreHistoryResponse response = new ScoreHistoryResponse();
                        response.setDate(invoice.getBookingDate().toLocalDate());
                        response.setMovieName(invoice.getMovieName());
                        response.setAmount(-invoice.getUseScore());
                        response.setType("Using");
                        return response;
                    })
                    .collect(Collectors.toList());
        } else if ("adding".equals(type)) {
            // Chưa triển khai do thiếu thực thể ScoreTransaction
            return Collections.emptyList();
        } else {
            throw new IllegalArgumentException("Loại không hợp lệ: " + type);
        }
    }
}