package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.domain.model.AccountStatus;
import com.svrmslk.authservice.authentication.domain.model.UserAccount;
import com.svrmslk.authservice.authentication.domain.model.UserRole;
import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;
import com.svrmslk.authservice.authentication.domain.valueobject.Email;
import com.svrmslk.authservice.authentication.domain.valueobject.PasswordHash;
import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.UserAccountEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserAccountRepositoryAdapter implements UserAccountRepository {

    private final JpaUserAccountRepository jpaRepository;

    public UserAccountRepositoryAdapter(JpaUserAccountRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public UserAccount save(UserAccount userAccount) {
        UserAccountEntity entity = toEntity(userAccount);
        UserAccountEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccount> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccount> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue()).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccount> findBySessionId(String sessionId) {
        return jpaRepository.findBySessionId(sessionId).map(this::toDomain);
    }

    @Override
    @Transactional
    public void delete(UserAccount userAccount) {
        jpaRepository.deleteById(userAccount.getId());
    }

    private UserAccount toDomain(UserAccountEntity entity) {
        Set<UserRole> roles = entity.getRoles().stream()
                .map(r -> UserRole.valueOf(r.name()))
                .collect(Collectors.toSet());

        return UserAccount.reconstitute(
                entity.getId(),
                Email.of(entity.getEmail()),
                PasswordHash.of(entity.getPasswordHash()),
                roles,
                entity.getFailedLoginAttempts(),
                entity.getLockedUntil(),
                AccountStatus.valueOf(entity.getStatus().name()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getSessionId()
        );
    }

    private UserAccountEntity toEntity(UserAccount domain) {
        UserAccountEntity entity = new UserAccountEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail().getValue());
        entity.setPasswordHash(domain.getPasswordHash().getValue());

        Set<UserAccountEntity.UserRoleEntity> roles = domain.getRoles().stream()
                .map(r -> UserAccountEntity.UserRoleEntity.valueOf(r.name()))
                .collect(Collectors.toSet());
        entity.setRoles(roles);

        entity.setFailedLoginAttempts(domain.getFailedLoginAttempts());
        entity.setLockedUntil(domain.getLockedUntil());
        entity.setStatus(UserAccountEntity.AccountStatusEntity.valueOf(domain.getStatus().name()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setSessionId(domain.getSessionId());

        return entity;
    }
}