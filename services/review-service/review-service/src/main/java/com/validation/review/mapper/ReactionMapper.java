package com.validation.review.mapper;

import com.validation.review.dto.AddReactionRequest;
import com.validation.review.dto.ReactionDTO;
import com.validation.review.entity.Reaction;

public class ReactionMapper {

    private ReactionMapper() {}

    public static ReactionDTO toDTO(Reaction reaction) {
        return new ReactionDTO(
                reaction.getId(),
                reaction.getPostId(),
                reaction.getUserId(),
                reaction.getReactionType(),
                reaction.getCreatedAt()
        );
    }

    public static Reaction toEntity(AddReactionRequest request) {
        return Reaction.builder()
                .postId(request.postId())
                .userId(request.userId())
                .reactionType(request.reactionType())
                .build();
    }
}
