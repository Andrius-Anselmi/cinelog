package com.cinelog.controller;

import com.cinelog.entity.Category;
import com.cinelog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cinelog/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping()
    public List<Category> getAllCategories(){
        return categoryService.findAll();
    }

    @PostMapping()
    public Category saveCategory(@RequestBody Category category){
        return categoryService.saveCategory(category);
    }

    @GetMapping("/{id}")
    public Category getByCategoryId(@PathVariable Long id){
        Optional<Category> optionalCategory  = categoryService.findCategoryById(id);
        if(optionalCategory.isPresent()){
            return optionalCategory.get();
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteByCategoryId(@PathVariable Long id){
        categoryService.deletCategoryById(id);
    }

}
