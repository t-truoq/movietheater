package org.example.movie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MOVIETHEATER_MEMBER")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "SCORE")
    private Integer score;

    @OneToOne
    @JoinColumn(name = "ACCOUNT_ID", nullable = false, referencedColumnName = "ACCOUNT_ID")
    private Account account;
}