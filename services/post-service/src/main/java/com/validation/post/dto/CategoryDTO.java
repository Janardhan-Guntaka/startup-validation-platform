package com.validation.post.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data transfer object for Category.
 */
public record CategoryDTO(
        UUID id,
        String name,
        String slug,
        String description,
        String icon,
        UUID parentCategoryId,
        Integer displayOrder,
        Boolean isActive,
        LocalDateTime createdAt
) {}
