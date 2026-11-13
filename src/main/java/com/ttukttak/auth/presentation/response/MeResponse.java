package com.ttukttak.auth.presentation.response;

import com.ttukttak.auth.presentation.common.UserSummary;
import com.ttukttak.domain.user.domain.User;

/** GET /me 응답. currentWeek는 주차 도메인이 생기면 채워요. */
public record MeResponse(UserSummary user, String role, Object currentWeek, int points) {

    public static MeResponse from(User user) {
        return new MeResponse(UserSummary.from(user), user.getRole().name().toLowerCase(), null, user.getPoints());
    }
}
