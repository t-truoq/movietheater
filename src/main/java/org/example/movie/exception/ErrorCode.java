
package org.example.movie.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    FORBIDDEN(401, "Access is forbidden"),
    SEAT_LIMIT_EXCEEDED(400, "Seat limit exceeded (0-8)"),
    INVOICE_ALREADY_CONFIRMED(400, "Invoice has already been confirmed"),
    INSUFFICIENT_SCORE(400, "Insufficient reward points"),

    INVALID_CREDENTIALS(1001, "Invalid username or password"),
    ACCOUNT_LOCKED(1002, "Account is locked"),
    USERNAME_EXISTS(1003, "Username already exists"),
    EMAIL_EXISTS(1004, "Email already exists"),
    PHONE_EXISTS(1005, "Phone number already exists"),
    ROLE_NOT_FOUND(1006, "Role not found"),
    ACCOUNT_NOT_FOUND(1007, "Account not found"),
    ROLE_ALREADY_EXISTS(1008, "Role already exists"),
    ROLE_IN_USE(1009, "Role is currently in use and cannot be deleted"),
    INVALID_USERNAME(1010, "Invalid username"),
    INVALID_PASSWORD(1011, "Invalid password"),
    INVALID_FULL_NAME(1012, "Invalid full name"),
    INVALID_EMAIL(1013, "Invalid email"),
    INVALID_PHONE(1014, "Invalid phone number"),
    INVALID_DATE_OF_BIRTH(1015, "Invalid date of birth"),
    EMPLOYEE_NOT_FOUND(1016, "Employee not found"),
    MOVIE_NOT_FOUND(1017, "Movie not found"),
    INVOICE_NOT_FOUND(1018, "Invoice not found"),
    MEMBER_NOT_FOUND(1019, "Member not found"),
    UNAUTHORIZED(1020, "Unauthorized access"),
    INVALID_CURRENT_PASSWORD(1021, "Current password is incorrect"),
    PASSWORD_MISMATCH(1022, "New password and confirmation password do not match"),
    SHOWTIME_NOT_FOUND(1023, "Showtime not found"),
    SEAT_NOT_FOUND(1024, "Seat not found"),
    SEAT_ALREADY_BOOKED(1025, "Seat is already booked"),
    INVALID_INVOICE_OWNER(1026, "Invoice does not belong to the user"),
    INVALID_PROMOTION(1027, "Invalid promotion code"),
    INVALID_TOTAL_AMOUNT(1028, "Invalid total amount"),
    IDENTITY_CARD_EXISTS(1029, "Identity card already exists"),
    CINEMA_ROOM_NOT_FOUND(1030, "Cinema room not found"),
    TYPE_NOT_FOUND(1031, "Type not found"),
    MOVIE_IN_USE(1032, "Movie is currently in use and cannot be deleted"),
    INVALID_MOVIE_DATA(1033, "Invalid movie data"),
    PROMOTION_NOT_FOUND(1034, "Promotion not found"),
    INVALID_REQUEST(1035, "Invalid request"),
    SCHEDULE_SEAT_NOT_FOUND(1036, "Schedule seat not found"),;

    private final int code;
    private final String message;
}