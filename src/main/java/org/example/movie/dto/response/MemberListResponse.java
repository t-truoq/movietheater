package org.example.movie.dto.response;

import lombok.Data;

@Data
public class MemberListResponse {
    private Long memberId;
    private String fullName;
    private String identityCard;
    private String email;
    private String phoneNumber;
    private String address;
    private String ticketManagementLink;
    private String memberManagementLink;
    private String statisticLink;
    private String editLink;
}