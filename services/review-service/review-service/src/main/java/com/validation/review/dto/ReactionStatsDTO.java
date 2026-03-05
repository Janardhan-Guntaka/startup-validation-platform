package com.validation.review.dto;

import com.validation.review.entity.ReactionType;

import java.util.Map;

public record ReactionStatsDTO(
        long totalReactions,
        Map<ReactionType, Long> countsByType,
        int totalScore,
        double demandPercentage
) {}
