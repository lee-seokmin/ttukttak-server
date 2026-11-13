package com.ttukttak.config;

import com.ttukttak.auth.infrastructure.GithubOAuth2UserService;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.io.IOException;
import java.util.Set;

@Configuration
public class SecurityConfig {
    private static final String AUTH = "/api/v1/auth";
    private static final String NEXT = "login.next";
    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clients,
                                            GithubOAuth2UserService githubUserService) throws Exception {
        return http
                // 세션 쿠키가 SameSite=Lax라 쓰기 요청은 X-Requested-With 헤더로만 막아요 (명세 0. 인증).
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(requireRequestedWith(), LogoutFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "/swagger-ui/**", "/v3/api-docs/**", AUTH + "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").permitAll()
                        .anyRequest().authenticated())
                // GET /auth/github → GitHub → GET /auth/github/callback → next로 이동
                .oauth2Login(login -> login
                        .loginPage(AUTH + "/github")
                        .authorizationEndpoint(a -> a.authorizationRequestResolver(
                                rememberNext(new DefaultOAuth2AuthorizationRequestResolver(clients, AUTH))))
                        .redirectionEndpoint(r -> r.baseUri(AUTH + "/*/callback"))
                        .userInfoEndpoint(u -> u.userService(githubUserService))
                        .successHandler((req, res, auth) -> res.sendRedirect(popNext(req)))
                        .failureHandler((req, res, ex) -> res.sendRedirect("/?login=failed")))
                .logout(logout -> logout
                        .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, AUTH + "/logout"))
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> writeError(res, 401, "unauthenticated", "로그인이 필요해요."))
                        .accessDeniedHandler((req, res, ex) -> writeError(res, 403, "forbidden", "권한이 없어요.")))
                .build();
    }

    private static Filter requireRequestedWith() {
        return (req, res, chain) -> {
            var request = (HttpServletRequest) req;
            if (!SAFE_METHODS.contains(request.getMethod()) && !"ddukddak".equals(request.getHeader("X-Requested-With"))) {
                writeError((HttpServletResponse) res, 403, "forbidden", "권한이 없어요.");
                return;
            }
            chain.doFilter(req, res);
        };
    }

    // GitHub로 보내기 전에 ?next를 세션에 적어둬요. 로그인하면서 세션 id가 바뀌어도 값은 따라가요.
    private static OAuth2AuthorizationRequestResolver rememberNext(OAuth2AuthorizationRequestResolver delegate) {
        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest req) {
                return remember(req, delegate.resolve(req));
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest req, String registrationId) {
                return remember(req, delegate.resolve(req, registrationId));
            }

            private OAuth2AuthorizationRequest remember(HttpServletRequest req, OAuth2AuthorizationRequest authRequest) {
                if (authRequest != null) req.getSession().setAttribute(NEXT, safeNext(req.getParameter("next")));
                return authRequest;
            }
        };
    }

    private static String popNext(HttpServletRequest req) {
        var session = req.getSession();
        Object next = session.getAttribute(NEXT);
        session.removeAttribute(NEXT);
        return next == null ? "/" : (String) next;
    }

    /** 같은 사이트 경로만 허용해요. //evil.com, /\evil.com 같은 다른 사이트 주소는 "/"로 바꿔요. */
    static String safeNext(String next) {
        boolean sameSite = next != null && next.startsWith("/") && !next.startsWith("//")
                && !next.contains("\\") && next.chars().noneMatch(Character::isISOControl);
        return sameSite ? next : "/";
    }

    private static void writeError(HttpServletResponse res, int status, String code, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}".formatted(code, message));
    }
}
