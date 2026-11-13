package com.ttukttak.domain.week.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Difficulty {
    LOW("낮음"),
    MEDIUM("보통"),
    HIGH("높음");

    private final String description;
}
