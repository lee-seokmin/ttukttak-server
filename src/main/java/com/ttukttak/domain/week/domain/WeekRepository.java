package com.ttukttak.domain.week.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WeekRepository {
    Week save(Week week);

    Optional<Week> findById(String id);

    /** 이미 열린 주차 중 가장 최근 것 */
    Optional<Week> findLatestOpened(Instant now);

    /** 아직 안 열린 주차 중 가장 가까운 것 */
    Optional<Week> findFirstUpcoming(Instant now);

    /** 결과가 나온 주제 주, 최신순. cursor(주차 id)보다 앞선 것만 */
    List<Week> findClosedThemes(Instant now, String cursor, int limit);

    Optional<Week> findPrevTheme(String id);

    Optional<Week> findNextTheme(String id);

    /** 버튼 누른 횟수를 더하고 새 합계를 돌려줘요. */
    long addPresses(String id, int count);
}
