package com.ttukttak.domain.week.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeekType {
    THEME("주제"),
    BREAK("휴식");

    private final String description;
}
