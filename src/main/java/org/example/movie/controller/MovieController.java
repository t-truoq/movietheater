package org.example.movie.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.movie.dto.request.AddMovieRequest;
import org.example.movie.dto.request.UpdateMovieRequest;
import org.example.movie.dto.response.MovieResponse;

import org.example.movie.entity.Movie;
import org.example.movie.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MovieResponse>> getMovieList(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(movieService.getMovieList(search));
    }
    @Operation(summary = "Add a new movie", description = "Adds a new movie with specified schedules")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/add")
    public ResponseEntity<Movie> addMovie(@RequestBody AddMovieRequest request) {
        Movie movie = movieService.addMovie(request);
        return ResponseEntity.ok(movie);
    }



    @PutMapping("/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateMovie(@PathVariable Long movieId, @RequestBody UpdateMovieRequest request) {
        request.setMovieId(movieId);
        return ResponseEntity.ok(movieService.updateMovie(request));
    }

    @DeleteMapping("/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }
}