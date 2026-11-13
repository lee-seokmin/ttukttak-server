package com.ttukttak.domain.user.application.command;

import com.ttukttak.domain.user.domain.User;
import com.ttukttak.domain.user.domain.UserRepository;
import com.ttukttak.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {
    private static final int HANDLE_MAX = 39;
    private static final int NAME_MAX = 20;

    private final UserRepository userRepository;

    /** GitHub 로그인. 처음이면 GitHub 아이디로 handle과 GitHub 링크를 만들어요(handle이 겹치면 숫자를 붙여요). */
    public User loginWithGithub(Long githubId, String login, String name, String avatarUrl) {
        return userRepository.findByGithubId(githubId).orElseGet(() -> userRepository.save(
                User.create(githubId, uniqueHandle(login), nameOf(name, login), avatarUrl,
                        "https://github.com/" + login)));
    }

    public User updateProfile(UUID id, String name, String bio, String githubLink, String blogLink) {
        User user = find(id);
        user.updateProfile(name, bio, githubLink, blogLink);
        return userRepository.save(user);
    }

    public void delete(UUID id) {
        User user = find(id);
        user.delete();
        userRepository.save(user);
    }

    private User find(UUID id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    // GitHub 아이디는 영문·숫자·하이픈 39자까지라 소문자로만 바꾸면 handle 규칙에 맞아요.
    private String uniqueHandle(String login) {
        String base = login.toLowerCase();
        String handle = base;
        for (int n = 2; userRepository.existsByHandle(handle); n++) {
            String suffix = String.valueOf(n);
            handle = base.substring(0, Math.min(base.length(), HANDLE_MAX - suffix.length())) + suffix;
        }
        return handle;
    }

    // GitHub 이름이 비어 있으면 아이디를 쓰고, 20자(코드 포인트)로 자르기
    private static String nameOf(String name, String login) {
        String value = name == null || name.isBlank() ? login : name.strip();
        int length = Math.min(NAME_MAX, value.codePointCount(0, value.length()));
        return value.substring(0, value.offsetByCodePoints(0, length));
    }
}
