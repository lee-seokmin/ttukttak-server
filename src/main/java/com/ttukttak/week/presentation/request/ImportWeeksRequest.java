package com.ttukttak.week.presentation.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ttukttak.domain.week.domain.*;
import com.ttukttak.week.presentation.common.WeekSchedule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.Objects;

import static com.ttukttak.week.presentation.common.WeekSchedule.instant;

/** weeks.json을 그대로 받아요. labels, owner, 집계 숫자처럼 쓰지 않는 필드는 무시해요. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ImportWeeksRequest(@NotEmpty(message = "주차를 하나 이상 넣어 주세요.") List<@Valid Item> weeks) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            @NotNull @Pattern(regexp = "^\\d{4}-w\\d{2}$", message = "2026-w47 형식이에요.") String id,
            @NotNull(message = "theme 또는 break예요.") WeekType type,
            Integer round,
            @NotBlank(message = "코드를 입력해 주세요.") String code,
            @NotBlank(message = "제목을 입력해 주세요.") String title,
            String announcement,
            String intent,
            String rule,
            List<String> constraints,
            Difficulty difficulty,
            WeekStatus status,
            Examples examples,
            @NotNull(message = "일정을 입력해 주세요.") @Valid WeekSchedule schedule,
            String notes
    ) {
        Week toDomain() {
            return Week.builder()
                    .id(id)
                    .type(type)
                    .round(round)
                    .code(code)
                    .title(title)
                    .announcement(Objects.requireNonNullElse(announcement, ""))
                    .intent(Objects.requireNonNullElse(intent, ""))
                    .rule(rule)
                    .constraints(Objects.requireNonNullElse(constraints, List.of()))
                    .difficulty(difficulty)
                    .status(Objects.requireNonNullElse(status, type == WeekType.BREAK ? WeekStatus.BREAK : WeekStatus.DRAFT))
                    .examples(examples)
                    .opensAt(instant(schedule.opensAt()))
                    .submissionClosesAt(instant(schedule.submissionClosesAt()))
                    .votingClosesAt(instant(schedule.votingClosesAt()))
                    .resultsAt(instant(schedule.resultsAt()))
                    .notes(Objects.requireNonNullElse(notes, ""))
                    .build();
        }
    }

    public List<Week> toDomain() {
        return weeks.stream().map(Item::toDomain).toList();
    }
}
