package com.svrmslk.authservice.authentication.domain.model;

import com.svrmslk.authservice.authentication.domain.valueobject.Email;
import com.svrmslk.authservice.authentication.domain.valueobject.PasswordHash;
import com.svrmslk.authservice.authentication.domain.exception.AccountLockedException;
import com.svrmslk.authservice.authentication.domain.exception.InvalidCredentialsException;
import com.svrmslk.authservice.authentication.domain.policy.AccountLockoutPolicy;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserAccount {

    private UUID id;
    private Email email;
    private PasswordHash passwordHash;
    private Set<UserRole> roles;
    private int failedLoginAttempts;
    private Instant lockedUntil;
    private AccountStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private String sessionId;

    private UserAccount() {
        this.roles = new HashSet<>();
        this.failedLoginAttempts = 0;
        this.status = AccountStatus.ACTIVE;
    }

    public static UserAccount create(Email email, PasswordHash passwordHash) {
        UserAccount account = new UserAccount();
        account.id = UUID.randomUUID();
        account.email = email;
        account.passwordHash = passwordHash;
        account.createdAt = Instant.now();
        account.updatedAt = Instant.now();
        return account;
    }

    public static UserAccount reconstitute(
            UUID id,
            Email email,
            PasswordHash passwordHash,
            Set<UserRole> roles,
            int failedLoginAttempts,
            Instant lockedUntil,
            AccountStatus status,
            Instant createdAt,
            Instant updatedAt,
            String sessionId
    ) {
        UserAccount account = new UserAccount();
        account.id = id;
        account.email = email;
        account.passwordHash = passwordHash;
        account.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        account.failedLoginAttempts = failedLoginAttempts;
        account.lockedUntil = lockedUntil;
        account.status = status;
        account.createdAt = createdAt;
        account.updatedAt = updatedAt;
        account.sessionId = sessionId;
        return account;
    }

    public void authenticate(String rawPassword, PasswordMatcher matcher, AccountLockoutPolicy lockoutPolicy) {
        if (isLocked()) {
            throw new AccountLockedException("Account is locked until " + lockedUntil);
        }

        if (status != AccountStatus.ACTIVE) {
            throw new InvalidCredentialsException("Account is not active");
        }

        if (!matcher.matches(rawPassword, this.passwordHash)) {
            recordFailedLogin(lockoutPolicy);
            throw new InvalidCredentialsException("Invalid credentials");
        }

        resetFailedAttempts();
        this.sessionId = UUID.randomUUID().toString();
        this.updatedAt = Instant.now();
    }

    public void changePassword(PasswordHash newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = Instant.now();
    }

    public void assignRole(UserRole role) {
        this.roles.add(role);
        this.updatedAt = Instant.now();
    }

    public void removeRole(UserRole role) {
        this.roles.remove(role);
        this.updatedAt = Instant.now();
    }

    public void lockAccount(Instant until) {
        this.lockedUntil = until;
        this.status = AccountStatus.LOCKED;
        this.updatedAt = Instant.now();
    }

    public void unlockAccount() {
        this.lockedUntil = null;
        this.status = AccountStatus.ACTIVE;
        this.failedLoginAttempts = 0;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = AccountStatus.DISABLED;
        this.updatedAt = Instant.now();
    }

    public void invalidateSession() {
        this.sessionId = null;
        this.updatedAt = Instant.now();
    }

    private boolean isLocked() {
        if (status == AccountStatus.LOCKED && lockedUntil != null) {
            if (Instant.now().isBefore(lockedUntil)) {
                return true;
            } else {
                unlockAccount();
                return false;
            }
        }
        return false;
    }

    private void recordFailedLogin(AccountLockoutPolicy lockoutPolicy) {
        this.failedLoginAttempts++;
        this.updatedAt = Instant.now();

        if (lockoutPolicy.shouldLockAccount(this.failedLoginAttempts)) {
            lockAccount(lockoutPolicy.calculateLockoutDuration(this.failedLoginAttempts));
        }
    }

    private void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public PasswordHash getPasswordHash() {
        return passwordHash;
    }

    public Set<UserRole> getRoles() {
        return new HashSet<>(roles);
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public interface PasswordMatcher {
        boolean matches(String rawPassword, PasswordHash hash);
    }
}