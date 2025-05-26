package org.example.movie.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberAccountResponse {
    private String username;
    private String fullName;
    private String password; // Chỉ hiển thị dạng mã hóa, không cho phép chỉnh sửa
    private LocalDate dateOfBirth;
    private String gender;
    private String email;
    private String identityCard;
    private String phoneNumber;
    private String address;
}