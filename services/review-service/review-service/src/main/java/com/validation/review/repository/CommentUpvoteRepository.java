package com.validation.review.repository;

import com.validation.review.entity.CommentUpvote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentUpvoteRepository extends JpaRepository<CommentUpvote, UUID> {
    boolean existsByCommentIdAndUserId(UUID commentId, UUID userId);
    void deleteByCommentIdAndUserId(UUID commentId, UUID userId);
}
