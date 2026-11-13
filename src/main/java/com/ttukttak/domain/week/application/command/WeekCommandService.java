package com.ttukttak.domain.week.application.command;

import com.ttukttak.domain.week.domain.*;
import com.ttukttak.domain.week.exception.WeekNotFoundException;
import com.ttukttak.domain.week.exception.WeekPhaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WeekCommandService {
    private final WeekRepository weekRepository;
    private final ParticipationRepository participationRepository;

    /** id가 같으면 덮어써요. 버튼 누른 횟수는 그대로 둬요. 하나라도 틀리면 모두 취소돼요. */
    public void importWeeks(List<Week> weeks) {
        for (Week week : weeks) {
            week.validate();
            long presses = weekRepository.findById(week.getId()).map(Week::getPresses).orElse(0L);
            weekRepository.save(week.toBuilder().presses(presses).build());
        }
    }

    // ponytail: 요청 제한(세션당 초당 5회)은 아직 없어요. 장난이 심해지면 붙여요.
    public long press(int count, Instant now) {
        return weekRepository.addPresses(current(now).getId(), count);
    }

    public long join(UUID userId, Instant now) {
        Week week = building(now);
        participationRepository.join(week.getId(), userId);
        return participationRepository.count(week.getId());
    }

    public long leave(UUID userId, Instant now) {
        Week week = building(now);
        participationRepository.leave(week.getId(), userId);
        return participationRepository.count(week.getId());
    }

    // 참가는 제작 기간에만 바꿀 수 있어요.
    private Week building(Instant now) {
        Week week = current(now);
        Phase phase = week.phase(now);
        if (phase == Phase.BREAK) throw WeekPhaseException.breakWeek();
        if (phase != Phase.BUILDING) throw WeekPhaseException.submissionClosed();
        return week;
    }

    private Week current(Instant now) {
        return weekRepository.findLatestOpened(now)
                .filter(week -> week.phase(now) != Phase.CLOSED)
                .orElseThrow(WeekNotFoundException::new);
    }
}
