package com.ttukttak.domain.user.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByHandle(String handle);

    Optional<User> findByGithubId(Long githubId);

    boolean existsByHandle(String handle);
}
