package com.ttukttak.domain.week.infrastructure;

import com.ttukttak.domain.week.domain.*;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "weeks")
@NoArgsConstructor(access = PROTECTED)
class WeekJpaEntity {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeekType type;

    @Column(unique = true)
    private Integer round;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false, columnDefinition = "text")
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String announcement;

    @Column(nullable = false, columnDefinition = "text")
    private String intent;

    @Column(columnDefinition = "text")
    private String rule;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(nullable = false, columnDefinition = "text[]")
    private List<String> constraints;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeekStatus status;

    // 명세 스키마는 jsonb 한 칸이지만 키가 셋으로 정해져 있어 칸을 나눴어요.
    @Column(columnDefinition = "text")
    private String exampleBeginner;

    @Column(columnDefinition = "text")
    private String exampleRegular;

    @Column(columnDefinition = "text")
    private String exampleExpert;

    @Column(nullable = false)
    private Instant opensAt;

    private Instant submissionClosesAt;
    private Instant votingClosesAt;
    private Instant resultsAt;

    @Column(nullable = false, columnDefinition = "text")
    private String notes;

    // UPDATE 문에서 빠져요. 더하기는 WeekJpaRepository.addPresses만 해서 동시에 눌러도 잃지 않아요.
    @Column(updatable = false)
    private long presses;

    static WeekJpaEntity from(Week week) {
        WeekJpaEntity entity = new WeekJpaEntity();
        entity.id = week.getId();
        entity.presses = week.getPresses();
        entity.overwrite(week);
        return entity;
    }

    /** id와 presses만 빼고 덮어써요. */
    void overwrite(Week week) {
        type = week.getType();
        round = week.getRound();
        code = week.getCode();
        title = week.getTitle();
        announcement = week.getAnnouncement();
        intent = week.getIntent();
        rule = week.getRule();
        constraints = week.getConstraints();
        difficulty = week.getDifficulty();
        status = week.getStatus();
        Examples examples = week.getExamples();
        exampleBeginner = examples == null ? null : examples.beginner();
        exampleRegular = examples == null ? null : examples.regular();
        exampleExpert = examples == null ? null : examples.expert();
        opensAt = week.getOpensAt();
        submissionClosesAt = week.getSubmissionClosesAt();
        votingClosesAt = week.getVotingClosesAt();
        resultsAt = week.getResultsAt();
        notes = week.getNotes();
    }

    Week toDomain() {
        boolean hasExamples = exampleBeginner != null || exampleRegular != null || exampleExpert != null;
        return Week.builder()
                .id(id)
                .type(type)
                .round(round)
                .code(code)
                .title(title)
                .announcement(announcement)
                .intent(intent)
                .rule(rule)
                .constraints(constraints)
                .difficulty(difficulty)
                .status(status)
                .examples(hasExamples ? new Examples(exampleBeginner, exampleRegular, exampleExpert) : null)
                .opensAt(opensAt)
                .submissionClosesAt(submissionClosesAt)
                .votingClosesAt(votingClosesAt)
                .resultsAt(resultsAt)
                .notes(notes)
                .presses(presses)
                .build();
    }
}
