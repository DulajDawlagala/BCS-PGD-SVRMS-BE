package com.svrmslk.authservice.authentication.domain.repository;

import com.svrmslk.authservice.authentication.domain.model.UserAccount;
import com.svrmslk.authservice.authentication.domain.valueobject.Email;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository {

    UserAccount save(UserAccount userAccount);

    Optional<UserAccount> findById(UUID id);

    Optional<UserAccount> findByEmail(Email email);

    boolean existsByEmail(Email email);

    Optional<UserAccount> findBySessionId(String sessionId);

    void delete(UserAccount userAccount);
}