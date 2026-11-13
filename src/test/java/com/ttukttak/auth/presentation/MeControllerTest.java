package com.ttukttak.auth.presentation;

import com.ttukttak.auth.infrastructure.GithubOAuth2UserService;
import com.ttukttak.domain.user.application.command.UserCommandService;
import com.ttukttak.domain.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MeControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    UserCommandService userCommandService;

    @Test
    void githubLogin() {
        User first = userCommandService.loginWithGithub(1001L, "JH-Park", null, null);
        assertThat(first.getHandle()).isEqualTo("jh-park");
        assertThat(first.getName()).isEqualTo("JH-Park");

        // 같은 GitHub 계정이면 같은 사용자, 아이디가 겹치면 숫자를 붙여요
        assertThat(userCommandService.loginWithGithub(1001L, "JH-Park", null, null).getId()).isEqualTo(first.getId());
        assertThat(userCommandService.loginWithGithub(1002L, "jh-park", "박지훈", null).getHandle()).isEqualTo("jh-park2");

        // 이름은 20자까지
        assertThat(userCommandService.loginWithGithub(1003L, "long", "가".repeat(30), null).getName()).hasSize(20);
    }

    @Test
    void me() throws Exception {
        User user = userCommandService.loginWithGithub(2001L, "jh.park", "박지훈", null);

        mvc.perform(as(user, get("/api/v1/me")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.handle").value("jh.park"))
                .andExpect(jsonPath("$.user.initials").value("JH"))
                .andExpect(jsonPath("$.role").value("member"));

        mvc.perform(as(user, write(patch("/api/v1/me")))
                        .content("""
                                { "bio": "작게 만들어요.", "links": { "blog": "https://jh.park.dev" } }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value("박지훈"));

        mvc.perform(get("/api/v1/users/jh.park"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.bio").value("작게 만들어요."))
                .andExpect(jsonPath("$.user.links.blog").value("https://jh.park.dev"))
                .andExpect(jsonPath("$.user.joinedAt").value(endsWith("+09:00")));

        mvc.perform(as(user, write(patch("/api/v1/me"))).content("{ \"name\": \"   \" }"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.fields.name").value("이름을 입력해 주세요."));

        String longUrl = "https://a.dev/" + "a".repeat(2048);
        mvc.perform(as(user, write(patch("/api/v1/me"))).content("{ \"links\": { \"blog\": \"" + longUrl + "\" } }"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.fields['links.blog']").exists());

        mvc.perform(as(user, write(patch("/api/v1/me"))).content("{ \"unknown\": 1 }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("invalid_request"));

        mvc.perform(as(user, write(delete("/api/v1/me")))).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/users/jh.park")).andExpect(status().isNotFound());

        // 탈퇴한 GitHub 계정으로 다시 로그인하면 새 사용자가 같은 handle로 생겨요
        User again = userCommandService.loginWithGithub(2001L, "jh.park", "박지훈", null);
        assertThat(again.getId()).isNotEqualTo(user.getId());
        assertThat(again.getHandle()).isEqualTo("jh.park");
    }

    @Test
    void auth() throws Exception {
        mvc.perform(get("/api/v1/auth/github").param("next", "/submit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", startsWith("https://github.com/login/oauth/authorize")))
                .andExpect(request().sessionAttribute("login.next", "/submit"));

        mvc.perform(get("/api/v1/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("unauthenticated"));

        User user = userCommandService.loginWithGithub(3001L, "csrf", null, null);
        mvc.perform(as(user, patch("/api/v1/me")).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("forbidden"));

        mvc.perform(as(user, write(post("/api/v1/auth/logout")))).andExpect(status().isNoContent());

        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/users/{handle}'].get.summary").value("프로필 조회"))
                .andExpect(jsonPath("$.paths['/api/v1/me'].get.summary").value("내 정보"));
    }

    private static MockHttpServletRequestBuilder as(User user, MockHttpServletRequestBuilder request) {
        return request.with(oauth2Login().oauth2User(GithubOAuth2UserService.principal(user)));
    }

    private static MockHttpServletRequestBuilder write(MockHttpServletRequestBuilder request) {
        return request.header("X-Requested-With", "ddukddak").contentType(MediaType.APPLICATION_JSON);
    }
}
