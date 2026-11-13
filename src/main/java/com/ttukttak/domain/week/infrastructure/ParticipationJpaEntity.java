package com.ttukttak.domain.week.infrastructure;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

// 테이블 모양만 정해요. 넣고 빼는 건 ParticipationJpaRepository의 쿼리로 해요.
@Entity
@Table(name = "participations")
@IdClass(ParticipationJpaEntity.Key.class)
@NoArgsConstructor(access = PROTECTED)
class ParticipationJpaEntity {
    @Id
    private String weekId;

    @Id
    private UUID userId;

    @Column(nullable = false)
    private Instant joinedAt;

    record Key(String weekId, UUID userId) implements Serializable {
    }
}
