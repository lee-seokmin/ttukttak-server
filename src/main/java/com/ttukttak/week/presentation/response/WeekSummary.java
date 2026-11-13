package com.ttukttak.week.presentation.response;

import com.ttukttak.domain.week.domain.Phase;
import com.ttukttak.domain.week.domain.Week;
import com.ttukttak.domain.week.domain.WeekType;
import com.ttukttak.week.presentation.common.WeekSchedule;

import java.time.Instant;

/** 명세의 WeekSummary. 공개 전 주차는 title이 null이에요. */
public record WeekSummary(String id, String code, WeekType type, Integer round, String title,
                          WeekSchedule schedule, Phase phase, int submissionCount) {

    // ponytail: submissionCount는 제출작 도메인이 생기면 채워요.
    public static WeekSummary from(Week week, Instant now) {
        return new WeekSummary(week.getId(), week.getCode(), week.getType(), week.getRound(),
                week.isOpen(now) ? week.getTitle() : null, WeekSchedule.from(week), week.phase(now), 0);
    }
}
