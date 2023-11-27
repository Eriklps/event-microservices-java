package com.eriklps.eventmicroservices.repositories;

import com.eriklps.eventmicroservices.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

}