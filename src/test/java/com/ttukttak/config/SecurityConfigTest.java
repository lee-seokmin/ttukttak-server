package com.ttukttak.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void safeNext() {
        assertThat(SecurityConfig.safeNext("/submit?tab=1")).isEqualTo("/submit?tab=1");
        assertThat(SecurityConfig.safeNext(null)).isEqualTo("/");
        assertThat(SecurityConfig.safeNext("https://evil.com")).isEqualTo("/");
        assertThat(SecurityConfig.safeNext("//evil.com")).isEqualTo("/");
        assertThat(SecurityConfig.safeNext("/\\evil.com")).isEqualTo("/");
        assertThat(SecurityConfig.safeNext("/a\r\nSet-Cookie: x")).isEqualTo("/");
    }
}
