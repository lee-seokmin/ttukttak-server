package com.ttukttak.week.presentation.response;

import com.ttukttak.domain.week.domain.Phase;
import com.ttukttak.domain.week.domain.Week;
import com.ttukttak.domain.week.domain.WeekType;
import com.ttukttak.week.presentation.common.WeekSchedule;

import java.time.Instant;
import java.util.List;

/** GET /weeks/:weekId 응답. 공개 전 주차는 주제 문구가 null이에요. */
public record WeekDetailResponse(Detail week, Stats stats, Object podium, Neighbor prev, Neighbor next) {

    public record Detail(String id, String code, WeekType type, Integer round, String title,
                         WeekSchedule schedule, Phase phase, int submissionCount,
                         String announcement, String intent, List<String> constraints) {
    }

    // ponytail: submissions, votes, completionRate, podium은 제출작·투표 도메인이 생기면 채워요.
    public record Stats(long submissions, long participants, long votes, double completionRate) {
    }

    public record Neighbor(String id, String code, String title, Phase phase) {
        static Neighbor from(Week week, Instant now) {
            return week == null ? null
                    : new Neighbor(week.getId(), week.getCode(), week.isOpen(now) ? week.getTitle() : null, week.phase(now));
        }
    }

    public static WeekDetailResponse of(Week week, long participants, Week prev, Week next, Instant now) {
        boolean open = week.isOpen(now);
        WeekSummary summary = WeekSummary.from(week, now);
        return new WeekDetailResponse(
                new Detail(summary.id(), summary.code(), summary.type(), summary.round(), summary.title(),
                        summary.schedule(), summary.phase(), summary.submissionCount(),
                        open ? week.getAnnouncement() : null,
                        open ? week.getIntent() : null,
                        open ? week.getConstraints() : null),
                new Stats(0, participants, 0, 0),
                null,
                Neighbor.from(prev, now),
                Neighbor.from(next, now));
    }
}
