package com.ttukttak.domain.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByHandle(String handle);

    boolean existsByHandle(String handle);

    boolean existsByGithubId(Long githubId);
}
