package com.ttukttak.auth.infrastructure;

import com.ttukttak.domain.user.application.command.UserCommandService;
import com.ttukttak.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

/** GitHub 사용자 정보로 우리 사용자를 찾거나 만들고, 세션에는 우리 사용자 id만 담아요. */
@Component
@RequiredArgsConstructor
public class GithubOAuth2UserService extends DefaultOAuth2UserService {
    private final UserCommandService userCommandService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User github = super.loadUser(request);
        User user = userCommandService.loginWithGithub(
                ((Number) github.getAttributes().get("id")).longValue(),
                github.getAttribute("login"),
                github.getAttribute("name"),
                github.getAttribute("avatar_url"));
        return principal(user);
    }

    /** Authentication.getName()이 우리 사용자 id가 돼요. */
    public static OAuth2User principal(User user) {
        return new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_" + user.getRole().name()),
                Map.of("id", user.getId().toString()), "id");
    }
}
