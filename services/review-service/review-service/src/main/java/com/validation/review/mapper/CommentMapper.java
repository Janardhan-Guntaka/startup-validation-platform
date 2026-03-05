package com.validation.review.mapper;

import com.validation.review.dto.AddCommentRequest;
import com.validation.review.dto.CommentDTO;
import com.validation.review.entity.Comment;

public class CommentMapper {

    private CommentMapper() {}

    public static CommentDTO toDTO(Comment comment, boolean isUpvotedByUser) {
        return new CommentDTO(
                comment.getId(),
                comment.getPostId(),
                comment.getUserId(),
                comment.getContent(),
                comment.getUpvotes(),
                comment.getSentiment(),
                comment.getIsEdited(),
                comment.getCreatedAt(),
                comment.getEditedAt(),
                isUpvotedByUser
        );
    }

    public static Comment toEntity(AddCommentRequest request) {
        return Comment.builder()
                .postId(request.postId())
                .userId(request.userId())
                .content(request.content())
                .build();
    }
}
