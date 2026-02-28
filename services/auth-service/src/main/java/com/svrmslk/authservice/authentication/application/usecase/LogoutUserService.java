package com.svrmslk.authservice.authentication.application.usecase;

import com.svrmslk.authservice.authentication.application.command.LogoutUserCommand;
import com.svrmslk.authservice.authentication.application.port.in.LogoutUserUseCase;
import com.svrmslk.authservice.authentication.application.port.out.EventPublisherPort;
import com.svrmslk.authservice.authentication.application.port.out.RefreshTokenRepositoryPort;
import com.svrmslk.authservice.authentication.domain.model.UserAccount;
import com.svrmslk.authservice.authentication.domain.repository.UserAccountRepository;

public class LogoutUserService implements LogoutUserUseCase {

    private final UserAccountRepository userAccountRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final EventPublisherPort eventPublisher;

    public LogoutUserService(
            UserAccountRepository userAccountRepository,
            RefreshTokenRepositoryPort refreshTokenRepository,
            EventPublisherPort eventPublisher
    ) {
        this.userAccountRepository = userAccountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void logout(LogoutUserCommand command) {
        userAccountRepository.findById(command.userId())
                .ifPresent(userAccount -> {
                    userAccount.invalidateSession();
                    userAccountRepository.save(userAccount);

                    refreshTokenRepository.deleteByUserId(userAccount.getId());

                    eventPublisher.publish(new UserLoggedOutEvent(userAccount.getId().toString()));
                });
    }

    private record UserLoggedOutEvent(String userId) implements EventPublisherPort.DomainEvent {
        @Override
        public String getEventType() {
            return "UserLoggedOut";
        }

        @Override
        public String getAggregateId() {
            return userId;
        }
    }
}