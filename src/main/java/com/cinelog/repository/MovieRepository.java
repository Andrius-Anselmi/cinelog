package com.cinelog.repository;

import com.cinelog.entity.Category;
import com.cinelog.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findMovieByCategoriesIn(List<Category> categories);
}
