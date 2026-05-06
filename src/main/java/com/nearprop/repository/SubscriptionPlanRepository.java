//package com.nearprop.repository;
//
//import com.nearprop.entity.SubscriptionPlan;
//import com.nearprop.entity.SubscriptionPlan.PlanType;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
//
//    List<SubscriptionPlan> findByTypeAndActiveTrue(PlanType type);
//
//    Optional<SubscriptionPlan> findByNameAndActiveTrue(String name);
//
//    List<SubscriptionPlan> findByActiveTrue();
//}

package com.nearprop.repository;

import com.nearprop.entity.SubscriptionPlan;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    List<SubscriptionPlan> findByTypeAndActiveTrue(PlanType type);

    Optional<SubscriptionPlan> findByNameAndActiveTrue(String name);
    Optional<SubscriptionPlan> findByNameAndType(String name, PlanType type);
    Optional<SubscriptionPlan> findByNameAndTypeAndActiveTrue(String name, PlanType type);


    // (optional) non-paginated use
    List<SubscriptionPlan> findByActiveTrue();

    // ✅ THIS IS THE IMPORTANT ONE
    Page<SubscriptionPlan> findByActiveTrue(Pageable pageable);
    Page<SubscriptionPlan> findByActiveFalse(Pageable pageable);

}
