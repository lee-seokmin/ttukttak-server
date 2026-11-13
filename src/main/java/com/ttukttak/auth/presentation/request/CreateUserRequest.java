package com.ttukttak.auth.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotNull(message = "GitHub 계정 번호가 필요해요.")
        Long githubId,

        @NotNull(message = "아이디를 입력해 주세요.")
        @Pattern(regexp = "^[a-z0-9][a-z0-9._-]{0,38}$", message = "아이디는 영어 소문자, 숫자, . _ -로 39자까지 써요.")
        String handle,

        @NotBlank(message = "이름을 입력해 주세요.")
        @Size(max = 20, message = "이름은 20자까지 써요.")
        String name,

        @Size(max = 2048, message = "주소는 2048자까지 써요.") String avatarUrl
) {
}
