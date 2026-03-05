package com.validation.post.service;

import com.validation.post.dto.CategoryDTO;
import com.validation.post.dto.CategoryWithSubsDTO;
import com.validation.post.dto.CreateCategoryRequest;
import com.validation.post.entity.Category;
import com.validation.post.exception.ResourceNotFoundException;
import com.validation.post.mapper.CategoryMapper;
import com.validation.post.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for category operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Returns all top-level categories (those without a parent).
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getTopLevelCategories() {
        log.debug("Fetching top-level categories");
        return categoryRepository.findByParentCategoryIdIsNullOrderByDisplayOrder()
                .stream()
                .map(CategoryMapper::toDTO)
                .toList();
    }

    /**
     * Returns a category by slug along with its sub-categories.
     */
    @Transactional(readOnly = true)
    public CategoryWithSubsDTO getCategoryBySlug(String slug) {
        log.debug("Fetching category by slug: {}", slug);
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + slug));

        List<CategoryDTO> subCategories = categoryRepository
                .findByParentCategoryIdOrderByDisplayOrder(category.getId())
                .stream()
                .map(CategoryMapper::toDTO)
                .toList();

        return new CategoryWithSubsDTO(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getIcon(),
                category.getDisplayOrder(),
                subCategories
        );
    }

    /**
     * Returns all sub-categories for a given parent category ID.
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getSubCategories(UUID parentId) {
        log.debug("Fetching sub-categories for parent: {}", parentId);
        return categoryRepository.findByParentCategoryIdOrderByDisplayOrder(parentId)
                .stream()
                .map(CategoryMapper::toDTO)
                .toList();
    }

    /**
     * Creates a new category.
     */
    @Transactional
    public CategoryDTO createCategory(CreateCategoryRequest request) {
        log.debug("Creating category: {}", request.name());
        Category category = CategoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toDTO(saved);
    }
}
