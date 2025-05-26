package org.example.movie.controller;

import org.example.movie.dto.request.AddMovieRequest;
import org.example.movie.dto.request.UpdateMovieRequest;
import org.example.movie.dto.response.MovieResponse;

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

//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<String> addMovie(@RequestBody AddMovieRequest request) {
//        return ResponseEntity.ok(movieService.addMovie(request));
//    }

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