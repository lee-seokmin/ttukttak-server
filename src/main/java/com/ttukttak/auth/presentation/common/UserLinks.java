package com.ttukttak.auth.presentation.common;

import jakarta.validation.constraints.Pattern;

/** 수정할 때 빈 문자열을 보내면 링크를 지워요. */
public record UserLinks(
        @Pattern(regexp = "^(https://.+)?$", message = "https://로 시작하는 주소여야 해요.") String github,
        @Pattern(regexp = "^(https://.+)?$", message = "https://로 시작하는 주소여야 해요.") String blog
) {
}
