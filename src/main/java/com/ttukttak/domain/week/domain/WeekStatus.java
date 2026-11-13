package com.ttukttak.domain.week.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 운영자가 관리하는 준비 상태. 지금 단계(Phase)와는 달라도 괜찮아요. */
@Getter
@RequiredArgsConstructor
public enum WeekStatus {
    DRAFT("준비 중"),
    ANNOUNCED("공지 완료"),
    BUILDING("진행 중"),
    VOTING("투표 중"),
    CLOSED("종료"),
    BREAK("휴식");

    private final String description;
}
