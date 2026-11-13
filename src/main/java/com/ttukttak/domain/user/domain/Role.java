package com.ttukttak.domain.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    MEMBER("멤버"),
    ADMIN("관리자");

    private final String description;
}
