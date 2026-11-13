package com.ttukttak.auth.presentation.response;

import com.ttukttak.auth.presentation.common.UserLinks;
import com.ttukttak.auth.presentation.common.UserSummary;
import com.ttukttak.domain.user.domain.User;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

/** GET /users/:handle 응답. social, viewer, 나머지 stats는 팔로우·제출 도메인이 생기면 채워요. */
public record UserResponse(Profile user, Stats stats) {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public record Profile(UUID id, String handle, String name, String avatarUrl, String initials,
                          String bio, UserLinks links, OffsetDateTime joinedAt) {
    }

    public record Stats(int currentStreak, int bestStreak) {
    }

    public static UserResponse from(User user) {
        return new UserResponse(
                new Profile(
                        user.getId(),
                        user.getHandle(),
                        user.getName(),
                        user.getAvatarUrl(),
                        UserSummary.initials(user.getHandle()),
                        user.getBio(),
                        new UserLinks(user.getGithubLink(), user.getBlogLink()),
                        user.getJoinedAt().atZone(KST).toOffsetDateTime()),
                new Stats(user.getCurrentStreak(), user.getBestStreak()));
    }
}
