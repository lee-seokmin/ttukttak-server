package com.ttukttak.auth.presentation;

import com.ttukttak.auth.application.UserFacade;
import com.ttukttak.auth.presentation.request.UpdateUserRequest;
import com.ttukttak.auth.presentation.response.MeResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

// principal 이름은 우리 사용자 id예요 (GithubOAuth2UserService.principal).
@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeControllerImpl implements MeController {
    private final UserFacade userFacade;

    @Override
    @GetMapping
    public MeResponse get(Principal principal) {
        return userFacade.me(UUID.fromString(principal.getName()));
    }

    @Override
    @PatchMapping
    public MeResponse update(Principal principal, @Valid @RequestBody UpdateUserRequest request) {
        return userFacade.updateMe(UUID.fromString(principal.getName()), request);
    }

    @Override
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Principal principal, HttpSession session) {
        userFacade.deleteMe(UUID.fromString(principal.getName()));
        session.invalidate();
    }
}
