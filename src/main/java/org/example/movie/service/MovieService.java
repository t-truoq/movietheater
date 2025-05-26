
package org.example.movie.service;

import org.example.movie.dto.request.AddMovieRequest;
import org.example.movie.dto.request.UpdateMovieRequest;
import org.example.movie.dto.response.MovieResponse;
import org.example.movie.entity.*;
import org.example.movie.exception.AppException;
import org.example.movie.exception.ErrorCode;
import org.example.movie.mapper.MovieMapper;
import org.example.movie.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private MovieMapper movieMapper;

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
    public String addMovie(AddMovieRequest request) {
        if (request.getFromDate() != null && request.getToDate() != null && request.getFromDate().isAfter(request.getToDate())) {
            throw new AppException(ErrorCode.INVALID_MOVIE_DATA, "From date must be before to date");
        }

        CinemaRoom cinemaRoom = cinemaRoomRepository.findById(request.getCinemaRoom())
                .orElseThrow(() -> new AppException(ErrorCode.CINEMA_ROOM_NOT_FOUND));

        Movie movie = movieMapper.toEntity(request);
        movie.setCinemaRoom(cinemaRoom);
        movieRepository.save(movie);

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

        return "Movie added successfully";
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
