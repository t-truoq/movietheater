package org.example.movie.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String role;
    private Long accountId;
    private String fullName;
}