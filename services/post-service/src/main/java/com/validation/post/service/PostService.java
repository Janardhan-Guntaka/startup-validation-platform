package com.validation.post.service;

import com.validation.post.dto.CategoryDTO;
import com.validation.post.dto.CreatePostRequest;
import com.validation.post.dto.PostDTO;
import com.validation.post.entity.Category;
import com.validation.post.entity.Post;
import com.validation.post.exception.ResourceNotFoundException;
import com.validation.post.mapper.CategoryMapper;
import com.validation.post.mapper.PostMapper;
import com.validation.post.repository.CategoryRepository;
import com.validation.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service layer for post operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Creates a new post after validating the category exists.
     */
    @Transactional
    public PostDTO createPost(CreatePostRequest request) {
        log.debug("Creating post: {}", request.title());
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found: " + request.categoryId()));

        Post post = PostMapper.toEntity(request);
        Post saved = postRepository.save(post);
        return PostMapper.toDTO(saved, CategoryMapper.toDTO(category));
    }

    /**
     * Returns a single post by ID.
     */
    @Transactional(readOnly = true)
    public PostDTO getPost(UUID id) {
        log.debug("Fetching post: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));

        CategoryDTO category = categoryRepository.findById(post.getCategoryId())
                .map(CategoryMapper::toDTO)
                .orElse(null);

        return PostMapper.toDTO(post, category);
    }

    /**
     * Returns a page of posts filtered by category.
     */
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsByCategory(UUID categoryId, int page, int size) {
        log.debug("Fetching posts for category: {}, page: {}, size: {}", categoryId, page, size);
        CategoryDTO category = categoryRepository.findById(categoryId)
                .map(CategoryMapper::toDTO)
                .orElse(null);

        return postRepository
                .findByCategoryIdOrderByCreatedAtDesc(categoryId, PageRequest.of(page, size))
                .map(post -> PostMapper.toDTO(post, category));
    }

    /**
     * Returns a page of posts filtered by author.
     */
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsByAuthor(UUID authorId, int page, int size) {
        log.debug("Fetching posts for author: {}, page: {}, size: {}", authorId, page, size);
        return postRepository
                .findByAuthorIdOrderByCreatedAtDesc(authorId, PageRequest.of(page, size))
                .map(post -> {
                    CategoryDTO category = categoryRepository.findById(post.getCategoryId())
                            .map(CategoryMapper::toDTO)
                            .orElse(null);
                    return PostMapper.toDTO(post, category);
                });
    }
}
