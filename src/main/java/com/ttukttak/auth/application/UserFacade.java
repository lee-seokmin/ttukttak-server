package com.ttukttak.auth.application;

import com.ttukttak.auth.presentation.request.UpdateUserRequest;
import com.ttukttak.auth.presentation.response.MeResponse;
import com.ttukttak.auth.presentation.response.UserResponse;
import com.ttukttak.domain.user.application.command.UserCommandService;
import com.ttukttak.domain.user.application.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** 요청·응답 DTO와 도메인 사이를 이어줘요. 트랜잭션은 각 서비스가 가져요. */
@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public UserResponse get(String handle) {
        return UserResponse.from(userQueryService.get(handle));
    }

    public MeResponse me(UUID id) {
        return MeResponse.from(userQueryService.get(id));
    }

    public MeResponse updateMe(UUID id, UpdateUserRequest request) {
        var links = request.links();
        return MeResponse.from(userCommandService.updateProfile(id, request.name(), request.bio(),
                links == null ? null : links.github(),
                links == null ? null : links.blog()));
    }

    public void deleteMe(UUID id) {
        userCommandService.delete(id);
    }
}
