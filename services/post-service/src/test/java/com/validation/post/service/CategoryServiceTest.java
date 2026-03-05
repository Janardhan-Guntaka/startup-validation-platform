package com.validation.post.service;

import com.validation.post.dto.CategoryDTO;
import com.validation.post.dto.CategoryWithSubsDTO;
import com.validation.post.dto.CreateCategoryRequest;
import com.validation.post.entity.Category;
import com.validation.post.exception.ResourceNotFoundException;
import com.validation.post.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category parentCategory;
    private Category subCategory;

    @BeforeEach
    void setUp() {
        parentCategory = Category.builder()
                .id(UUID.randomUUID())
                .name("Software & Technology")
                .slug("software-technology")
                .description("Software startups")
                .icon("\uD83D\uDCBB")
                .displayOrder(1)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        subCategory = Category.builder()
                .id(UUID.randomUUID())
                .name("B2B SaaS")
                .slug("software-technology-b2b-saas")
                .description("B2B SaaS in Software & Technology")
                .parentCategoryId(parentCategory.getId())
                .displayOrder(1)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getTopLevelCategories_returnsCategories() {
        when(categoryRepository.findByParentCategoryIdIsNullOrderByDisplayOrder())
                .thenReturn(List.of(parentCategory));

        List<CategoryDTO> result = categoryService.getTopLevelCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Software & Technology");
        assertThat(result.get(0).slug()).isEqualTo("software-technology");
    }

    @Test
    void getCategoryBySlug_returnsWithSubCategories() {
        when(categoryRepository.findBySlug("software-technology"))
                .thenReturn(Optional.of(parentCategory));
        when(categoryRepository.findByParentCategoryIdOrderByDisplayOrder(parentCategory.getId()))
                .thenReturn(List.of(subCategory));

        CategoryWithSubsDTO result = categoryService.getCategoryBySlug("software-technology");

        assertThat(result.name()).isEqualTo("Software & Technology");
        assertThat(result.subCategories()).hasSize(1);
        assertThat(result.subCategories().get(0).name()).isEqualTo("B2B SaaS");
    }

    @Test
    void getCategoryBySlug_notFound_throwsException() {
        when(categoryRepository.findBySlug("unknown"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryBySlug("unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    void getSubCategories_returnsList() {
        when(categoryRepository.findByParentCategoryIdOrderByDisplayOrder(parentCategory.getId()))
                .thenReturn(List.of(subCategory));

        List<CategoryDTO> result = categoryService.getSubCategories(parentCategory.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("B2B SaaS");
    }

    @Test
    void createCategory_savesAndReturnsDTO() {
        var request = new CreateCategoryRequest(
                "New Category", "new-category", "Description", "\uD83D\uDE80", null, 15);

        Category saved = Category.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .icon(request.icon())
                .displayOrder(request.displayOrder())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryDTO result = categoryService.createCategory(request);

        assertThat(result.name()).isEqualTo("New Category");
        assertThat(result.slug()).isEqualTo("new-category");
    }
}
