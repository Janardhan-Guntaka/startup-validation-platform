package com.validation.post.dto;

import com.validation.post.entity.AskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request object for creating a new post.
 */
public record CreatePostRequest(
        @NotNull(message = "Category ID is required")
        UUID categoryId,

        @NotNull(message = "Author ID is required")
        UUID authorId,

        @NotBlank(message = "Title is required")
        @Size(min = 10, max = 200, message = "Title must be between 10 and 200 characters")
        String title,

        @NotBlank(message = "Problem is required")
        @Size(min = 50, max = 2000, message = "Problem must be between 50 and 2000 characters")
        String problem,

        @NotBlank(message = "Solution is required")
        @Size(min = 50, max = 2000, message = "Solution must be between 50 and 2000 characters")
        String solution,

        @NotBlank(message = "Target customer is required")
        @Size(min = 20, max = 500, message = "Target customer must be between 20 and 500 characters")
        String targetCustomer,

        @Size(max = 1000, message = "Traction must be at most 1000 characters")
        String traction,

        @NotNull(message = "Ask type is required")
        AskType askType
) {}
