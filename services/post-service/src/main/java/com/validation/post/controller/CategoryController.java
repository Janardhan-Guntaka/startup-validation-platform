package com.validation.post.controller;

import com.validation.post.dto.CategoryDTO;
import com.validation.post.dto.CategoryWithSubsDTO;
import com.validation.post.dto.CreateCategoryRequest;
import com.validation.post.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for category operations.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getTopLevelCategories() {
        return ResponseEntity.ok(categoryService.getTopLevelCategories());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CategoryWithSubsDTO> getCategoryBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(categoryService.getCategoryBySlug(slug));
    }

    @GetMapping("/{slug}/subcategories")
    public ResponseEntity<List<CategoryDTO>> getSubCategories(@PathVariable String slug) {
        CategoryWithSubsDTO category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(categoryService.getSubCategories(category.id()));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }
}
