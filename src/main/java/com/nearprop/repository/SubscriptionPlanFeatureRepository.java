package com.nearprop.repository;

import com.nearprop.entity.SubscriptionPlanFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanFeatureRepository extends JpaRepository<SubscriptionPlanFeature, Long> {
    
    Optional<SubscriptionPlanFeature> findByPlanName(String planName);
    
    List<SubscriptionPlanFeature> findByPlanType(SubscriptionPlanFeature.PlanType planType);
    
    List<SubscriptionPlanFeature> findByIsActiveTrue();
    
    List<SubscriptionPlanFeature> findByPlanTypeAndIsActiveTrue(SubscriptionPlanFeature.PlanType planType);
    Optional<SubscriptionPlanFeature> findByPlanNameAndPlanType(
            String planName,
            SubscriptionPlanFeature.PlanType planType
    );

} 