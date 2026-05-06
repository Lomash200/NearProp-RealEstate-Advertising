package com.nearprop.mapper;

import com.nearprop.dto.CouponDto;
import com.nearprop.dto.CouponRequestDto;
import com.nearprop.entity.Coupon;
import com.nearprop.entity.User;
import com.nearprop.util.IdGenerator;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {
    
    /**
     * Map Coupon entity to CouponDto
     * 
     * @param coupon The coupon entity
     * @return The coupon DTO
     */
    public CouponDto toDto(Coupon coupon) {
        if (coupon == null) {
            return null;
        }
        
        CouponDto dto = new CouponDto();
        dto.setId(coupon.getId());
        dto.setPermanentId(coupon.getPermanentId());
        dto.setCode(coupon.getCode());
        dto.setDescription(coupon.getDescription());
        dto.setDiscountAmount(coupon.getDiscountAmount());
        dto.setDiscountPercentage(coupon.getDiscountPercentage());
        dto.setMaxDiscount(coupon.getMaxDiscount());
        dto.setValidFrom(coupon.getValidFrom());
        dto.setValidUntil(coupon.getValidUntil());
        dto.setMaxUses(coupon.getMaxUses());
        dto.setCurrentUses(coupon.getCurrentUses());
        dto.setActive(coupon.isActive());
        dto.setDiscountType(coupon.getDiscountType());
        dto.setSubscriptionType(coupon.getSubscriptionType());
        dto.setCreatedAt(coupon.getCreatedAt());
        dto.setUpdatedAt(coupon.getUpdatedAt());
        
        if (coupon.getCreatedBy() != null) {
            dto.setCreatedById(coupon.getCreatedBy().getId());
            dto.setCreatedByName(coupon.getCreatedBy().getName());
        }
        
        return dto;
    }
    
    /**
     * Create a new Coupon entity from CouponRequestDto
     * 
     * @param requestDto The coupon request DTO
     * @param admin The admin user creating the coupon
     * @return The new coupon entity
     */
    public Coupon toEntity(CouponRequestDto requestDto, User admin) {
        if (requestDto == null) {
            return null;
        }
        
        Coupon coupon = new Coupon();
        coupon.setPermanentId(IdGenerator.generateCouponId());
        coupon.setCode(requestDto.getCode().toUpperCase());
        coupon.setDescription(requestDto.getDescription());
        coupon.setDiscountType(requestDto.getDiscountType());
        
        if (requestDto.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            coupon.setDiscountPercentage(requestDto.getDiscountPercentage());
            coupon.setMaxDiscount(requestDto.getMaxDiscount());
        } else {
            coupon.setDiscountAmount(requestDto.getDiscountAmount());
        }
        
        coupon.setValidFrom(requestDto.getValidFrom());
        coupon.setValidUntil(requestDto.getValidUntil());
        coupon.setMaxUses(requestDto.getMaxUses());
        coupon.setCurrentUses(0);
        coupon.setActive(requestDto.isActive());
        coupon.setSubscriptionType(requestDto.getSubscriptionType());
        coupon.setCreatedBy(admin);
        
        return coupon;
    }
    
    /**
     * Update an existing Coupon entity from CouponRequestDto
     * 
     * @param coupon The existing coupon entity
     * @param requestDto The coupon request DTO
     * @return The updated coupon entity
     */
    public Coupon updateEntity(Coupon coupon, CouponRequestDto requestDto) {
        if (coupon == null || requestDto == null) {
            return coupon;
        }
        
        coupon.setCode(requestDto.getCode().toUpperCase());
        coupon.setDescription(requestDto.getDescription());
        coupon.setDiscountType(requestDto.getDiscountType());
        
        if (requestDto.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            coupon.setDiscountPercentage(requestDto.getDiscountPercentage());
            coupon.setMaxDiscount(requestDto.getMaxDiscount());
            coupon.setDiscountAmount(null);
        } else {
            coupon.setDiscountAmount(requestDto.getDiscountAmount());
            coupon.setDiscountPercentage(null);
            coupon.setMaxDiscount(null);
        }
        
        coupon.setValidFrom(requestDto.getValidFrom());
        coupon.setValidUntil(requestDto.getValidUntil());
        coupon.setMaxUses(requestDto.getMaxUses());
        coupon.setActive(requestDto.isActive());
        coupon.setSubscriptionType(requestDto.getSubscriptionType());
        
        return coupon;
    }
} 