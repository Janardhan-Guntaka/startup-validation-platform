package com.validation.review.controller;

import com.validation.review.dto.AddReactionRequest;
import com.validation.review.dto.ReactionDTO;
import com.validation.review.dto.ReactionStatsDTO;
import com.validation.review.service.ReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping
    public ResponseEntity<ReactionDTO> addOrUpdateReaction(@Valid @RequestBody AddReactionRequest request) {
        return ResponseEntity.ok(reactionService.addOrUpdateReaction(request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> removeReaction(
            @PathVariable UUID postId,
            @RequestParam UUID userId) {
        reactionService.removeReaction(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{postId}/stats")
    public ResponseEntity<ReactionStatsDTO> getReactionStats(@PathVariable UUID postId) {
        return ResponseEntity.ok(reactionService.getReactionStats(postId));
    }

    @GetMapping("/posts/{postId}/user/{userId}")
    public ResponseEntity<ReactionDTO> getUserReaction(
            @PathVariable UUID postId,
            @PathVariable UUID userId) {
        return reactionService.getUserReaction(postId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
