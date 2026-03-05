package com.validation.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reactions", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "post_id")
    @NotNull
    private UUID postId;

    @Column(name = "user_id")
    @NotNull
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ReactionType reactionType;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
