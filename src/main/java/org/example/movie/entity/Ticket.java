    package org.example.movie.entity;

    import jakarta.persistence.*;
    import lombok.*;

    import java.util.List;

    @Entity
    @Table(name = "MOVIETHEATER_TICKET")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Ticket {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "TICKET_ID")
        private Long ticketId;

        @Column(name = "PRICE")
        private Integer price;

        @Column(name = "TICKET_TYPE")
        private Integer ticketType;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "SCHEDULE_SEAT_ID", referencedColumnName = "SCHEDULE_SEAT_ID")
        private ScheduleSeat scheduleSeat;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "INVOICE_ID", referencedColumnName = "INVOICE_ID")
        private Invoice invoice;

    }