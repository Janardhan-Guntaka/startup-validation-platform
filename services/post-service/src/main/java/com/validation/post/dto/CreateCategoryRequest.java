package com.validation.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request object for creating a new category.
 */
public record CreateCategoryRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name,

        @NotBlank(message = "Slug is required")
        String slug,

        String description,

        @Size(max = 10, message = "Icon must be at most 10 characters")
        String icon,

        UUID parentCategoryId,

        @NotNull(message = "Display order is required")
        Integer displayOrder
) {}
