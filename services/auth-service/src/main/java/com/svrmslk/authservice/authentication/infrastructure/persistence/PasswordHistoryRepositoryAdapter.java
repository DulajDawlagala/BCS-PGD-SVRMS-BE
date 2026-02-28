package com.svrmslk.authservice.authentication.infrastructure.persistence;

import com.svrmslk.authservice.authentication.application.port.out.PasswordHistoryRepositoryPort;
import com.svrmslk.authservice.authentication.infrastructure.persistence.entity.PasswordHistoryEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PasswordHistoryRepositoryAdapter implements PasswordHistoryRepositoryPort {

    private final JpaPasswordHistoryRepository jpaRepository;

    public PasswordHistoryRepositoryAdapter(JpaPasswordHistoryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public void save(PasswordHistory history) {
        PasswordHistoryEntity entity = new PasswordHistoryEntity();
        entity.setId(history.id());
        entity.setUserId(history.userId());
        entity.setPasswordHash(history.passwordHash());
        entity.setCreatedAt(history.createdAt());
        jpaRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PasswordHistory> findRecentByUserId(UUID userId, int limit) {
        return jpaRepository.findRecentByUserIdOrderByCreatedAtDesc(userId).stream()
                .limit(limit)
                .map(e -> new PasswordHistory(e.getId(), e.getUserId(), e.getPasswordHash(), e.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
    }
}