package com.validation.post.dto;

import com.validation.post.entity.AskType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data transfer object for Post.
 */
public record PostDTO(
        UUID id,
        UUID categoryId,
        UUID authorId,
        String title,
        String problem,
        String solution,
        String targetCustomer,
        String traction,
        AskType askType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        CategoryDTO category
) {}
