package com.validation.review.dto;

import com.validation.review.entity.ReactionType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddReactionRequest(
        @NotNull UUID postId,
        @NotNull UUID userId,
        @NotNull ReactionType reactionType
) {}
