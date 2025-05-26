package org.example.movie.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AddEmployeeRequest {
    private String image;
    private String username;
    private String password;
    private String confirmPassword;
    private LocalDate dateOfBirth;
    private String gender;
    private String fullName;
    private String identityCard;
    private String email;
    private String phoneNumber;
    private String address;
}