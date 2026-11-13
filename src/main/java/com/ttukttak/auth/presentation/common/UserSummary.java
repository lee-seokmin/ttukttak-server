package com.ttukttak.auth.presentation.common;

import com.ttukttak.domain.user.domain.User;

import java.util.UUID;

/** 명세의 UserSummary. 작성자·목록 등 사용자를 짧게 보여줄 때 써요. */
public record UserSummary(UUID id, String handle, String name, String avatarUrl, String initials) {

    public static UserSummary from(User user) {
        return new UserSummary(user.getId(), user.getHandle(), user.getName(), user.getAvatarUrl(),
                initials(user.getHandle()));
    }

    public static String initials(String handle) {
        return handle.substring(0, Math.min(2, handle.length())).toUpperCase();
    }
}
