// ==========================================
// FILE: repository/SubscriptionRepository.java
// ==========================================
package com.svrmslk.tenant.repository;

import com.svrmslk.tenant.domain.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByTenantIdAndStatus(String tenantId, Subscription.SubscriptionStatus status);

    Optional<Subscription> findFirstByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<Subscription> findByTenantId(String tenantId);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' " +
            "AND s.currentPeriodEnd < :date")
    List<Subscription> findExpiringSubscriptions(@Param("date") LocalDateTime date);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'TRIAL' " +
            "AND s.trialEndDate < :date")
    List<Subscription> findExpiredTrials(@Param("date") LocalDateTime date);
}
