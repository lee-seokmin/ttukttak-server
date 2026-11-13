package com.ttukttak.domain.week.infrastructure;

import com.ttukttak.domain.week.domain.WeekType;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

// 주차 id('2026-w47')는 글자 순서가 곧 시간 순서라 id로 정렬해요.
interface WeekJpaRepository extends JpaRepository<WeekJpaEntity, String> {
    Optional<WeekJpaEntity> findFirstByOpensAtLessThanEqualOrderByOpensAtDesc(Instant now);

    Optional<WeekJpaEntity> findFirstByOpensAtGreaterThanOrderByOpensAtAsc(Instant now);

    List<WeekJpaEntity> findByTypeAndResultsAtLessThanEqualAndIdLessThanOrderByIdDesc(
            WeekType type, Instant now, String cursor, Limit limit);

    Optional<WeekJpaEntity> findFirstByTypeAndIdLessThanOrderByIdDesc(WeekType type, String id);

    Optional<WeekJpaEntity> findFirstByTypeAndIdGreaterThanOrderByIdAsc(WeekType type, String id);

    @Modifying(clearAutomatically = true)
    @Query("update WeekJpaEntity w set w.presses = w.presses + :count where w.id = :id")
    void addPresses(String id, int count);

    @Query("select w.presses from WeekJpaEntity w where w.id = :id")
    long presses(String id);
}
