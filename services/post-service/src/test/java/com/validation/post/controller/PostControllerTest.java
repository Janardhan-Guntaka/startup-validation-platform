package com.validation.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.validation.post.dto.CategoryDTO;
import com.validation.post.dto.CreatePostRequest;
import com.validation.post.dto.PostDTO;
import com.validation.post.entity.AskType;
import com.validation.post.exception.GlobalExceptionHandler;
import com.validation.post.exception.ResourceNotFoundException;
import com.validation.post.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@Import(GlobalExceptionHandler.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    private final UUID postId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final UUID authorId = UUID.randomUUID();
    private final LocalDateTime now = LocalDateTime.now();

    private PostDTO samplePostDTO() {
        var categoryDTO = new CategoryDTO(categoryId, "Software", "software",
                "desc", "\uD83D\uDCBB", null, 1, true, now);
        return new PostDTO(postId, categoryId, authorId, "My Startup Idea for Validation",
                "There is a significant problem in the market that needs solving urgently by startups",
                "We have built an innovative solution that addresses this problem effectively for users",
                "Small business owners and entrepreneurs who need help",
                "100 users", AskType.DEMAND_VALIDATION, now, now, categoryDTO);
    }

    @Test
    void createPost_returnsCreated() throws Exception {
        var request = new CreatePostRequest(
                categoryId, authorId,
                "My Startup Idea for Validation",
                "There is a significant problem in the market that needs solving urgently by startups",
                "We have built an innovative solution that addresses this problem effectively for users",
                "Small business owners and entrepreneurs who need help",
                "100 users", AskType.DEMAND_VALIDATION);

        when(postService.createPost(any(CreatePostRequest.class))).thenReturn(samplePostDTO());

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("My Startup Idea for Validation"))
                .andExpect(jsonPath("$.askType").value("DEMAND_VALIDATION"));
    }

    @Test
    void createPost_invalidRequest_returnsBadRequest() throws Exception {
        var request = new CreatePostRequest(null, null, "", "", "", "", null, null);

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPost_returnsOk() throws Exception {
        when(postService.getPost(postId)).thenReturn(samplePostDTO());

        mockMvc.perform(get("/api/v1/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("My Startup Idea for Validation"));
    }

    @Test
    void getPost_notFound_returns404() throws Exception {
        when(postService.getPost(postId))
                .thenThrow(new ResourceNotFoundException("Post not found: " + postId));

        mockMvc.perform(get("/api/v1/posts/" + postId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPostsByCategory_returnsPage() throws Exception {
        Page<PostDTO> page = new PageImpl<>(List.of(samplePostDTO()));
        when(postService.getPostsByCategory(eq(categoryId), eq(0), eq(20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/posts")
                        .param("categoryId", categoryId.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("My Startup Idea for Validation"));
    }

    @Test
    void getPostsByAuthor_returnsPage() throws Exception {
        Page<PostDTO> page = new PageImpl<>(List.of(samplePostDTO()));
        when(postService.getPostsByAuthor(eq(authorId), eq(0), eq(20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/posts")
                        .param("authorId", authorId.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("My Startup Idea for Validation"));
    }
}
