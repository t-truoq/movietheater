package org.example.movie.service;

import org.example.movie.dto.request.BookingConfirmationRequest;
import org.example.movie.dto.request.BookingSearchRequest;
import org.example.movie.dto.response.BookingConfirmationResponse;
import org.example.movie.dto.response.BookingListResponse;
import org.example.movie.dto.response.TicketInfoResponse;
import org.example.movie.entity.Invoice;
import org.example.movie.entity.Member;
import org.example.movie.enums.InvoiceStatus;
import org.example.movie.exception.AppException;
import org.example.movie.exception.ErrorCode;
import org.example.movie.mapper.TicketMapper;
import org.example.movie.repository.InvoiceRepository;
import org.example.movie.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketBookingService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TicketMapper ticketMapper;

    @Cacheable(value = "bookingsCache", key = "#request.keyword")
    public List<BookingListResponse> searchBookings(BookingSearchRequest request) {
        List<Invoice> invoices = invoiceRepository.findByKeyword(request.getKeyword());
        return invoices.stream().map(invoice -> {
            BookingListResponse response = new BookingListResponse();
            response.setBookingId(invoice.getInvoiceId());
            response.setIdentityCard(invoice.getAccount().getIdentityCard());
            response.setPhoneNumber(invoice.getAccount().getPhoneNumber());
            response.setMovie(invoice.getMovieName());
            response.setTime(invoice.getScheduleShowTime().toString());
            return response;
        }).collect(Collectors.toList());
    }

    @Cacheable(value = "bookingListCache")
    public List<BookingListResponse> getBookingList() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream().map(invoice -> {
            BookingListResponse response = new BookingListResponse();
            response.setBookingId(invoice.getInvoiceId());
            response.setIdentityCard(invoice.getAccount().getIdentityCard());
            response.setPhoneNumber(invoice.getAccount().getPhoneNumber());
            response.setMovie(invoice.getMovieName());
            response.setTime(invoice.getScheduleShowTime().toString());
            return response;
        }).collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"bookingsCache", "bookingListCache", "ticketInfoCache"}, allEntries = true)
    public BookingConfirmationResponse confirmBooking(BookingConfirmationRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getBookingId())
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));
        Member member = memberRepository.findByAccount_AccountId(invoice.getAccount().getAccountId())
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        if (request.getConvertToTicket() != null && request.getConvertToTicket()) {
            if (request.getUseScore() != null && request.getUseScore() > 0) {
                if (member.getScore() < request.getUseScore()) {
                    throw new AppException(ErrorCode.INSUFFICIENT_SCORE);
                }
                int ticketsConverted = request.getUseScore() / 100;
                member.setScore(member.getScore() - request.getUseScore());
                invoice.setUseScore(request.getUseScore());
                invoice.setStatus(InvoiceStatus.PENDING);
                memberRepository.save(member);
                invoiceRepository.save(invoice);
            } else {
                throw new AppException(ErrorCode.INSUFFICIENT_SCORE);
            }
        } else {
            invoice.setStatus(InvoiceStatus.PENDING);
            invoiceRepository.save(invoice);
        }

        return ticketMapper.toBookingConfirmationResponse(invoice);
    }

    @Cacheable(value = "ticketInfoCache", key = "#bookingId")
    public TicketInfoResponse getTicketInfo(Long bookingId) {
        Invoice invoice = invoiceRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));
        return ticketMapper.toInfoResponse(invoice);
    }
}