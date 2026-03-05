package com.validation.post.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a startup validation post submitted by a founder.
 */
@Entity
@Table(name = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @NotNull
    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @NotBlank
    @Size(min = 10, max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank
    @Size(min = 50, max = 2000)
    @Column(nullable = false, length = 2000)
    private String problem;

    @NotBlank
    @Size(min = 50, max = 2000)
    @Column(nullable = false, length = 2000)
    private String solution;

    @NotBlank
    @Size(min = 20, max = 500)
    @Column(name = "target_customer", nullable = false, length = 500)
    private String targetCustomer;

    @Size(max = 1000)
    @Column(length = 1000)
    private String traction;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ask_type", nullable = false)
    private AskType askType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
