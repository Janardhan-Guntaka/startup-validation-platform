package com.validation.review.service;

import com.validation.review.dto.AddReactionRequest;
import com.validation.review.dto.ReactionDTO;
import com.validation.review.dto.ReactionStatsDTO;
import com.validation.review.entity.Reaction;
import com.validation.review.entity.ReactionType;
import com.validation.review.mapper.ReactionMapper;
import com.validation.review.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReactionService {

    private final ReactionRepository reactionRepository;

    public ReactionDTO addOrUpdateReaction(AddReactionRequest request) {
        log.info("Adding/updating reaction for postId={}, userId={}", request.postId(), request.userId());
        Optional<Reaction> existing = reactionRepository.findByPostIdAndUserId(request.postId(), request.userId());
        Reaction reaction;
        if (existing.isPresent()) {
            reaction = existing.get();
            reaction.setReactionType(request.reactionType());
            log.info("Updated existing reaction id={}", reaction.getId());
        } else {
            reaction = ReactionMapper.toEntity(request);
            log.info("Created new reaction");
        }
        Reaction saved = reactionRepository.save(reaction);
        return ReactionMapper.toDTO(saved);
    }

    public void removeReaction(UUID postId, UUID userId) {
        log.info("Removing reaction for postId={}, userId={}", postId, userId);
        reactionRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Transactional(readOnly = true)
    public ReactionStatsDTO getReactionStats(UUID postId) {
        List<Reaction> reactions = reactionRepository.findByPostId(postId);
        long totalReactions = reactions.size();

        Map<ReactionType, Long> countsByType = reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getReactionType, Collectors.counting()));

        int totalScore = reactions.stream()
                .mapToInt(r -> r.getReactionType().getWeight())
                .sum();

        long positiveCount = reactions.stream()
                .filter(r -> r.getReactionType() == ReactionType.WOULD_PAY
                        || r.getReactionType() == ReactionType.WOULD_USE
                        || r.getReactionType() == ReactionType.LOVE_IT)
                .count();

        double demandPercentage = totalReactions > 0
                ? (double) positiveCount / totalReactions * 100.0
                : 0.0;

        return new ReactionStatsDTO(totalReactions, countsByType, totalScore, demandPercentage);
    }

    @Transactional(readOnly = true)
    public Optional<ReactionDTO> getUserReaction(UUID postId, UUID userId) {
        return reactionRepository.findByPostIdAndUserId(postId, userId)
                .map(ReactionMapper::toDTO);
    }
}
