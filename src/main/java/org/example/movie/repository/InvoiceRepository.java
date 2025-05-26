package org.example.movie.repository;

import org.example.movie.entity.Invoice;
import org.example.movie.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByAccount_AccountIdAndStatus(Long accountId, InvoiceStatus status);
    List<Invoice> findByAccount_AccountIdAndUseScoreGreaterThanAndBookingDateBetween(Long accountId, Integer minScore, LocalDate fromDate, LocalDate toDate);
    List<Invoice> findByAccount_AccountIdAndUseScoreGreaterThan(Long accountId, Integer minScore);
    Optional<Invoice> findByInvoiceIdAndAccount_AccountId(Long invoiceId, Long accountId);
    @Query("SELECT i FROM Invoice i WHERE " +
            "LOWER(i.movieName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.seat) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.account.identityCard) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.account.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Invoice> findByKeyword(@Param("keyword") String keyword);
}