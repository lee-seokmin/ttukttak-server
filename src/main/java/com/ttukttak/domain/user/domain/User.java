package com.ttukttak.domain.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class User {
    private UUID id;
    private Long githubId;
    private String handle;
    private String name;

    private String avatarUrl;
    private String bio;
    private String githubLink;
    private String blogLink;

    private Role role;
    private int points;

    private int currentStreak;
    private int bestStreak;

    private Instant joinedAt;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public static User create(Long githubId, String handle, String name, String avatarUrl) {
        return User.builder()
                .githubId(githubId)
                .handle(handle)
                .name(name)
                .avatarUrl(avatarUrl)
                .bio("")
                .role(Role.MEMBER)
                .build();
    }

    /** null은 그대로 두고, 링크는 빈 문자열이면 지워요. */
    public void updateProfile(String name, String bio, String githubLink, String blogLink) {
        if (name != null) this.name = name;
        if (bio != null) this.bio = bio;
        if (githubLink != null) this.githubLink = githubLink.isEmpty() ? null : githubLink;
        if (blogLink != null) this.blogLink = blogLink.isEmpty() ? null : blogLink;
    }

    public void delete() {
        this.deletedAt = Instant.now();
    }
}
