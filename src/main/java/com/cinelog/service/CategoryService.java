package com.cinelog.service;

import com.cinelog.entity.Category;
import com.cinelog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public List<Category> findAll(){
        return repository.findAll();
    }

    public Category saveCategory(Category category){
        return repository.save(category);
    }

    public Optional<Category> findCategoryById(Long id){
        return repository.findById(id);
    }

    public Optional<Category> updateCategoryById(Long id, Category request){
        Optional<Category> optionalCategory = repository.findById(id);
        if(optionalCategory.isPresent()){
            Category category = optionalCategory.get();
            category.setName(request.getName());
            repository.save(category);

            return Optional.of(category);
        }

        return Optional.empty();
    }

    public void deleteCategoryById(Long id){
        repository.deleteById(id);
    }

}
