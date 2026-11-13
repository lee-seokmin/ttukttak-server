package com.ttukttak.domain.user.domain;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findByHandle(String handle);

    boolean existsByHandle(String handle);

    boolean existsByGithubId(Long githubId);
}
