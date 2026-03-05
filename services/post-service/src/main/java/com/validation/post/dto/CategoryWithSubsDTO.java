package com.validation.post.dto;

import java.util.List;
import java.util.UUID;

/**
 * Data transfer object for Category with its sub-categories.
 */
public record CategoryWithSubsDTO(
        UUID id,
        String name,
        String slug,
        String description,
        String icon,
        Integer displayOrder,
        List<CategoryDTO> subCategories
) {}
