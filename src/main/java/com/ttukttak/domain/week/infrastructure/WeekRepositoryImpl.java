package com.ttukttak.domain.week.infrastructure;

import com.ttukttak.domain.week.domain.Week;
import com.ttukttak.domain.week.domain.WeekRepository;
import com.ttukttak.domain.week.domain.WeekType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class WeekRepositoryImpl implements WeekRepository {
    private static final String AFTER_ALL = "9999-w99"; // 커서가 없으면 모든 id보다 뒤

    private final WeekJpaRepository jpaRepository;

    @Override
    public Week save(Week week) {
        WeekJpaEntity entity = jpaRepository.findById(week.getId()).orElse(null);
        if (entity == null) return jpaRepository.saveAndFlush(WeekJpaEntity.from(week)).toDomain();
        entity.overwrite(week);
        jpaRepository.flush();
        return entity.toDomain();
    }

    @Override
    public Optional<Week> findById(String id) {
        return jpaRepository.findById(id).map(WeekJpaEntity::toDomain);
    }

    @Override
    public Optional<Week> findLatestOpened(Instant now) {
        return jpaRepository.findFirstByOpensAtLessThanEqualOrderByOpensAtDesc(now).map(WeekJpaEntity::toDomain);
    }

    @Override
    public Optional<Week> findFirstUpcoming(Instant now) {
        return jpaRepository.findFirstByOpensAtGreaterThanOrderByOpensAtAsc(now).map(WeekJpaEntity::toDomain);
    }

    @Override
    public List<Week> findClosedThemes(Instant now, String cursor, int limit) {
        return jpaRepository.findByTypeAndResultsAtLessThanEqualAndIdLessThanOrderByIdDesc(
                        WeekType.THEME, now, cursor == null ? AFTER_ALL : cursor, Limit.of(limit))
                .stream().map(WeekJpaEntity::toDomain).toList();
    }

    @Override
    public Optional<Week> findPrevTheme(String id) {
        return jpaRepository.findFirstByTypeAndIdLessThanOrderByIdDesc(WeekType.THEME, id).map(WeekJpaEntity::toDomain);
    }

    @Override
    public Optional<Week> findNextTheme(String id) {
        return jpaRepository.findFirstByTypeAndIdGreaterThanOrderByIdAsc(WeekType.THEME, id).map(WeekJpaEntity::toDomain);
    }

    @Override
    public long addPresses(String id, int count) {
        jpaRepository.addPresses(id, count);
        return jpaRepository.presses(id);
    }
}
