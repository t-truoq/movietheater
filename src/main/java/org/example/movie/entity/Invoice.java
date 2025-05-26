package org.example.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.movie.enums.InvoiceStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "MOVIETHEATER_INVOICE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVOICE_ID", length = 10)

    private Long invoiceId;

    @Column(name = "ADD_SCORE")
    private Integer addScore;

    @Column(name = "BOOKING_DATE")
    private LocalDateTime bookingDate;

    @Column(name = "MOVIE_NAME", length = 255)
    private String movieName;

    @Column(name = "SCHEDULE_SHOW_TIME")
    private LocalDateTime scheduleShowTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private InvoiceStatus status;

    @Column(name = "TOTAL_MONEY")
    private Integer totalMoney;

    @Column(name = "USE_SCORE")
    private Integer useScore;

    @Column(name = "SEAT", length = 255)
    private String seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
    private Account account;
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets;
}