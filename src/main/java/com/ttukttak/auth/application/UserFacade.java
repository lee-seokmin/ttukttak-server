package com.ttukttak.auth.application;

import com.ttukttak.auth.presentation.request.UpdateUserRequest;
import com.ttukttak.auth.presentation.response.MeResponse;
import com.ttukttak.auth.presentation.response.UserResponse;
import com.ttukttak.domain.user.application.command.UserCommandService;
import com.ttukttak.domain.user.application.query.UserQueryService;
import com.ttukttak.domain.user.domain.User;
import com.ttukttak.domain.week.application.query.WeekQueryService;
import com.ttukttak.domain.week.domain.Week;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/** 요청·응답 DTO와 도메인 사이를 이어줘요. 트랜잭션은 각 서비스가 가져요. */
@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final WeekQueryService weekQueryService;

    public UserResponse get(String handle) {
        return UserResponse.from(userQueryService.get(handle));
    }

    public MeResponse me(UUID id) {
        return me(userQueryService.get(id));
    }

    public MeResponse updateMe(UUID id, UpdateUserRequest request) {
        var links = request.links();
        return me(userCommandService.updateProfile(id, request.name(), request.bio(),
                links == null ? null : links.github(),
                links == null ? null : links.blog()));
    }

    public void deleteMe(UUID id) {
        userCommandService.delete(id);
    }

    private MeResponse me(User user) {
        Week week = weekQueryService.current(Instant.now()).orElse(null);
        return week == null
                ? MeResponse.from(user, null, false)
                : MeResponse.from(user, week.getId(), weekQueryService.isJoined(week.getId(), user.getId()));
    }
}
