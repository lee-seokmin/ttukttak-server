package com.ttukttak.auth.presentation;

import com.ttukttak.auth.application.UserFacade;
import com.ttukttak.auth.presentation.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    private final UserFacade userFacade;

    @Override
    @GetMapping("/{handle}")
    public UserResponse get(@PathVariable String handle) {
        return userFacade.get(handle);
    }
}
