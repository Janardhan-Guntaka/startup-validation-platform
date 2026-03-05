package com.validation.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.validation.post.dto.CategoryDTO;
import com.validation.post.dto.CategoryWithSubsDTO;
import com.validation.post.dto.CreateCategoryRequest;
import com.validation.post.exception.GlobalExceptionHandler;
import com.validation.post.exception.ResourceNotFoundException;
import com.validation.post.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@Import(GlobalExceptionHandler.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private final UUID categoryId = UUID.randomUUID();
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void getTopLevelCategories_returnsOk() throws Exception {
        var dto = new CategoryDTO(categoryId, "Software & Technology", "software-technology",
                "desc", "\uD83D\uDCBB", null, 1, true, now);
        when(categoryService.getTopLevelCategories()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Software & Technology"))
                .andExpect(jsonPath("$[0].slug").value("software-technology"));
    }

    @Test
    void getCategoryBySlug_returnsOk() throws Exception {
        var subDto = new CategoryDTO(UUID.randomUUID(), "B2B SaaS", "software-technology-b2b-saas",
                "desc", null, categoryId, 1, true, now);
        var dto = new CategoryWithSubsDTO(categoryId, "Software & Technology", "software-technology",
                "desc", "\uD83D\uDCBB", 1, List.of(subDto));
        when(categoryService.getCategoryBySlug("software-technology")).thenReturn(dto);

        mockMvc.perform(get("/api/v1/categories/software-technology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Software & Technology"))
                .andExpect(jsonPath("$.subCategories[0].name").value("B2B SaaS"));
    }

    @Test
    void getCategoryBySlug_notFound_returns404() throws Exception {
        when(categoryService.getCategoryBySlug("unknown"))
                .thenThrow(new ResourceNotFoundException("Category not found: unknown"));

        mockMvc.perform(get("/api/v1/categories/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found: unknown"));
    }

    @Test
    void createCategory_returnsCreated() throws Exception {
        var request = new CreateCategoryRequest(
                "New Category", "new-category", "desc", "\uD83D\uDE80", null, 15);
        var dto = new CategoryDTO(UUID.randomUUID(), "New Category", "new-category",
                "desc", "\uD83D\uDE80", null, 15, true, now);
        when(categoryService.createCategory(any(CreateCategoryRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    void createCategory_invalidRequest_returnsBadRequest() throws Exception {
        var request = new CreateCategoryRequest("", "", null, null, null, null);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
