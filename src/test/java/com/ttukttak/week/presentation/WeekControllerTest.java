package com.ttukttak.week.presentation;

import com.ttukttak.auth.infrastructure.GithubOAuth2UserService;
import com.ttukttak.domain.user.application.command.UserCommandService;
import com.ttukttak.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WeekControllerTest {
    static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.ofHours(9));

    @Autowired
    MockMvc mvc;

    @Autowired
    UserCommandService userCommandService;

    @Autowired
    JdbcTemplate jdbc;

    // 로컬에서 넣어둔 주차가 "이번 주"를 바꾸지 않게 비우고 시작해요 (테스트가 끝나면 롤백).
    @BeforeEach
    void clear() {
        jdbc.update("delete from participations");
        jdbc.update("delete from weeks");
    }

    @Test
    void weeks() throws Exception {
        String body = """
                { "labels": { "track": { "beginner": "입문" } }, "weeks": [ %s, %s, %s, %s, %s ] }""".formatted(
                theme("2025-w50", 1, -40), theme("2025-w51", 2, -20),
                """
                        { "id": "2025-w52", "type": "break", "round": null, "code": "W52", "title": "휴식",
                          "schedule": { "opensAt": "%s" } }""".formatted(days(-8)),
                theme("2026-w01", 3, 0), theme("2026-w02", 4, 7));

        User member = userCommandService.loginWithGithub(5001L, "member", null, null);
        mvc.perform(write(as(member, post("/api/v1/admin/weeks/import"))).content(body))
                .andExpect(status().isForbidden());
        mvc.perform(write(admin(post("/api/v1/admin/weeks/import"))).content(body))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/weeks/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.week.id").value("2026-w01"))
                .andExpect(jsonPath("$.week.type").value("theme"))
                .andExpect(jsonPath("$.week.phase").value("building"))
                .andExpect(jsonPath("$.week.examples.beginner").value("한 줄 일기"))
                .andExpect(jsonPath("$.week.schedule.opensAt").value(endsWith("+09:00")))
                .andExpect(jsonPath("$.nextWeek.id").value("2026-w02"))
                .andExpect(jsonPath("$.stats.presses").value(0));

        // 버튼
        mvc.perform(post("/api/v1/weeks/current/presses").contentType(MediaType.APPLICATION_JSON).content("{ \"count\": 3 }"))
                .andExpect(status().isForbidden());
        mvc.perform(write(post("/api/v1/weeks/current/presses")).content("{ \"count\": 3 }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.presses").value(3));
        mvc.perform(write(post("/api/v1/weeks/current/presses")).content("{ \"count\": 21 }"))
                .andExpect(status().isUnprocessableContent());

        // 참가하기는 여러 번 불러도 한 번만
        mvc.perform(write(as(member, post("/api/v1/weeks/current/participation"))))
                .andExpect(jsonPath("$.participants").value(1));
        mvc.perform(write(as(member, post("/api/v1/weeks/current/participation"))))
                .andExpect(jsonPath("$.joined").value(true))
                .andExpect(jsonPath("$.participants").value(1));
        mvc.perform(as(member, get("/api/v1/me")))
                .andExpect(jsonPath("$.currentWeek.weekId").value("2026-w01"))
                .andExpect(jsonPath("$.currentWeek.joined").value(true));
        mvc.perform(write(as(member, delete("/api/v1/weeks/current/participation"))))
                .andExpect(jsonPath("$.joined").value(false))
                .andExpect(jsonPath("$.participants").value(0));

        // 아카이브: 결과가 나온 주제 주만, 최신순, 커서
        mvc.perform(get("/api/v1/weeks").param("limit", "1"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value("2025-w51"))
                .andExpect(jsonPath("$.items[0].phase").value("closed"))
                .andExpect(jsonPath("$.nextCursor").value("2025-w51"));
        mvc.perform(get("/api/v1/weeks").param("limit", "1").param("cursor", "2025-w51"))
                .andExpect(jsonPath("$.items[0].id").value("2025-w50"))
                .andExpect(jsonPath("$.nextCursor").value(nullValue()));

        // 상세: 이웃은 휴식 주를 건너뛰고, 공개 전 주제는 숨겨요
        mvc.perform(get("/api/v1/weeks/2026-w01"))
                .andExpect(jsonPath("$.week.announcement").value("2026-w01 공지"))
                .andExpect(jsonPath("$.prev.id").value("2025-w51"))
                .andExpect(jsonPath("$.next.id").value("2026-w02"))
                .andExpect(jsonPath("$.next.title").value(nullValue()));
        mvc.perform(get("/api/v1/weeks/2026-w02"))
                .andExpect(jsonPath("$.week.phase").value("upcoming"))
                .andExpect(jsonPath("$.week.title").value(nullValue()))
                .andExpect(jsonPath("$.week.announcement").value(nullValue()));
        mvc.perform(get("/api/v1/weeks/2099-w01"))
                .andExpect(status().isNotFound());

        // 다시 가져와도 버튼 횟수는 그대로
        mvc.perform(write(admin(post("/api/v1/admin/weeks/import"))).content(body))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/weeks/current")).andExpect(jsonPath("$.stats.presses").value(3));

        // 주제 주인데 마감 일정이 없으면 거절
        mvc.perform(write(admin(post("/api/v1/admin/weeks/import"))).content("""
                        { "weeks": [ { "id": "2026-w09", "type": "theme", "round": 9, "code": "W9", "title": "x",
                                       "schedule": { "opensAt": "%s" } } ] }""".formatted(days(60))))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.code").value("validation_failed"));
    }

    @Test
    void noWeek() throws Exception {
        mvc.perform(get("/api/v1/weeks/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.week").value(nullValue()))
                .andExpect(jsonPath("$.serverTime").value(endsWith("+09:00")));
        mvc.perform(write(post("/api/v1/weeks/current/presses")).content("{ \"count\": 1 }"))
                .andExpect(status().isNotFound());
    }

    /** startDay일 뒤(음수면 전)에서 1시간 전에 열리는 주제 주. 3일 제작, 3일 투표, 1일 집계. */
    private static String theme(String id, int round, int startDay) {
        OffsetDateTime opens = NOW.plusDays(startDay).minusHours(1);
        return """
                { "id": "%s", "type": "theme", "round": %d, "code": "W", "title": "%s 주제", "announcement": "%s 공지",
                  "difficulty": "low", "status": "building", "examples": { "beginner": "한 줄 일기" },
                  "participantCount": 9, "owner": null,
                  "schedule": { "opensAt": "%s", "submissionClosesAt": "%s", "votingClosesAt": "%s", "resultsAt": "%s" } }"""
                .formatted(id, round, id, id, opens, opens.plusDays(3), opens.plusDays(6), opens.plusDays(7));
    }

    private static OffsetDateTime days(int days) {
        return NOW.plus(Duration.ofDays(days));
    }

    private static MockHttpServletRequestBuilder as(User user, MockHttpServletRequestBuilder request) {
        return request.with(oauth2Login().oauth2User(GithubOAuth2UserService.principal(user)));
    }

    private static MockHttpServletRequestBuilder admin(MockHttpServletRequestBuilder request) {
        return request.with(oauth2Login().oauth2User(new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_ADMIN"), Map.of("id", UUID.randomUUID().toString()), "id")));
    }

    private static MockHttpServletRequestBuilder write(MockHttpServletRequestBuilder request) {
        return request.header("X-Requested-With", "ddukddak").contentType(MediaType.APPLICATION_JSON);
    }
}
