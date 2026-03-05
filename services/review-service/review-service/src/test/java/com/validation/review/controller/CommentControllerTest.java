package com.validation.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.validation.review.dto.AddCommentRequest;
import com.validation.review.dto.CommentDTO;
import com.validation.review.dto.EditCommentRequest;
import com.validation.review.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;

    @Test
    void addComment_returns201WithLocation() throws Exception {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        AddCommentRequest request = new AddCommentRequest(postId, userId, "Great startup idea!");
        CommentDTO dto = new CommentDTO(
                commentId, postId, userId, "Great startup idea!", 0, null, false, LocalDateTime.now(), null, false);

        when(commentService.addComment(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Great startup idea!"))
                .andExpect(jsonPath("$.upvotes").value(0));
    }

    @Test
    void getCommentsByPost_returns200WithPage() throws Exception {
        UUID postId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CommentDTO dto = new CommentDTO(
                UUID.randomUUID(), postId, userId, "Test comment", 5, null, false, LocalDateTime.now(), null, false);
        Page<CommentDTO> page = new PageImpl<>(List.of(dto));

        when(commentService.getCommentsByPost(eq(postId), any(), any(), eq(0), eq(20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/comments/posts/{postId}", postId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("Test comment"))
                .andExpect(jsonPath("$.content[0].upvotes").value(5));
    }

    @Test
    void editComment_returns200WithUpdatedContent() throws Exception {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        EditCommentRequest request = new EditCommentRequest(userId, "Updated content");
        CommentDTO dto = new CommentDTO(
                commentId, postId, userId, "Updated content", 0, null, true, LocalDateTime.now(), LocalDateTime.now(), false);

        when(commentService.editComment(eq(commentId), any())).thenReturn(dto);

        mockMvc.perform(put("/api/v1/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.isEdited").value(true));
    }

    @Test
    void deleteComment_returns204() throws Exception {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        doNothing().when(commentService).deleteComment(commentId, userId);

        mockMvc.perform(delete("/api/v1/comments/{commentId}", commentId)
                        .param("userId", userId.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void upvoteComment_returns200() throws Exception {
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        doNothing().when(commentService).upvoteComment(commentId, userId);

        mockMvc.perform(post("/api/v1/comments/{commentId}/upvote", commentId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk());
    }
}
