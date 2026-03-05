package com.validation.review.repository;

import com.validation.review.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByPostIdAndIsDeletedFalseOrderByUpvotesDesc(UUID postId, Pageable pageable);
    Page<Comment> findByPostIdAndIsDeletedFalseOrderByCreatedAtDesc(UUID postId, Pageable pageable);
    List<Comment> findByPostIdAndIsDeletedFalse(UUID postId);
    long countByPostIdAndIsDeletedFalse(UUID postId);
}
