package com.ttukttak.domain.week.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 지금 이 순간 주차가 어느 단계인지. 저장하지 않고 시각으로 계산해요. */
@Getter
@RequiredArgsConstructor
public enum Phase {
    UPCOMING("공개 전"),
    BUILDING("제작 중"),     // 공개 ~ 제출 마감 (월 10:00 ~ 목 10:00)
    VOTING("투표 중"),       // 제출 마감 ~ 투표 마감 (목 10:00 ~ 일 24:00)
    TALLYING("집계 중"),     // 투표 마감 ~ 결과 발표 (일 24:00 ~ 월 10:00)
    CLOSED("결과 발표"),
    BREAK("휴식");

    private final String description;
}
