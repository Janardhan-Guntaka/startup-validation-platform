package com.validation.review.controller;

import com.validation.review.dto.AddCommentRequest;
import com.validation.review.dto.CommentDTO;
import com.validation.review.dto.EditCommentRequest;
import com.validation.review.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDTO> addComment(@Valid @RequestBody AddCommentRequest request) {
        CommentDTO dto = commentService.addComment(request);
        return ResponseEntity.created(URI.create("/api/v1/comments/" + dto.id())).body(dto);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Page<CommentDTO>> getCommentsByPost(
            @PathVariable UUID postId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(defaultValue = "upvotes") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, userId, sort, page, size));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> editComment(
            @PathVariable UUID commentId,
            @Valid @RequestBody EditCommentRequest request) {
        return ResponseEntity.ok(commentService.editComment(commentId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/upvote")
    public ResponseEntity<Void> upvoteComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId) {
        commentService.upvoteComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}/upvote")
    public ResponseEntity<Void> removeUpvote(
            @PathVariable UUID commentId,
            @RequestParam UUID userId) {
        commentService.removeUpvote(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
