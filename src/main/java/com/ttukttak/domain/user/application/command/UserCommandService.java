package com.ttukttak.domain.user.application.command;

import com.ttukttak.domain.user.domain.User;
import com.ttukttak.domain.user.domain.UserRepository;
import com.ttukttak.domain.user.exception.DuplicateUserException;
import com.ttukttak.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {
    private final UserRepository userRepository;

    public User create(Long githubId, String handle, String name, String avatarUrl) {
        if (userRepository.existsByHandle(handle)) throw DuplicateUserException.handle();
        if (userRepository.existsByGithubId(githubId)) throw DuplicateUserException.githubAccount();

        return userRepository.save(User.create(githubId, handle, name, avatarUrl));
    }

    public User updateProfile(String handle, String name, String bio, String githubLink, String blogLink) {
        User user = find(handle);
        user.updateProfile(name, bio, githubLink, blogLink);
        return userRepository.save(user);
    }

    public void delete(String handle) {
        User user = find(handle);
        user.delete();
        userRepository.save(user);
    }

    private User find(String handle) {
        return userRepository.findByHandle(handle.toLowerCase()).orElseThrow(UserNotFoundException::new);
    }
}
