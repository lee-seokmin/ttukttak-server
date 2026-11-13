package com.ttukttak.domain.week.domain;

import java.util.UUID;

/** "이번 주 참가하기". 완주율의 분모예요. */
public interface ParticipationRepository {
    /** 여러 번 불러도 한 번만 세요. */
    void join(String weekId, UUID userId);

    void leave(String weekId, UUID userId);

    boolean isJoined(String weekId, UUID userId);

    long count(String weekId);
}
