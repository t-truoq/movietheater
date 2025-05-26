package org.example.movie.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterResponse {
    private Long accountId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String identityCard;
    private String role;
}