package com.validation.review.service;

import com.validation.review.dto.AddCommentRequest;
import com.validation.review.dto.CommentDTO;
import com.validation.review.dto.EditCommentRequest;
import com.validation.review.dto.SentimentDistribution;
import com.validation.review.entity.Comment;
import com.validation.review.entity.CommentUpvote;
import com.validation.review.exception.ForbiddenException;
import com.validation.review.exception.ResourceNotFoundException;
import com.validation.review.repository.CommentRepository;
import com.validation.review.repository.CommentUpvoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentUpvoteRepository commentUpvoteRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void addComment_savesAndReturnsDTO() {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AddCommentRequest request = new AddCommentRequest(postId, userId, "Great idea!");
        Comment saved = Comment.builder()
                .id(UUID.randomUUID()).postId(postId).userId(userId)
                .content("Great idea!").upvotes(0).isEdited(false).isDeleted(false)
                .createdAt(LocalDateTime.now()).build();

        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        CommentDTO result = commentService.addComment(request);

        assertThat(result.content()).isEqualTo("Great idea!");
        assertThat(result.postId()).isEqualTo(postId);
        assertThat(result.isUpvotedByUser()).isFalse();
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void editComment_throwsForbiddenException_whenNotOwner() {
        UUID commentId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        Comment comment = Comment.builder()
                .id(commentId).userId(ownerId).content("Original").build();
        EditCommentRequest request = new EditCommentRequest(otherId, "Updated");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.editComment(commentId, request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void deleteComment_softDeletes_whenOwner() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Comment comment = Comment.builder()
                .id(commentId).userId(userId).content("Test").isDeleted(false).build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        commentService.deleteComment(commentId, userId);

        assertThat(comment.getIsDeleted()).isTrue();
        verify(commentRepository).save(comment);
    }

    @Test
    void upvoteComment_incrementsUpvotes_whenNotAlreadyUpvoted() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Comment comment = Comment.builder()
                .id(commentId).userId(UUID.randomUUID()).content("Test").upvotes(5).build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentUpvoteRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);
        when(commentUpvoteRepository.save(any(CommentUpvote.class))).thenReturn(CommentUpvote.builder().build());
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        commentService.upvoteComment(commentId, userId);

        assertThat(comment.getUpvotes()).isEqualTo(6);
    }

    @Test
    void deleteComment_throwsResourceNotFound_whenCommentNotExists() {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(commentId, userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(commentId.toString());
    }

    @Test
    void getSentimentDistribution_returnsCorrectCounts() {
        UUID postId = UUID.randomUUID();
        List<Comment> comments = List.of(
                Comment.builder().sentiment("POSITIVE").isDeleted(false).build(),
                Comment.builder().sentiment("POSITIVE").isDeleted(false).build(),
                Comment.builder().sentiment("NEGATIVE").isDeleted(false).build(),
                Comment.builder().isDeleted(false).build()
        );
        when(commentRepository.findByPostIdAndIsDeletedFalse(postId)).thenReturn(comments);

        SentimentDistribution dist = commentService.getSentimentDistribution(postId);

        assertThat(dist.positive()).isEqualTo(2);
        assertThat(dist.negative()).isEqualTo(1);
        assertThat(dist.neutral()).isEqualTo(1);
    }
}
