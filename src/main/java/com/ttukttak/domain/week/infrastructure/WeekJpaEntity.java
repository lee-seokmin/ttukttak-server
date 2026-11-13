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

    private long presses;

    static WeekJpaEntity from(Week week) {
        WeekJpaEntity entity = new WeekJpaEntity();
        entity.id = week.getId();
        entity.type = week.getType();
        entity.round = week.getRound();
        entity.code = week.getCode();
        entity.title = week.getTitle();
        entity.announcement = week.getAnnouncement();
        entity.intent = week.getIntent();
        entity.rule = week.getRule();
        entity.constraints = week.getConstraints();
        entity.difficulty = week.getDifficulty();
        entity.status = week.getStatus();
        Examples examples = week.getExamples();
        if (examples != null) {
            entity.exampleBeginner = examples.beginner();
            entity.exampleRegular = examples.regular();
            entity.exampleExpert = examples.expert();
        }
        entity.opensAt = week.getOpensAt();
        entity.submissionClosesAt = week.getSubmissionClosesAt();
        entity.votingClosesAt = week.getVotingClosesAt();
        entity.resultsAt = week.getResultsAt();
        entity.notes = week.getNotes();
        entity.presses = week.getPresses();
        return entity;
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
