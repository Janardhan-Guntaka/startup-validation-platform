package com.validation.review.dto;

public record SentimentDistribution(
        long positive,
        long negative,
        long neutral
) {}
