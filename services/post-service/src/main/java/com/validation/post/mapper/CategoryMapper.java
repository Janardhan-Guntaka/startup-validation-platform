package com.validation.post.mapper;

import com.validation.post.dto.CategoryDTO;
import com.validation.post.dto.CreateCategoryRequest;
import com.validation.post.entity.Category;

/**
 * Maps between Category entity and DTOs.
 */
public final class CategoryMapper {

    private CategoryMapper() {}

    public static CategoryDTO toDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getIcon(),
                category.getParentCategoryId(),
                category.getDisplayOrder(),
                category.getIsActive(),
                category.getCreatedAt()
        );
    }

    public static Category toEntity(CreateCategoryRequest request) {
        return Category.builder()
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .icon(request.icon())
                .parentCategoryId(request.parentCategoryId())
                .displayOrder(request.displayOrder())
                .build();
    }
}
