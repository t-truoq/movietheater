package org.example.movie.dto.response;

import lombok.Data;

@Data
public class EmployeeResponse {
    private String employeeId;
    private String fullName;
    private String identityCard;
    private String email;
    private String phoneNumber;
    private String address;
}