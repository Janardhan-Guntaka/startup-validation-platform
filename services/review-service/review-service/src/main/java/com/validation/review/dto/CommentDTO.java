package com.validation.review.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDTO(
        UUID id,
        UUID postId,
        UUID userId,
        String content,
        Integer upvotes,
        String sentiment,
        Boolean isEdited,
        LocalDateTime createdAt,
        LocalDateTime editedAt,
        boolean isUpvotedByUser
) {}
