package com.validation.review.service;

import com.validation.review.dto.AddCommentRequest;
import com.validation.review.dto.CommentDTO;
import com.validation.review.dto.EditCommentRequest;
import com.validation.review.dto.SentimentDistribution;
import com.validation.review.entity.Comment;
import com.validation.review.entity.CommentUpvote;
import com.validation.review.exception.ForbiddenException;
import com.validation.review.exception.ResourceNotFoundException;
import com.validation.review.mapper.CommentMapper;
import com.validation.review.repository.CommentRepository;
import com.validation.review.repository.CommentUpvoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentUpvoteRepository commentUpvoteRepository;

    public CommentDTO addComment(AddCommentRequest request) {
        log.info("Adding comment for postId={}, userId={}", request.postId(), request.userId());
        Comment comment = CommentMapper.toEntity(request);
        Comment saved = commentRepository.save(comment);
        return CommentMapper.toDTO(saved, false);
    }

    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByPost(UUID postId, UUID userId, String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments;
        if ("upvotes".equalsIgnoreCase(sort)) {
            comments = commentRepository.findByPostIdAndIsDeletedFalseOrderByUpvotesDesc(postId, pageable);
        } else {
            comments = commentRepository.findByPostIdAndIsDeletedFalseOrderByCreatedAtDesc(postId, pageable);
        }
        return comments.map(comment -> {
            boolean upvoted = userId != null
                    && commentUpvoteRepository.existsByCommentIdAndUserId(comment.getId(), userId);
            return CommentMapper.toDTO(comment, upvoted);
        });
    }

    public CommentDTO editComment(UUID commentId, EditCommentRequest request) {
        log.info("Editing comment id={}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        if (!comment.getUserId().equals(request.userId())) {
            throw new ForbiddenException("You are not authorized to edit this comment");
        }
        comment.setContent(request.content());
        comment.setIsEdited(true);
        comment.setEditedAt(LocalDateTime.now());
        Comment saved = commentRepository.save(comment);
        boolean upvoted = commentUpvoteRepository.existsByCommentIdAndUserId(commentId, request.userId());
        return CommentMapper.toDTO(saved, upvoted);
    }

    public void deleteComment(UUID commentId, UUID userId) {
        log.info("Deleting comment id={}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        if (!comment.getUserId().equals(userId)) {
            throw new ForbiddenException("You are not authorized to delete this comment");
        }
        comment.setIsDeleted(true);
        commentRepository.save(comment);
    }

    public void upvoteComment(UUID commentId, UUID userId) {
        log.info("Upvoting comment id={} by userId={}", commentId, userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        if (commentUpvoteRepository.existsByCommentIdAndUserId(commentId, userId)) {
            log.warn("User {} already upvoted comment {}", userId, commentId);
            return;
        }
        CommentUpvote upvote = CommentUpvote.builder()
                .commentId(commentId)
                .userId(userId)
                .build();
        commentUpvoteRepository.save(upvote);
        comment.setUpvotes(comment.getUpvotes() + 1);
        commentRepository.save(comment);
    }

    public void removeUpvote(UUID commentId, UUID userId) {
        log.info("Removing upvote from comment id={} by userId={}", commentId, userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        if (!commentUpvoteRepository.existsByCommentIdAndUserId(commentId, userId)) {
            return;
        }
        commentUpvoteRepository.deleteByCommentIdAndUserId(commentId, userId);
        comment.setUpvotes(Math.max(0, comment.getUpvotes() - 1));
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public long getCommentCount(UUID postId) {
        return commentRepository.countByPostIdAndIsDeletedFalse(postId);
    }

    @Transactional(readOnly = true)
    public SentimentDistribution getSentimentDistribution(UUID postId) {
        List<Comment> comments = commentRepository.findByPostIdAndIsDeletedFalse(postId);
        long positive = comments.stream()
                .filter(c -> "POSITIVE".equalsIgnoreCase(c.getSentiment()))
                .count();
        long negative = comments.stream()
                .filter(c -> "NEGATIVE".equalsIgnoreCase(c.getSentiment()))
                .count();
        long neutral = comments.size() - positive - negative;
        return new SentimentDistribution(positive, negative, neutral);
    }
}
