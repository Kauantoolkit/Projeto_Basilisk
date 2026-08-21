package com.basilisk.gym.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    List<Subscription> findByClientIdOrderByCreatedAtDesc(UUID clientId);
    List<Subscription> findByStatusOrderByEndDateAsc(SubscriptionStatus status);
    List<Subscription> findByClientIdAndStatus(UUID clientId, SubscriptionStatus status);
    long countByStatus(SubscriptionStatus status);
}