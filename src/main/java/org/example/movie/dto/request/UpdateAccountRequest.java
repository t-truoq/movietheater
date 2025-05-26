package org.example.movie.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class UpdateAccountRequest {
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    private String address;

    @PastOrPresent(message = "Date of birth must be in the past or present")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    private String identityCard;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @Size(min = 8, max = 50, message = "Current password must be between 8 and 50 characters")
    private String currentPassword;

    @Size(min = 8, max = 50, message = "New password must be between 8 and 50 characters")
    private String newPassword;

    @Size(min = 8, max = 50, message = "Confirm password must be between 8 and 50 characters")
    private String confirmPassword;
}