package com.validation.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AddCommentRequest(
        @NotNull UUID postId,
        @NotNull UUID userId,
        @NotBlank @Size(max = 2000) String content
) {}
