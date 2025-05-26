package org.example.movie.mapper;

import org.example.movie.dto.request.AddMovieRequest;
import org.example.movie.dto.request.UpdateMovieRequest;
import org.example.movie.dto.response.MovieResponse;
import org.example.movie.entity.CinemaRoom;
import org.example.movie.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(source = "cinemaRoom.cinemaRoomId", target = "cinemaRoomId")
    MovieResponse toResponse(Movie movie);

    @Mapping(source = "cinemaRoom", target = "cinemaRoom")
    Movie toEntity(AddMovieRequest request);

    @Mapping(source = "cinemaRoomId", target = "cinemaRoom")
    Movie toEntity(UpdateMovieRequest request);

    // Hỗ trợ ánh xạ Long -> CinemaRoom
    default CinemaRoom map(Long cinemaRoomId) {
        if (cinemaRoomId == null) return null;
        CinemaRoom cinemaRoom = new CinemaRoom();
        cinemaRoom.setCinemaRoomId(cinemaRoomId);
        return cinemaRoom;
    }
}