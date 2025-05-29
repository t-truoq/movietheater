package org.example.movie.service;

import org.example.movie.dto.request.BookingSearchRequest;
import org.example.movie.dto.request.SelectSeatsRequest;
import org.example.movie.dto.request.TicketConfirmationRequest;
import org.example.movie.dto.response.BookingListResponse;
import org.example.movie.dto.response.TicketConfirmationResponse;
import org.example.movie.dto.response.TicketInfoResponse;
import org.example.movie.entity.*;
import org.example.movie.enums.InvoiceStatus;
import org.example.movie.enums.SeatStatus;
import org.example.movie.exception.AppException;
import org.example.movie.exception.ErrorCode;
import org.example.movie.mapper.TicketMapper;
import org.example.movie.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketBookingService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MovieScheduleRepository movieScheduleRepository;

    @Autowired
    private ScheduleSeatRepository scheduleSeatRepository;

    @Autowired
    private MovieDateRepository movieDateRepository;

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
    public TicketConfirmationResponse selectSeats(SelectSeatsRequest request) {
        // Tìm lịch chiếu
        MovieSchedule movieSchedule = movieScheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));

        // Tìm danh sách ghế
        List<ScheduleSeat> seats = scheduleSeatRepository.findAllById(request.getScheduleSeatIds());
        if (seats.isEmpty()) {
            throw new AppException(ErrorCode.SEAT_NOT_FOUND);
        }
        if (seats.size() > 8) {
            throw new AppException(ErrorCode.SEAT_LIMIT_EXCEEDED);
        }

        // Kiểm tra và cập nhật trạng thái ghế
        for (ScheduleSeat seat : seats) {
            if (seat.getSeatStatus() == SeatStatus.BOOKED) {
                throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
            }
            seat.setSeatStatus(SeatStatus.BOOKED);
            scheduleSeatRepository.save(seat);
        }

        // Tạo chuỗi ghế
        String seatString = seats.stream()
                .map(seat -> seat.getSeatColumn() + seat.getSeatRow())
                .collect(Collectors.joining(","));

        // Tìm ngày chiếu
        MovieDate movieDate = movieDateRepository.findByMovie_MovieId(movieSchedule.getMovie().getMovieId())
                .stream().findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));

        // Xử lý thời gian chiếu
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm")
                .optionalStart()
                .appendPattern(":ss")
                .optionalEnd()
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();

        LocalDate showDate = movieDate.getShowDate().getShowDate();
        LocalDateTime scheduleTime = LocalDateTime.parse(movieSchedule.getSchedule().getScheduleTime(), formatter);
        LocalDateTime scheduleShowTime = showDate.atTime(scheduleTime.toLocalTime());

        // Tạo invoice
        Invoice invoice = Invoice.builder()
                .movieName(movieSchedule.getMovie().getMovieNameVn())
                .bookingDate(LocalDateTime.now())
                .scheduleShowTime(scheduleShowTime)
                .totalMoney(100000 * seats.size())
                .useScore(0)
                .addScore(0)
                .status(InvoiceStatus.PENDING)
                .seat(seatString)
                .build();

        invoice = invoiceRepository.save(invoice);

        // Tạo response
        TicketConfirmationResponse response = new TicketConfirmationResponse();
        response.setInvoiceId(invoice.getInvoiceId());
        response.setMovieName(invoice.getMovieName());
        response.setScheduleShowDate(scheduleShowTime.toLocalDate());
        response.setScheduleShowTime(scheduleShowTime.toLocalTime().toString());
        response.setSeatNumbers(Arrays.asList(seatString.split(",")));
        response.setTotalPrice(invoice.getTotalMoney());

        return response;
    }

    @Transactional
    @CacheEvict(value = {"bookingsCache", "bookingListCache", "ticketInfoCache"}, allEntries = true)
    public TicketInfoResponse confirmBooking(TicketConfirmationRequest request, String identityCard, String phoneNumber) {
        // Tìm invoice
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));

        // Kiểm tra Member
        Member member = checkMember(identityCard, phoneNumber);

        // Gán đầy đủ Account vào Invoice
        invoice.setAccount(member.getAccount());

        // Xử lý điểm số nếu có
        if (request.getUseScore() != null && request.getUseScore() > 0) {
            if (member.getScore() < request.getUseScore()) {
                throw new AppException(ErrorCode.INSUFFICIENT_SCORE);
            }
            int ticketsConverted = request.getUseScore() / 100;
            member.setScore(member.getScore() - request.getUseScore());
            invoice.setUseScore(request.getUseScore());
            invoice.setStatus(InvoiceStatus.PENDING); // Cập nhật trạng thái khi xác nhận
            memberRepository.save(member);
        } else {
            invoice.setStatus(InvoiceStatus.PENDING); // Cập nhật trạng thái khi xác nhận
        }

        // Lưu invoice
        invoiceRepository.save(invoice);

        // Lấy cinemaRoomName từ ScheduleSeat qua scheduleId
        ScheduleSeat scheduleSeat = scheduleSeatRepository.findFirstBySchedule_ScheduleId(request.getScheduleId())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_SEAT_NOT_FOUND));
        String cinemaRoomName = (scheduleSeat.getSeat() != null &&
                scheduleSeat.getSeat().getCinemaRoom() != null)
                ? scheduleSeat.getSeat().getCinemaRoom().getCinemaRoomName()
                : "Room Not Found";

        // Tạo response bằng TicketMapper
        TicketInfoResponse response = ticketMapper.toInfoResponse(invoice);
        response.setCinemaRoomName(cinemaRoomName);

        return response;
    }


    @Transactional(readOnly = true)
    public Member checkMember(String identityCard, String phoneNumber) {
        if (identityCard == null && phoneNumber == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        Member member = null;
        if (identityCard != null) {
            member = memberRepository.findByIdentityCard(identityCard)
                    .orElse(null);
        }
        if (member == null && phoneNumber != null) {
            member = memberRepository.findByPhoneNumber(phoneNumber)
                    .orElse(null);
        }
        if (member == null) {
            throw new AppException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return member;
    }

    @Cacheable(value = "ticketInfoCache", key = "#bookingId")
    public TicketInfoResponse getTicketInfo(Long bookingId) {
        Invoice invoice = invoiceRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));
        return ticketMapper.toInfoResponse(invoice);
    }
}