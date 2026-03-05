package com.validation.post.repository;

import com.validation.post.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Category entity operations.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findBySlug(String slug);

    List<Category> findByParentCategoryIdIsNullOrderByDisplayOrder();

    List<Category> findByParentCategoryIdOrderByDisplayOrder(UUID parentId);
}
