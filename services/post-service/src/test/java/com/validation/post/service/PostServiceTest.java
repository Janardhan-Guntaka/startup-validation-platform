package com.validation.post.service;

import com.validation.post.dto.CreatePostRequest;
import com.validation.post.dto.PostDTO;
import com.validation.post.entity.AskType;
import com.validation.post.entity.Category;
import com.validation.post.entity.Post;
import com.validation.post.exception.ResourceNotFoundException;
import com.validation.post.repository.CategoryRepository;
import com.validation.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PostService postService;

    private Category category;
    private Post post;
    private UUID categoryId;
    private UUID authorId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        authorId = UUID.randomUUID();

        category = Category.builder()
                .id(categoryId)
                .name("Software & Technology")
                .slug("software-technology")
                .description("Software startups")
                .icon("\uD83D\uDCBB")
                .displayOrder(1)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        post = Post.builder()
                .id(UUID.randomUUID())
                .categoryId(categoryId)
                .authorId(authorId)
                .title("My Startup Idea for Validation")
                .problem("There is a significant problem in the market that needs solving urgently by startups")
                .solution("We have built an innovative solution that addresses this problem effectively for users")
                .targetCustomer("Small business owners and entrepreneurs who need help")
                .traction("100 beta users signed up")
                .askType(AskType.DEMAND_VALIDATION)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createPost_savesAndReturnsDTO() {
        var request = new CreatePostRequest(
                categoryId, authorId,
                "My Startup Idea for Validation",
                "There is a significant problem in the market that needs solving urgently by startups",
                "We have built an innovative solution that addresses this problem effectively for users",
                "Small business owners and entrepreneurs who need help",
                "100 beta users",
                AskType.DEMAND_VALIDATION);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostDTO result = postService.createPost(request);

        assertThat(result.title()).isEqualTo("My Startup Idea for Validation");
        assertThat(result.askType()).isEqualTo(AskType.DEMAND_VALIDATION);
        assertThat(result.category()).isNotNull();
        assertThat(result.category().name()).isEqualTo("Software & Technology");
    }

    @Test
    void createPost_categoryNotFound_throwsException() {
        var request = new CreatePostRequest(
                categoryId, authorId,
                "My Startup Idea for Validation",
                "There is a significant problem in the market that needs solving urgently by startups",
                "We have built an innovative solution that addresses this problem effectively for users",
                "Small business owners and entrepreneurs who need help",
                null, AskType.PRICING);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.createPost(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPost_returnsPostDTO() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        PostDTO result = postService.getPost(post.getId());

        assertThat(result.id()).isEqualTo(post.getId());
        assertThat(result.title()).isEqualTo(post.getTitle());
    }

    @Test
    void getPost_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPost(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPostsByCategory_returnsPage() {
        Page<Post> page = new PageImpl<>(List.of(post));
        when(postRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId, PageRequest.of(0, 20)))
                .thenReturn(page);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Page<PostDTO> result = postService.getPostsByCategory(categoryId, 0, 20);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).title()).isEqualTo(post.getTitle());
    }

    @Test
    void getPostsByAuthor_returnsPage() {
        Page<Post> page = new PageImpl<>(List.of(post));
        when(postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, PageRequest.of(0, 20)))
                .thenReturn(page);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Page<PostDTO> result = postService.getPostsByAuthor(authorId, 0, 20);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}
