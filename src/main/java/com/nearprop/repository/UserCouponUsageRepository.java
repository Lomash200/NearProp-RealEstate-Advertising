package com.nearprop.repository;

import com.nearprop.entity.UserCouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponUsageRepository
        extends JpaRepository<UserCouponUsage, Long> {

    // ✅ RELATION-BASED QUERY (CORRECT)
    boolean existsByUser_IdAndCoupon_Id(Long userId, Long couponId);
}
