package com.cinelog.service;

import com.cinelog.entity.Category;
import com.cinelog.entity.Movie;
import com.cinelog.entity.Streaming;
import com.cinelog.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository repository;
    private final CategoryService categoryService;
    private final StreamingService streamingService;

    public Movie save(Movie movie){
        movie.setCategories(this.findCategories(movie.getCategories()));
        movie.setStreamings(this.findStreamings(movie.getStreamings()));
        return repository.save(movie);
    }

    public List<Movie> findAll(){
        return repository.findAll();
    }

    public Optional<Movie> findById(Long id){
        return repository.findById(id);
    }

    public Optional<Movie> updateById(Long id, Movie updateMovie){
        Optional<Movie> optMovie = repository.findById(id);
        if (optMovie.isPresent()){

            List<Category> categories = this.findCategories(updateMovie.getCategories());
            List<Streaming> streamings = this.findStreamings(updateMovie.getStreamings());

            Movie movie = optMovie.get();
            movie.setDescription(updateMovie.getDescription());
            movie.setUpdatedAt(updateMovie.getUpdatedAt());
            movie.setRating(updateMovie.getRating());
            movie.setReleaseDate(updateMovie.getReleaseDate());
            movie.setTitle(updateMovie.getTitle());

            movie.getStreamings().clear();
            movie.getStreamings().addAll(streamings);

            movie.getCategories().clear();
            movie.getCategories().addAll(categories);

            repository.save(movie);

            return Optional.of(movie);
        }

        return Optional.empty();
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }

    public List<Movie> findByCategory(Long categoryId){

        return repository.findMovieByCategoriesIn(List.of(Category.builder().
                id(categoryId).build()));
    }

    private List<Streaming> findStreamings(List<Streaming> streamingList){
        List<Streaming> streamingsFound = new ArrayList<>();
        streamingList.forEach(streaming -> streamingService.
                findById(streaming.getId()).ifPresent(streamingsFound::add));

        return streamingsFound;
    }

    private List<Category> findCategories(List<Category> categoryList){
        List<Category> categoriesFound = new ArrayList<>();
        categoryList.forEach(category -> categoryService.
                findCategoryById(category.getId()).ifPresent(categoriesFound::add));

        return categoriesFound;
    }



}
