package com.ttukttak.auth.presentation.request;

import com.ttukttak.auth.presentation.common.UserLinks;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

/** 보낸 필드만 바뀌어요. */
public record UpdateUserRequest(
        @Size(min = 1, max = 20, message = "이름은 1–20자로 써요.")
        String name,

        @Size(max = 160, message = "소개는 160자까지 써요.")
        String bio,

        @Valid
        UserLinks links
) {
}
