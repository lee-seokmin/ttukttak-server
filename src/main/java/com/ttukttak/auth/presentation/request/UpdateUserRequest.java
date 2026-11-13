package com.ttukttak.auth.presentation.request;

import com.ttukttak.auth.presentation.common.UserLinks;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 보낸 필드만 바뀌어요. */
public record UpdateUserRequest(
        // null은 "안 바꿈"이라 @NotBlank 대신 @Pattern으로 공백만 있는 이름을 막아요.
        @Pattern(regexp = "(?s).*\\S.*", message = "이름을 입력해 주세요.")
        @Size(max = 20, message = "이름은 20자까지 써요.")
        String name,

        @Size(max = 160, message = "소개는 160자까지 써요.")
        String bio,

        @Valid
        UserLinks links
) {
}
