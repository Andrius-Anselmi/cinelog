package com.cinelog.controller;

import com.cinelog.request.CategoryRequest;

import com.cinelog.response.CategoryResponse;
import com.cinelog.entity.Category;
import com.cinelog.mapper.CategoryMapper;
import com.cinelog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cinelog/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        return ResponseEntity.ok().body(categoryService.findAll().stream().map((CategoryMapper::toCategoryResponse)).toList());
    }

    @PostMapping()
    public ResponseEntity<CategoryResponse> saveCategory(@Valid @RequestBody CategoryRequest request){
         Category savedCategory = categoryService.saveCategory(CategoryMapper.toCategory(request));
         return ResponseEntity.status(HttpStatus.CREATED).body(CategoryMapper.toCategoryResponse(savedCategory));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getByCategoryId(@PathVariable Long id){
        return categoryService.findCategoryById(id).map(category -> ResponseEntity.ok(CategoryMapper.toCategoryResponse(category))).
                orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return categoryService.updateCategoryById(id, CategoryMapper.toCategory(request)).
                map(category -> ResponseEntity.ok(CategoryMapper.
                        toCategoryResponse(category))).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByCategoryId(@PathVariable Long id){
        Optional<Category> optionalCategory = categoryService.findCategoryById(id);
        if(optionalCategory.isPresent()){
            categoryService.deleteCategoryById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}
