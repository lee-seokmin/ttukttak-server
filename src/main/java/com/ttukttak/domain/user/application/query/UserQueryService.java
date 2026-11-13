package com.ttukttak.domain.user.application.query;

import com.ttukttak.domain.user.domain.User;
import com.ttukttak.domain.user.domain.UserRepository;
import com.ttukttak.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {
    private final UserRepository userRepository;

    public User get(String handle) {
        return userRepository.findByHandle(handle.toLowerCase()).orElseThrow(UserNotFoundException::new);
    }

    public User get(UUID id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }
}
