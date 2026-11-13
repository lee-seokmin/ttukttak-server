package com.ttukttak.domain.user.infrastructure;

import com.ttukttak.domain.user.domain.User;
import com.ttukttak.domain.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        return jpaRepository.saveAndFlush(UserJpaEntity.from(user)).toDomain();
    }

    @Override
    public Optional<User> findByHandle(String handle) {
        return jpaRepository.findByHandle(handle).map(UserJpaEntity::toDomain);
    }

    @Override
    public boolean existsByHandle(String handle) {
        return jpaRepository.existsByHandle(handle);
    }

    @Override
    public boolean existsByGithubId(Long githubId) {
        return jpaRepository.existsByGithubId(githubId);
    }
}
