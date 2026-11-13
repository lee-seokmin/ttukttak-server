package com.ttukttak.auth.presentation.response;

import com.ttukttak.auth.presentation.common.UserSummary;
import com.ttukttak.domain.user.domain.User;

/** GET /me 응답. 진행 중인 주차가 없으면 currentWeek가 null이에요. */
public record MeResponse(UserSummary user, String role, CurrentWeek currentWeek, int points) {

    // ponytail: submissionId는 제출작 도메인이 생기면 채워요.
    public record CurrentWeek(String weekId, boolean joined, String submissionId, int streak) {
    }

    public static MeResponse from(User user, String currentWeekId, boolean joined) {
        return new MeResponse(UserSummary.from(user), user.getRole().name().toLowerCase(),
                currentWeekId == null ? null : new CurrentWeek(currentWeekId, joined, null, user.getCurrentStreak()),
                user.getPoints());
    }
}
