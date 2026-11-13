package com.ttukttak.week.presentation.common;

import com.ttukttak.domain.week.domain.Week;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/** 휴식 주는 마감 일정이 모두 null이에요. */
public record WeekSchedule(
        @NotNull(message = "공개 시각을 입력해 주세요.") OffsetDateTime opensAt,
        OffsetDateTime submissionClosesAt,
        OffsetDateTime votingClosesAt,
        OffsetDateTime resultsAt
) {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public static WeekSchedule from(Week week) {
        return new WeekSchedule(kst(week.getOpensAt()), kst(week.getSubmissionClosesAt()),
                kst(week.getVotingClosesAt()), kst(week.getResultsAt()));
    }

    public static OffsetDateTime kst(Instant at) {
        return at == null ? null : at.atZone(KST).toOffsetDateTime();
    }

    public static Instant instant(OffsetDateTime at) {
        return at == null ? null : at.toInstant();
    }
}
