package com.svrmslk.company.organization.infrastructure.persistence;

import com.svrmslk.company.organization.domain.Invitation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InvitationRepository {

    private final InvitationJpaRepository jpaRepository;
    private final InvitationEntityMapper mapper;

    public Invitation save(Invitation invitation) {
        InvitationEntity entity = mapper.toEntity(invitation);
        InvitationEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    public Optional<Invitation> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(mapper::toDomain);
    }
}