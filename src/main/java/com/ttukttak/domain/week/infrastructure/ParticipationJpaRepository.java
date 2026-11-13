package com.ttukttak.domain.week.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

interface ParticipationJpaRepository extends JpaRepository<ParticipationJpaEntity, ParticipationJpaEntity.Key> {
    @Modifying
    @Query(value = """
            insert into participations (week_id, user_id, joined_at) values (:weekId, :userId, now())
            on conflict do nothing""", nativeQuery = true)
    void join(String weekId, UUID userId);

    @Modifying
    @Query("delete from ParticipationJpaEntity p where p.weekId = :weekId and p.userId = :userId")
    void leave(String weekId, UUID userId);

    boolean existsByWeekIdAndUserId(String weekId, UUID userId);

    long countByWeekId(String weekId);
}
