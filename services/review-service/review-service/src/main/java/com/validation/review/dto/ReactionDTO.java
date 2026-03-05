package com.validation.review.dto;

import com.validation.review.entity.ReactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReactionDTO(
        UUID id,
        UUID postId,
        UUID userId,
        ReactionType reactionType,
        LocalDateTime createdAt
) {}
