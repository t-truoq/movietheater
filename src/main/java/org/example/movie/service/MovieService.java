package org.example.movie.service;

import org.example.movie.dto.request.AddMovieRequest;
import org.example.movie.dto.request.UpdateMovieRequest;
import org.example.movie.dto.response.MovieResponse;
import org.example.movie.entity.*;
import org.example.movie.enums.SeatStatus;
import org.example.movie.enums.SeatType;
import org.example.movie.exception.AppException;
import org.example.movie.exception.ErrorCode;
import org.example.movie.mapper.MovieMapper;
import org.example.movie.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaRoomRepository cinemaRoomRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private MovieTypeRepository movieTypeRepository;

    @Autowired
    private MovieScheduleRepository movieScheduleRepository;

    @Autowired
    private ScheduleSeatRepository scheduleSeatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ShowDateRepository showDateRepository;

    @Autowired
    private MovieDateRepository movieDateRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private MovieMapper movieMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieService.class);

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Movie addMovie(AddMovieRequest request) {
        // Validation
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

        // Tạo Movie
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

        // Tạo MovieType
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

        // Tạo ShowDate và MovieDate
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

        // Lấy danh sách ghế của phòng chiếu
        List<Seat> seats = seatRepository.findByCinemaRoomCinemaRoomId(cinemaRoom.getCinemaRoomId());
        if (seats.isEmpty()) {
            throw new AppException(ErrorCode.SEAT_NOT_FOUND, "No seats found for cinema room");
        }

        // Tạo Schedule và ScheduleSeat
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        for (LocalDateTime scheduleTime : request.getScheduleTimes()) {
            LocalDate scheduleDate = scheduleTime.toLocalDate();
            if (scheduleDate.isBefore(request.getFromDate()) || scheduleDate.isAfter(request.getToDate())) {
                LOGGER.warn("Schedule time {} is outside the movie date range ({} to {}), skipping",
                        scheduleTime, request.getFromDate(), request.getToDate());
                continue;
            }

            // Tạo Schedule
            Schedule schedule = Schedule.builder()
                    .scheduleTime(scheduleTime.format(formatter))
                    .build();
            schedule = scheduleRepository.save(schedule);

            // Tạo MovieSchedule
            MovieSchedule movieSchedule = MovieSchedule.builder()
                    .movie(movie)
                    .schedule(schedule)
                    .build();
            movieScheduleRepository.save(movieSchedule);

            // Tạo ScheduleSeat cho mỗi Seat trong phòng chiếu
            for (Seat seat : seats) {
                ScheduleSeat scheduleSeat = ScheduleSeat.builder()
                        .schedule(schedule)
                        .movie(movie)
                        .seatColumn(seat.getSeatColumn())
                        .seatRow(seat.getSeatRow())
                        .seatStatus(SeatStatus.AVAILABLE)
                        .seatType(seat.getSeatType().ordinal()) // Sử dụng seatType trực tiếp thay vì ordinal()
                        .build();
                scheduleSeatRepository.save(scheduleSeat);
            }
        }

        return movie;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<MovieResponse> getMovieList(String searchKeyword) {
        List<Movie> movies = searchKeyword == null || searchKeyword.isEmpty()
                ? movieRepository.findAll()
                : movieRepository.findByMovieNameVnContainingIgnoreCaseOrMovieNameEnglishContainingIgnoreCase(searchKeyword, searchKeyword);
        return movies.stream()
                .map(movieMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String updateMovie(UpdateMovieRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        if (request.getFromDate() != null && request.getToDate() != null && request.getFromDate().isAfter(request.getToDate())) {
            throw new AppException(ErrorCode.INVALID_MOVIE_DATA, "From date must be before to date");
        }

        CinemaRoom cinemaRoom = cinemaRoomRepository.findById(request.getCinemaRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_ROOM_NOT_FOUND));

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
        movie.setCinemaRoom(cinemaRoom);
        movieRepository.save(movie);

        movieTypeRepository.deleteByMovieMovieId(movie.getMovieId());
        List<Type> types = typeRepository.findAllById(request.getTypeIds());
        if (types.size() != request.getTypeIds().size()) {
            throw new AppException(ErrorCode.TYPE_NOT_FOUND);
        }
        List<MovieType> movieTypes = types.stream()
                .map(type -> MovieType.builder()
                        .movie(movie)
                        .type(type)
                        .build())
                .collect(Collectors.toList());
        movieTypeRepository.saveAll(movieTypes);

        return "Movie updated successfully";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deleteMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        LocalDate currentDate = LocalDate.now(); // 22/05/2025
        if (movie.getToDate() != null && movie.getToDate().isAfter(currentDate)) {
            throw new AppException(ErrorCode.MOVIE_IN_USE, "Cannot delete movie because it is still scheduled.");
        }

        List<MovieSchedule> movieSchedules = movieScheduleRepository.findByMovieMovieId(movieId);
        if (!movieSchedules.isEmpty()) {
            throw new AppException(ErrorCode.MOVIE_IN_USE, "Cannot delete movie because it has scheduled showtimes.");
        }

        List<ScheduleSeat> scheduleSeats = scheduleSeatRepository.findByMovieMovieId(movieId);
        for (ScheduleSeat scheduleSeat : scheduleSeats) {
            List<Ticket> tickets = ticketRepository.findByScheduleSeat_ScheduleSeatId(scheduleSeat.getScheduleSeatId());
            if (!tickets.isEmpty()) {
                throw new AppException(ErrorCode.MOVIE_IN_USE, "Cannot delete movie because it has booked tickets.");
            }
        }

        movieTypeRepository.deleteByMovieMovieId(movieId);
        movieScheduleRepository.deleteByMovieMovieId(movieId);
        movieRepository.delete(movie);

        return "Movie deleted successfully";
    }
}