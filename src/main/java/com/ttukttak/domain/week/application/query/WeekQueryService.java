package com.ttukttak.domain.week.application.query;

import com.ttukttak.domain.week.domain.ParticipationRepository;
import com.ttukttak.domain.week.domain.Phase;
import com.ttukttak.domain.week.domain.Week;
import com.ttukttak.domain.week.domain.WeekRepository;
import com.ttukttak.domain.week.exception.WeekNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeekQueryService {
    private final WeekRepository weekRepository;
    private final ParticipationRepository participationRepository;

    /** 지금 진행 중인 주차. 가장 최근에 열린 주차가 결과까지 끝났으면 없어요. */
    public Optional<Week> current(Instant now) {
        return weekRepository.findLatestOpened(now).filter(week -> week.phase(now) != Phase.CLOSED);
    }

    public Optional<Week> next(Instant now) {
        return weekRepository.findFirstUpcoming(now);
    }

    public Week get(String id) {
        return weekRepository.findById(id).orElseThrow(WeekNotFoundException::new);
    }

    /** limit + 1개를 읽어서 다음 페이지가 있는지 알아봐요. */
    public List<Week> archive(Instant now, String cursor, int limit) {
        return weekRepository.findClosedThemes(now, cursor, limit + 1);
    }

    public Optional<Week> prevTheme(String id) {
        return weekRepository.findPrevTheme(id);
    }

    public Optional<Week> nextTheme(String id) {
        return weekRepository.findNextTheme(id);
    }

    public long participants(String weekId) {
        return participationRepository.count(weekId);
    }

    public boolean isJoined(String weekId, UUID userId) {
        return participationRepository.isJoined(weekId, userId);
    }
}
