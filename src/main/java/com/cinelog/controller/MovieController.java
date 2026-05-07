package com.cinelog.controller;

import com.cinelog.controller.request.MovieRequest;
import com.cinelog.controller.response.MovieResponse;
import com.cinelog.entity.Category;
import com.cinelog.entity.Movie;
import com.cinelog.mapper.MovieMapper;
import com.cinelog.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cinelog/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService service;

    @GetMapping()
    public List<MovieResponse> getAll(){
        return service.findAll().stream().map((MovieMapper::toMovieResponse)).toList();
    }

    @PostMapping()
    public ResponseEntity<MovieResponse> saveMovie(@RequestBody MovieRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(MovieMapper.toMovieResponse(service.save(MovieMapper.toMovie(request))));
    }

    @GetMapping("{id}")
    public ResponseEntity<MovieResponse> getById(@PathVariable Long id){
        return service.findById(id).map(movie -> ResponseEntity.ok(MovieMapper.toMovieResponse(movie))).orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
