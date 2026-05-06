package com.nearprop.service;

import com.nearprop.entity.Coupon;
import com.nearprop.entity.User;
import com.nearprop.entity.UserCouponUsage;
import com.nearprop.exception.BadRequestException;
import com.nearprop.repository.UserCouponUsageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponUsageService {

    private final UserCouponUsageRepository userCouponUsageRepository;
    private final UserService userService;

    public void checkNotUsed(Long couponId) {
        User user = userService.getCurrentUser();

        log.error("🔥 CHECK NOT USED CALLED | userId={} couponId={}", user.getId(), couponId);

        boolean exists = userCouponUsageRepository.existsByUser_IdAndCoupon_Id(
                user.getId(), couponId);


        log.error("🔥 EXISTS RESULT = {}", exists);

        if (exists) {
            throw new BadRequestException("You have already used this coupon");
        }
    }

    @Transactional
    public void markUsed(User user, Coupon coupon) {
        log.error("🔥 MARKING COUPON USED | userId={} couponId={}", user.getId(), coupon.getId());

        userCouponUsageRepository.save(
                UserCouponUsage.builder()
                        .user(user)
                        .coupon(coupon)
                        .build()
        );
    }
}
