package com.ttukttak.domain.week.infrastructure;

import com.ttukttak.domain.week.domain.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
class ParticipationRepositoryImpl implements ParticipationRepository {
    private final ParticipationJpaRepository jpaRepository;

    @Override
    public void join(String weekId, UUID userId) {
        jpaRepository.join(weekId, userId);
    }

    @Override
    public void leave(String weekId, UUID userId) {
        jpaRepository.leave(weekId, userId);
    }

    @Override
    public boolean isJoined(String weekId, UUID userId) {
        return jpaRepository.existsByWeekIdAndUserId(weekId, userId);
    }

    @Override
    public long count(String weekId) {
        return jpaRepository.countByWeekId(weekId);
    }
}
