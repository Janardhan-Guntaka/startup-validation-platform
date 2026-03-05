package com.validation.review.service;

import com.validation.review.dto.AddReactionRequest;
import com.validation.review.dto.ReactionDTO;
import com.validation.review.dto.ReactionStatsDTO;
import com.validation.review.entity.Reaction;
import com.validation.review.entity.ReactionType;
import com.validation.review.repository.ReactionRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private ReactionService reactionService;

    @Test
    void addOrUpdateReaction_createsNewReaction_whenNotExists() {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AddReactionRequest request = new AddReactionRequest(postId, userId, ReactionType.WOULD_PAY);
        Reaction saved = Reaction.builder()
                .id(UUID.randomUUID()).postId(postId).userId(userId)
                .reactionType(ReactionType.WOULD_PAY).createdAt(LocalDateTime.now()).build();

        when(reactionRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(reactionRepository.save(any(Reaction.class))).thenReturn(saved);

        ReactionDTO result = reactionService.addOrUpdateReaction(request);

        assertThat(result.postId()).isEqualTo(postId);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.reactionType()).isEqualTo(ReactionType.WOULD_PAY);
        verify(reactionRepository).save(any(Reaction.class));
    }

    @Test
    void addOrUpdateReaction_updatesExistingReaction_whenExists() {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID reactionId = UUID.randomUUID();
        Reaction existing = Reaction.builder()
                .id(reactionId).postId(postId).userId(userId)
                .reactionType(ReactionType.LOVE_IT).createdAt(LocalDateTime.now()).build();
        AddReactionRequest request = new AddReactionRequest(postId, userId, ReactionType.WOULD_PAY);
        Reaction updated = Reaction.builder()
                .id(reactionId).postId(postId).userId(userId)
                .reactionType(ReactionType.WOULD_PAY).createdAt(LocalDateTime.now()).build();

        when(reactionRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(existing));
        when(reactionRepository.save(existing)).thenReturn(updated);

        ReactionDTO result = reactionService.addOrUpdateReaction(request);

        assertThat(result.reactionType()).isEqualTo(ReactionType.WOULD_PAY);
        verify(reactionRepository, never()).save(argThat(r -> r != existing));
    }

    @Test
    void removeReaction_callsRepositoryDelete() {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        doNothing().when(reactionRepository).deleteByPostIdAndUserId(postId, userId);

        reactionService.removeReaction(postId, userId);

        verify(reactionRepository).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void getReactionStats_returnsCorrectStats() {
        UUID postId = UUID.randomUUID();
        List<Reaction> reactions = List.of(
                Reaction.builder().reactionType(ReactionType.WOULD_PAY).build(),
                Reaction.builder().reactionType(ReactionType.WOULD_USE).build(),
                Reaction.builder().reactionType(ReactionType.WONT_USE).build()
        );
        when(reactionRepository.findByPostId(postId)).thenReturn(reactions);

        ReactionStatsDTO stats = reactionService.getReactionStats(postId);

        assertThat(stats.totalReactions()).isEqualTo(3);
        assertThat(stats.totalScore()).isEqualTo(10 + 5 - 5);
        assertThat(stats.demandPercentage()).isCloseTo(66.67, org.assertj.core.data.Offset.offset(0.1));
        assertThat(stats.countsByType()).containsKey(ReactionType.WOULD_PAY);
    }

    @Test
    void getUserReaction_returnsEmpty_whenNotFound() {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(reactionRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        Optional<ReactionDTO> result = reactionService.getUserReaction(postId, userId);

        assertThat(result).isEmpty();
    }
}
