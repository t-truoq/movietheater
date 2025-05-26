package org.example.movie.service;

import jakarta.transaction.Transactional;
import org.example.movie.configuration.JwtUtil;
import org.example.movie.dto.request.AddMovieRequest;
import org.example.movie.dto.request.BookingSearchRequest;
import org.example.movie.dto.request.SelectSeatsRequest;
import org.example.movie.dto.request.TicketConfirmationRequest;
import org.example.movie.dto.response.*;
import org.example.movie.entity.*;
import org.example.movie.enums.InvoiceStatus;
import org.example.movie.enums.SeatStatus;
import org.example.movie.exception.AppException;
import org.example.movie.exception.ErrorCode;
import org.example.movie.mapper.MovieMapper;
import org.example.movie.mapper.ScheduleSeatMapper;
import org.example.movie.mapper.TicketMapper;
import org.example.movie.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieDateRepository movieDateRepository;

    @Autowired
    private MovieScheduleRepository movieScheduleRepository;

    @Autowired
    private ScheduleSeatRepository scheduleSeatRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ScheduleSeatMapper scheduleSeatMapper;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ShowDateRepository showDateRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private CinemaRoomRepository cinemaRoomRepository;

    @Autowired
    private MovieTypeRepository movieTypeRepository;

    @Autowired
    private MovieMapper movieMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            LOGGER.error("Authentication is null in SecurityContextHolder");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        LOGGER.debug("Authentication found - IsAuthenticated: {}, Principal: {}, Credentials: {}",
                authentication.isAuthenticated(), authentication.getPrincipal(), authentication.getCredentials());

        if (!authentication.isAuthenticated()) {
            LOGGER.error("Authentication is not authenticated");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Object credentials = authentication.getCredentials();
        if (credentials == null || !(credentials instanceof String)) {
            LOGGER.error("Credentials not found or not a String: {}", credentials);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String token = (String) credentials;
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Long accountId = jwtUtil.getAccountIdFromToken(token);
            LOGGER.info("Successfully retrieved accountId: {}", accountId);
            return accountId;
        } catch (Exception e) {
            LOGGER.error("Failed to extract accountId from token: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Transactional
    public Movie addMovie(AddMovieRequest request) {
        if (request.getMovieNameVn() == null || request.getMovieNameVn().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Movie name (VN) is required");
        }
        if (request.getFromDate() == null || request.getToDate() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "From date and to date are required");
        }
        if (request.getFromDate().isAfter(request.getToDate())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "From date must be before to date");
        }
        if (request.getScheduleTimes() == null || request.getScheduleTimes().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "At least one schedule time is required");
        }

        Movie movie = new Movie();
        movie.setMovieNameVn(request.getMovieNameVn());
        movie.setMovieNameEnglish(request.getMovieNameEnglish());
        movie.setFromDate(request.getFromDate());
        movie.setToDate(request.getToDate());
        movie.setActor(request.getActor());
        movie.setMovieProductionCompany(request.getMovieProductionCompany());
        movie.setDirector(request.getDirector());
        movie.setDuration(request.getDuration());
        movie.setVersion(request.getVersion());
        movie.setContent(request.getContent());
        movie.setLargeImage(request.getLargeImage());
        CinemaRoom cinemaRoom = cinemaRoomRepository.findById(request.getCinemaRoom())
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_ROOM_NOT_FOUND));
        movie.setCinemaRoom(cinemaRoom);
        movie = movieRepository.save(movie);

        if (request.getTypeIds() != null && !request.getTypeIds().isEmpty()) {
            for (Long typeId : request.getTypeIds()) {
                Type type = typeRepository.findById(typeId)
                        .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));
                MovieType movieType = MovieType.builder()
                        .movie(movie)
                        .type(type)
                        .build();
                movieTypeRepository.save(movieType);
            }
        }

        LocalDate currentDate = request.getFromDate();
        while (!currentDate.isAfter(request.getToDate())) {
            ShowDate showDate = ShowDate.builder()
                    .showDate(currentDate)
                    .build();
            showDate = showDateRepository.save(showDate);

            MovieDate movieDate = MovieDate.builder()
                    .movie(movie)
                    .showDate(showDate)
                    .build();
            movieDateRepository.save(movieDate);

            currentDate = currentDate.plusDays(1);
        }

        // Định dạng chuẩn cho scheduleTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        for (LocalDateTime scheduleTime : request.getScheduleTimes()) {
            LocalDate scheduleDate = scheduleTime.toLocalDate();
            if (scheduleDate.isBefore(request.getFromDate()) || scheduleDate.isAfter(request.getToDate())) {
                LOGGER.warn("Schedule time {} is outside the movie date range ({} to {}), skipping",
                        scheduleTime, request.getFromDate(), request.getToDate());
                continue;
            }

            // Định dạng scheduleTime trước khi lưu
            String formattedScheduleTime = scheduleTime.format(formatter);
            Schedule schedule = Schedule.builder()
                    .scheduleTime(formattedScheduleTime)
                    .build();
            schedule = scheduleRepository.save(schedule);

            MovieSchedule movieSchedule = MovieSchedule.builder()
                    .movie(movie)
                    .schedule(schedule)
                    .build();
            movieScheduleRepository.save(movieSchedule);
        }

        return movie;
    }

    public List<MovieResponse> getMovies(String query) {
        List<Movie> movies = query == null || query.isEmpty()
                ? movieRepository.findAll()
                : movieRepository.findByMovieNameVnContainingIgnoreCase(query);
        return movies.stream()
                .map(movieMapper::toResponse)
                .toList();
    }

    public List<ShowtimeResponse> getShowtimes(Long movieId, String date) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        List<MovieDate> movieDates = movieDateRepository.findByMovie_MovieId(movieId);
        List<MovieSchedule> movieSchedules = movieScheduleRepository.findByMovie_MovieId(movieId);

        // Tạo DateTimeFormatter hỗ trợ định dạng không có giây
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm")
                .optionalStart()
                .appendPattern(":ss")
                .optionalEnd()
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();

        return movieSchedules.stream()
                .filter(schedule -> {
                    try {
                        LocalDateTime scheduleTime = LocalDateTime.parse(schedule.getSchedule().getScheduleTime(), formatter);
                        LocalDate scheduleDate = scheduleTime.toLocalDate();
                        if (date != null && !date.isEmpty()) {
                            LocalDate filterDate = LocalDate.parse(date);
                            return scheduleDate.equals(filterDate);
                        }
                        return true;
                    } catch (DateTimeParseException e) {
                        LOGGER.error("Failed to parse scheduleTime: {} for scheduleId: {}",
                                schedule.getSchedule().getScheduleTime(), schedule.getId(), e);
                        throw new AppException(ErrorCode.INVALID_REQUEST, "Invalid schedule time format");
                    }
                })
                .flatMap(schedule -> movieDates.stream()
                        .filter(dateEntry -> {
                            LocalDateTime scheduleTime = LocalDateTime.parse(schedule.getSchedule().getScheduleTime(), formatter);
                            return dateEntry.getShowDate().getShowDate().equals(scheduleTime.toLocalDate());
                        })
                        .map(dateEntry -> {
                            ShowtimeResponse response = new ShowtimeResponse();
                            response.setScheduleId(schedule.getId());
                            response.setShowDate(dateEntry.getShowDate().getShowDate());
                            LocalDateTime scheduleTime = LocalDateTime.parse(schedule.getSchedule().getScheduleTime(), formatter);
                            response.setShowTime(scheduleTime.toLocalTime());
                            response.setCinemaRoomName(movie.getCinemaRoom().getCinemaRoomName());
                            return response;
                        }))
                .toList();
    }

    public List<LocalDate> getAvailableShowDates(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        List<MovieDate> movieDates = movieDateRepository.findByMovie_MovieId(movieId);
        return movieDates.stream()
                .map(movieDate -> movieDate.getShowDate().getShowDate())
                .distinct()
                .sorted()
                .toList();
    }

    public List<ShowtimeResponse> getShowtimesByDate(Long movieId, LocalDate date) {
        LOGGER.info("Fetching showtimes for movieId: {} and date: {}", movieId, date);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        List<MovieSchedule> movieSchedules = movieScheduleRepository.findByMovie_MovieId(movieId);
        LOGGER.debug("Found {} schedules for movieId: {}", movieSchedules.size(), movieId);

        // Tạo DateTimeFormatter hỗ trợ định dạng không có giây
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm")
                .optionalStart()
                .appendPattern(":ss")
                .optionalEnd()
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();

        return movieSchedules.stream()
                .filter(schedule -> {
                    try {
                        LocalDateTime scheduleTime = LocalDateTime.parse(schedule.getSchedule().getScheduleTime(), formatter);
                        LOGGER.debug("Parsed scheduleTime: {} for scheduleId: {}", scheduleTime, schedule.getId());
                        return scheduleTime.toLocalDate().equals(date);
                    } catch (DateTimeParseException e) {
                        LOGGER.error("Failed to parse scheduleTime: {} for scheduleId: {}",
                                schedule.getSchedule().getScheduleTime(), schedule.getId(), e);
                        throw new AppException(ErrorCode.INVALID_REQUEST, "Invalid schedule time format");
                    }
                })
                .map(schedule -> {
                    ShowtimeResponse response = new ShowtimeResponse();
                    response.setScheduleId(schedule.getId());
                    response.setShowDate(date);
                    LocalDateTime scheduleTime = LocalDateTime.parse(schedule.getSchedule().getScheduleTime(), formatter);
                    response.setShowTime(scheduleTime.toLocalTime());
                    response.setCinemaRoomName(movie.getCinemaRoom().getCinemaRoomName());
                    return response;
                })
                .toList();
    }

    public List<SeatResponse> getSeats(Long scheduleId) {
        MovieSchedule movieSchedule = movieScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));
        List<ScheduleSeat> seats = scheduleSeatRepository.findBySchedule_ScheduleIdAndMovie_MovieId(
                movieSchedule.getSchedule().getScheduleId(),
                movieSchedule.getMovie().getMovieId());
        if (seats.isEmpty()) {
            throw new AppException(ErrorCode.SEAT_NOT_FOUND);
        }
        return seats.stream()
                .map(scheduleSeatMapper::toSeatResponse)
                .toList();
    }

    public List<SeatResponse> getPublicSeats(Long scheduleId) {
        return getSeats(scheduleId);
    }

    @Transactional
    public Long selectSeats(SelectSeatsRequest request) {
        MovieSchedule movieSchedule = movieScheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));
        List<ScheduleSeat> seats = scheduleSeatRepository.findAllById(request.getScheduleSeatIds());
        if (seats.isEmpty()) {
            throw new AppException(ErrorCode.SEAT_NOT_FOUND);
        }
        if (seats.size() > 8) {
            throw new AppException(ErrorCode.SEAT_LIMIT_EXCEEDED);
        }
        for (ScheduleSeat seat : seats) {
            if (seat.getSeatStatus() == SeatStatus.BOOKED) {
                throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
            }
            seat.setSeatStatus(SeatStatus.BOOKED);
            scheduleSeatRepository.save(seat);
        }
        String seatString = seats.stream()
                .map(seat -> seat.getSeatColumn() + seat.getSeatRow())
                .collect(Collectors.joining(","));
        MovieDate movieDate = movieDateRepository.findByMovie_MovieId(movieSchedule.getMovie().getMovieId())
                .stream().findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));

        // Tạo DateTimeFormatter hỗ trợ định dạng không có giây
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

        Invoice invoice = Invoice.builder()
                .account(Account.builder().accountId(getCurrentUserId()).build())
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
        return invoice.getInvoiceId();
    }

    @Transactional
    public TicketConfirmationResponse confirmBooking(TicketConfirmationRequest request) {
        Invoice invoice = invoiceRepository.findByInvoiceIdAndAccount_AccountId(request.getInvoiceId(), getCurrentUserId())
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new AppException(ErrorCode.INVOICE_ALREADY_CONFIRMED);
        }
        List<ScheduleSeat> seats = scheduleSeatRepository.findBySchedule_ScheduleIdAndMovie_MovieId(
                movieScheduleRepository.findById(request.getScheduleId())
                        .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND))
                        .getSchedule().getScheduleId(),
                movieScheduleRepository.findById(request.getScheduleId())
                        .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND))
                        .getMovie().getMovieId());
        for (String seat : invoice.getSeat().split(",")) {
            boolean seatExists = seats.stream()
                    .anyMatch(s -> (s.getSeatColumn() + s.getSeatRow()).equals(seat));
            if (!seatExists || seats.stream().filter(s -> (s.getSeatColumn() + s.getSeatRow()).equals(seat))
                    .findFirst().get().getSeatStatus() != SeatStatus.BOOKED) {
                throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
            }
        }
        int totalMoney = invoice.getTotalMoney();
        int useScore = 0;
        if (request.getUseScore() != null && request.getUseScore() > 0) {
            Member member = memberRepository.findByAccount_AccountId(getCurrentUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
            if (member.getScore() < request.getUseScore()) {
                throw new AppException(ErrorCode.INSUFFICIENT_SCORE);
            }
            useScore = request.getUseScore();
            totalMoney = totalMoney - useScore * 10000;
            member.setScore(member.getScore() - useScore);
            memberRepository.save(member);
        }
        if (request.getPromotionId() != null) {
            Promotion promotion = promotionRepository.findByPromotionIdAndEndTimeAfter(
                            request.getPromotionId(), LocalDateTime.now())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_PROMOTION));
            totalMoney = totalMoney * (100 - promotion.getDiscountLevel()) / 100;
        }
        invoice.setTotalMoney(totalMoney);
        invoice.setUseScore(useScore);
        invoice.setAddScore(totalMoney / 10000);
        invoice.setStatus(InvoiceStatus.PAID);
        invoice = invoiceRepository.save(invoice);
        return ticketMapper.toConfirmationResponse(invoice);
    }

    public TicketInfoResponse getTicketInfo(Long invoiceId) {
        Invoice invoice = invoiceRepository.findByInvoiceIdAndAccount_AccountId(invoiceId, getCurrentUserId())
                .orElseThrow(() -> new AppException(ErrorCode.INVOICE_NOT_FOUND));
        return ticketMapper.toInfoResponse(invoice);
    }

    public List<BookingListResponse> searchBookings(BookingSearchRequest request) {
        // Placeholder logic for searching bookings
        return null;
    }

    public List<BookingListResponse> getBookingList() {
        // Placeholder logic for retrieving booking list
        return null;
    }
}