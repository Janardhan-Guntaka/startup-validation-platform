package com.validation.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.validation.review.dto.AddReactionRequest;
import com.validation.review.dto.ReactionDTO;
import com.validation.review.dto.ReactionStatsDTO;
import com.validation.review.entity.ReactionType;
import com.validation.review.service.ReactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReactionController.class)
class ReactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReactionService reactionService;

    @Test
    void addOrUpdateReaction_returns200() throws Exception {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID reactionId = UUID.randomUUID();
        AddReactionRequest request = new AddReactionRequest(postId, userId, ReactionType.WOULD_PAY);
        ReactionDTO dto = new ReactionDTO(reactionId, postId, userId, ReactionType.WOULD_PAY, LocalDateTime.now());

        when(reactionService.addOrUpdateReaction(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/reactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reactionType").value("WOULD_PAY"))
                .andExpect(jsonPath("$.postId").value(postId.toString()));
    }

    @Test
    void removeReaction_returns204() throws Exception {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        doNothing().when(reactionService).removeReaction(postId, userId);

        mockMvc.perform(delete("/api/v1/reactions/{postId}", postId)
                        .param("userId", userId.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getReactionStats_returns200WithStats() throws Exception {
        UUID postId = UUID.randomUUID();
        ReactionStatsDTO stats = new ReactionStatsDTO(
                10, Map.of(ReactionType.WOULD_PAY, 3L, ReactionType.WOULD_USE, 7L), 85, 100.0);

        when(reactionService.getReactionStats(postId)).thenReturn(stats);

        mockMvc.perform(get("/api/v1/reactions/posts/{postId}/stats", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReactions").value(10))
                .andExpect(jsonPath("$.totalScore").value(85));
    }

    @Test
    void getUserReaction_returns200_whenFound() throws Exception {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ReactionDTO dto = new ReactionDTO(UUID.randomUUID(), postId, userId, ReactionType.LOVE_IT, LocalDateTime.now());

        when(reactionService.getUserReaction(eq(postId), eq(userId))).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/reactions/posts/{postId}/user/{userId}", postId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reactionType").value("LOVE_IT"));
    }

    @Test
    void getUserReaction_returns404_whenNotFound() throws Exception {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(reactionService.getUserReaction(eq(postId), eq(userId))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/reactions/posts/{postId}/user/{userId}", postId, userId))
                .andExpect(status().isNotFound());
    }
}
