package com.cinelog.controller;

import com.cinelog.controller.request.MovieRequest;
import com.cinelog.controller.response.MovieResponse;
import com.cinelog.entity.Movie;
import com.cinelog.mapper.MovieMapper;
import com.cinelog.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cinelog/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService service;

    @GetMapping()
    public ResponseEntity<List<MovieResponse>> getAll(){
        return ResponseEntity.ok(service.findAll().stream().map((MovieMapper::toMovieResponse)).toList());
    }

    @PostMapping()
    public ResponseEntity<MovieResponse> saveMovie(@RequestBody MovieRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(MovieMapper.toMovieResponse(service.save(MovieMapper.toMovie(request))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getById(@PathVariable Long id){
        return service.findById(id).map(movie -> ResponseEntity.ok(MovieMapper.toMovieResponse(movie))).orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        Optional<Movie> optionalMovie = service.findById(id);
        if(optionalMovie.isPresent()){
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> update(@PathVariable Long id, @RequestBody MovieRequest request){
        return service.updateById(id, MovieMapper.toMovie(request)).map(movie -> ResponseEntity.ok(MovieMapper.toMovieResponse(movie))).
                orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> findByCategory(@RequestParam Long category){
       return ResponseEntity.ok(service.findByCategory(category).stream().map(MovieMapper::toMovieResponse).toList());

    }

}
