package com.ttukttak.week.presentation.response;

import com.ttukttak.domain.week.domain.*;
import com.ttukttak.week.presentation.common.WeekSchedule;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

/** GET /weeks/current 응답. 진행 중인 주차가 없으면 week, stats가 null이에요. */
public record CurrentWeekResponse(Current week, Stats stats, NextWeek nextWeek, OffsetDateTime serverTime) {

    public record Current(String id, String code, WeekType type, Integer round, String title,
                          String announcement, String rule, List<String> constraints, Difficulty difficulty,
                          Examples examples, WeekSchedule schedule, Phase phase) {
    }

    // ponytail: submissions는 제출작 도메인이 생기면 채워요.
    public record Stats(long participants, long submissions, long presses) {
    }

    public record NextWeek(String id, OffsetDateTime opensAt) {
    }

    public static CurrentWeekResponse of(Week week, long participants, Week next, Instant now) {
        Current current = week == null ? null : new Current(week.getId(), week.getCode(), week.getType(),
                week.getRound(), week.getTitle(), week.getAnnouncement(), week.getRule(), week.getConstraints(),
                week.getDifficulty(), week.getExamples(), WeekSchedule.from(week), week.phase(now));
        return new CurrentWeekResponse(
                current,
                week == null ? null : new Stats(participants, 0, week.getPresses()),
                next == null ? null : new NextWeek(next.getId(), WeekSchedule.kst(next.getOpensAt())),
                WeekSchedule.kst(now));
    }
}
