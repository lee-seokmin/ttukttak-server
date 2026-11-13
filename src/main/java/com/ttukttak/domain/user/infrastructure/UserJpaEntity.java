package com.ttukttak.domain.user.infrastructure;

import com.ttukttak.domain.user.domain.Role;
import com.ttukttak.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "users")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
class UserJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true) // 탈퇴하면 null
    private Long githubId;

    @Column(nullable = false, unique = true, length = 39)
    private String handle;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(length = 2048)
    private String avatarUrl;

    @Column(nullable = false, length = 160)
    private String bio;

    @Column(name = "link_github", length = 2048)
    private String githubLink;

    @Column(name = "link_blog", length = 2048)
    private String blogLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private int points;

    private int currentStreak;
    private int bestStreak;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant joinedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant deletedAt;

    static UserJpaEntity from(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.id = user.getId();
        entity.githubId = user.getGithubId();
        entity.handle = user.getHandle();
        entity.name = user.getName();
        entity.avatarUrl = user.getAvatarUrl();
        entity.bio = user.getBio();
        entity.githubLink = user.getGithubLink();
        entity.blogLink = user.getBlogLink();
        entity.role = user.getRole();
        entity.points = user.getPoints();
        entity.currentStreak = user.getCurrentStreak();
        entity.bestStreak = user.getBestStreak();
        entity.joinedAt = user.getJoinedAt();
        entity.createdAt = user.getCreatedAt();
        entity.updatedAt = user.getUpdatedAt();
        entity.deletedAt = user.getDeletedAt();
        return entity;
    }

    User toDomain() {
        return User.builder()
                .id(id)
                .githubId(githubId)
                .handle(handle)
                .name(name)
                .avatarUrl(avatarUrl)
                .bio(bio)
                .githubLink(githubLink)
                .blogLink(blogLink)
                .role(role)
                .points(points)
                .currentStreak(currentStreak)
                .bestStreak(bestStreak)
                .joinedAt(joinedAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }
}
