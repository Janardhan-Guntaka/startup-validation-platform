package com.validation.post.mapper;

import com.validation.post.dto.CategoryDTO;
import com.validation.post.dto.CreatePostRequest;
import com.validation.post.dto.PostDTO;
import com.validation.post.entity.Post;

/**
 * Maps between Post entity and DTOs.
 */
public final class PostMapper {

    private PostMapper() {}

    public static PostDTO toDTO(Post post, CategoryDTO category) {
        return new PostDTO(
                post.getId(),
                post.getCategoryId(),
                post.getAuthorId(),
                post.getTitle(),
                post.getProblem(),
                post.getSolution(),
                post.getTargetCustomer(),
                post.getTraction(),
                post.getAskType(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                category
        );
    }

    public static Post toEntity(CreatePostRequest request) {
        return Post.builder()
                .categoryId(request.categoryId())
                .authorId(request.authorId())
                .title(request.title())
                .problem(request.problem())
                .solution(request.solution())
                .targetCustomer(request.targetCustomer())
                .traction(request.traction())
                .askType(request.askType())
                .build();
    }
}
