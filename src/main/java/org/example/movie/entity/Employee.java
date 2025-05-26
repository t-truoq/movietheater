package org.example.movie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MOVIETHEATER_EMPLOYEES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @Column(name = "EMPLOYEE_ID", length = 10)
    private String employeeId;

    @OneToOne
    @JoinColumn(name = "ACCOUNT_ID", nullable = false)
    private Account account;

}