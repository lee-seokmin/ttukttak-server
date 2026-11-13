package com.ttukttak.auth.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @Test
    void crud() throws Exception {
        String body = """
                { "githubId": 1001, "handle": "jh.park", "name": "박지훈" }""";

        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.handle").value("jh.park"))
                .andExpect(jsonPath("$.user.initials").value("JH"));

        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("handle_taken"));

        mvc.perform(patch("/api/v1/users/jh.park").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "bio": "작게 만들어요.", "links": { "blog": "https://jh.park.dev" } }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.name").value("박지훈"))
                .andExpect(jsonPath("$.user.bio").value("작게 만들어요."))
                .andExpect(jsonPath("$.user.links.blog").value("https://jh.park.dev"));

        mvc.perform(get("/api/v1/users/jh.park"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.joinedAt").value(org.hamcrest.Matchers.endsWith("+09:00")));

        mvc.perform(delete("/api/v1/users/jh.park")).andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/users/jh.park"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("not_found"));

        // 탈퇴한 handle과 GitHub 계정으로 다시 가입할 수 있어요
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void errors() throws Exception {
        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "githubId": 1002, "handle": "Bad Handle", "name": "" }"""))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.error.code").value("validation_failed"))
                .andExpect(jsonPath("$.error.fields.handle").exists())
                .andExpect(jsonPath("$.error.fields.name").exists());

        mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content("{ \"unknown\": 1 }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("invalid_request"));

        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/users/{handle}'].get.summary").value("프로필 조회"));

        mvc.perform(get("/api/v1/weeks"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("unauthenticated"));
    }
}
