package com.ttukttak.auth.presentation;

import com.ttukttak.auth.application.UserFacade;
import com.ttukttak.auth.presentation.request.CreateUserRequest;
import com.ttukttak.auth.presentation.request.UpdateUserRequest;
import com.ttukttak.auth.presentation.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// ponytail: 로그인 전 임시 CRUD. GitHub 로그인이 붙으면 생성은 콜백으로, 수정·삭제는 /me로 옮겨요.
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    private final UserFacade userFacade;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userFacade.create(request);
    }

    @Override
    @GetMapping("/{handle}")
    public UserResponse get(@PathVariable String handle) {
        return userFacade.get(handle);
    }

    @Override
    @PatchMapping("/{handle}")
    public UserResponse update(@PathVariable String handle, @Valid @RequestBody UpdateUserRequest request) {
        return userFacade.update(handle, request);
    }

    @Override
    @DeleteMapping("/{handle}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String handle) {
        userFacade.delete(handle);
    }
}
