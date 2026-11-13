package com.ttukttak.auth.application;

import com.ttukttak.auth.presentation.request.CreateUserRequest;
import com.ttukttak.auth.presentation.request.UpdateUserRequest;
import com.ttukttak.auth.presentation.response.UserResponse;
import com.ttukttak.domain.user.application.command.UserCommandService;
import com.ttukttak.domain.user.application.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 요청·응답 DTO와 도메인 사이를 이어줘요. 트랜잭션은 각 서비스가 가져요. */
@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public UserResponse create(CreateUserRequest request) {
        return UserResponse.from(userCommandService.create(
                request.githubId(), request.handle(), request.name(), request.avatarUrl()));
    }

    public UserResponse get(String handle) {
        return UserResponse.from(userQueryService.get(handle));
    }

    public UserResponse update(String handle, UpdateUserRequest request) {
        var links = request.links();
        return UserResponse.from(userCommandService.updateProfile(handle, request.name(), request.bio(),
                links == null ? null : links.github(),
                links == null ? null : links.blog()));
    }

    public void delete(String handle) {
        userCommandService.delete(handle);
    }
}
