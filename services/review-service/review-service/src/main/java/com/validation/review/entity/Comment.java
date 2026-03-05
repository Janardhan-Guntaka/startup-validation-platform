package com.validation.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "post_id")
    private UUID postId;

    @NotNull
    @Column(name = "user_id")
    private UUID userId;

    @NotBlank
    @Size(max = 2000)
    @Column(length = 2000)
    private String content;

    @Column(name = "parent_comment_id")
    private UUID parentCommentId;

    @NotNull
    @Builder.Default
    private Integer upvotes = 0;

    private String sentiment;

    private Double sentimentConfidence;

    @Builder.Default
    private Boolean isEdited = false;

    @Builder.Default
    private Boolean isDeleted = false;

    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
