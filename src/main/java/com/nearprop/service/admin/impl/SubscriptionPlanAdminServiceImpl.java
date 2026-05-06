//package com.nearprop.service.admin.impl;
//
//import com.nearprop.dto.SubscriptionPlanDto;
//import com.nearprop.dto.admin.CreateSubscriptionPlanDto;
//import com.nearprop.entity.SubscriptionPlan;
//import com.nearprop.entity.SubscriptionPlan.PlanType;
//import com.nearprop.exception.ConflictException;
//import com.nearprop.exception.InvalidInputException;
//import com.nearprop.exception.ResourceNotFoundException;
//import com.nearprop.repository.SubscriptionPlanRepository;
//import com.nearprop.repository.SubscriptionRepository;
//import com.nearprop.service.admin.SubscriptionPlanAdminService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class SubscriptionPlanAdminServiceImpl implements SubscriptionPlanAdminService {
//
//    private final SubscriptionPlanRepository subscriptionPlanRepository;
//    private final SubscriptionRepository subscriptionRepository;
//
//    @Override
//    @Transactional
//    public SubscriptionPlanDto createSubscriptionPlan(CreateSubscriptionPlanDto createDto) {
//        // Check if plan name already exists
//        Optional<SubscriptionPlan> existingPlan = subscriptionPlanRepository.findByNameAndActiveTrue(createDto.getName());
//        if (existingPlan.isPresent()) {
//            throw new ConflictException("A subscription plan with this name already exists");
//        }
//
//        // // Set appropriate defaults for franchise plans
//        if (createDto.getType() == PlanType.FRANCHISEE) {
//            createDto.setMaxProperties(-1); // Unlimited
//            createDto.setMaxTotalReels(-1); // Unlimited
//            createDto.setMaxReelsPerProperty(-1); // Unlimited
//            createDto.setDurationDays(365); // 1 year default for franchisees
//        }
//
//        // Create new plan
//        SubscriptionPlan plan = SubscriptionPlan.builder()
//                .name(createDto.getName())
//                .description(createDto.getDescription())
//                .type(createDto.getType())
//                .price(createDto.getPrice())
//                .marketingFee(createDto.getMarketingFee())
//                .durationDays(createDto.getDurationDays())
//                .maxProperties(createDto.getMaxProperties())
//                .maxReelsPerProperty(createDto.getMaxReelsPerProperty())
//                .maxTotalReels(createDto.getMaxTotalReels())
//                .contentHideAfterDays(createDto.getContentHideAfterDays())
//                .contentDeleteAfterDays(createDto.getContentDeleteAfterDays())
//                .active(createDto.getActive() != null ? createDto.getActive() : true).build();
//
//        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
//        log.info("Created new subscription plan: {}", savedPlan.getName());
//
//        return mapToDto(savedPlan);
//    }
//
////    @Override
////    @Transactional(readOnly = true)
////    public Page<SubscriptionPlanDto> getAllSubscriptionPlans(Pageable pageable) {
////        return subscriptionPlanRepository.findAll(pageable)
////                .map(this::mapToDto);
////    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public Page<SubscriptionPlanDto> getAllSubscriptionPlans(Pageable pageable) {
//        return subscriptionPlanRepository.findByActiveTrue(pageable)
//                .map(this::mapToDto);
//    }
//
//
//    @Override
//    @Transactional(readOnly = true)
//    public SubscriptionPlanDto getSubscriptionPlanById(Long id) {
//        SubscriptionPlan plan = findPlanById(id);
//        return mapToDto(plan);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<SubscriptionPlanDto> getSubscriptionPlansByType(PlanType type) {
//        return subscriptionPlanRepository.findByTypeAndActiveTrue(type).stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public SubscriptionPlanDto updateSubscriptionPlan(Long id, CreateSubscriptionPlanDto updateDto) {
//        SubscriptionPlan plan = findPlanById(id);
//
//        // Check if we're trying to change the name to one that already exists
//        if (!plan.getName().equals(updateDto.getName())) {
//            Optional<SubscriptionPlan> existingPlan = subscriptionPlanRepository.findByNameAndActiveTrue(updateDto.getName());
//            if (existingPlan.isPresent()) {
//                throw new ConflictException("A subscription plan with this name already exists");
//            }
//        }
//
//        // Check if trying to change a plan type that has active subscriptions
//        if (plan.getType() != updateDto.getType()) {
//            long activeSubscriptions = subscriptionRepository.countActiveByPlanId(id);
//            if (activeSubscriptions > 0) {
//                throw new InvalidInputException("Cannot change plan type while there are active subscriptions");
//            }
//        }
//
//        // Update plan details, preserving existing values if not provided
//        plan.setName(updateDto.getName());
//        plan.setDescription(updateDto.getDescription());
//        plan.setType(updateDto.getType());
//        plan.setPrice(updateDto.getPrice());
//        plan.setMarketingFee(updateDto.getMarketingFee());
//        plan.setDurationDays(updateDto.getDurationDays());
//        plan.setMaxProperties(updateDto.getMaxProperties() != null ? updateDto.getMaxProperties() : plan.getMaxProperties());
//        plan.setMaxReelsPerProperty(updateDto.getMaxReelsPerProperty() != null ? updateDto.getMaxReelsPerProperty() : plan.getMaxReelsPerProperty());
//        plan.setMaxTotalReels(updateDto.getMaxTotalReels() != null ? updateDto.getMaxTotalReels() : plan.getMaxTotalReels());
//        plan.setContentHideAfterDays(updateDto.getContentHideAfterDays());
//        plan.setContentDeleteAfterDays(updateDto.getContentDeleteAfterDays());
//        plan.setActive(updateDto.getActive());
//
//        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
//        log.info("Updated subscription plan: {}", updatedPlan.getId());
//
//        return mapToDto(updatedPlan);
//    }
//
//    @Override
//    @Transactional
//    public SubscriptionPlanDto setSubscriptionPlanActiveStatus(Long id, boolean active) {
//        SubscriptionPlan plan = findPlanById(id);
//
//        // If deactivating, check if there are active subscriptions
//        if (!active && plan.isActive()) {
//            long activeSubscriptions = subscriptionRepository.countActiveByPlanId(id);
//            if (activeSubscriptions > 0) {
//                throw new InvalidInputException("Cannot deactivate plan while there are active subscriptions");
//            }
//        }
//
//        plan.setActive(active);
//        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
//
//        log.info("Set subscription plan {} active status to {}", id, active);
//        return mapToDto(updatedPlan);
//    }
//
//    @Override
//    @Transactional
//    public void deleteSubscriptionPlan(Long id) {
//        SubscriptionPlan plan = findPlanById(id);
//
//        // Check if there are active subscriptions
//        long activeSubscriptions = subscriptionRepository.countActiveByPlanId(id);
//        if (activeSubscriptions > 0) {
//            throw new InvalidInputException("Cannot delete plan while there are active subscriptions");
//        }
//
//        // Soft delete by setting active=false
//        plan.setActive(false);
//        subscriptionPlanRepository.save(plan);
//
//        log.info("Deleted subscription plan: {}", id);
//    }
//
//    // Helper methods
//
//    private SubscriptionPlan findPlanById(Long id) {
//        return subscriptionPlanRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + id));
//    }
//
//    private SubscriptionPlanDto mapToDto(SubscriptionPlan plan) {
//        return SubscriptionPlanDto.builder()
//                .id(plan.getId())
//                .name(plan.getName())
//                .description(plan.getDescription())
//                .type(plan.getType())
//                .price(plan.getPrice())
//                .marketingFee(plan.getMarketingFee())
//                .durationDays(plan.getDurationDays())
//                .maxProperties(plan.getMaxProperties())
//                .maxReelsPerProperty(plan.getMaxReelsPerProperty())
//                .maxTotalReels(plan.getMaxTotalReels())
//                .contentHideAfterDays(plan.getContentHideAfterDays())
//                .contentDeleteAfterDays(plan.getContentDeleteAfterDays())
//                .active(plan.isActive())
//                .createdAt(plan.getCreatedAt())
//                .updatedAt(plan.getUpdatedAt())
//                .hasUnlimitedProperties(plan.hasUnlimitedProperties())
//                .hasUnlimitedReels(plan.hasUnlimitedReels())
//                .build();
//    }
//}




package com.nearprop.service.admin.impl;

import com.nearprop.dto.SubscriptionPlanDto;
import com.nearprop.dto.admin.CreateSubscriptionPlanDto;
import com.nearprop.entity.SubscriptionPlan;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import com.nearprop.exception.ConflictException;
import com.nearprop.exception.InvalidInputException;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.SubscriptionPlanRepository;
import com.nearprop.repository.SubscriptionRepository;
import com.nearprop.service.NotificationService;
import com.nearprop.service.admin.SubscriptionPlanAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionPlanAdminServiceImpl implements SubscriptionPlanAdminService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;


    // ================= CREATE =================
    @Override
    @Transactional
    public SubscriptionPlanDto createSubscriptionPlan(CreateSubscriptionPlanDto createDto) {

//        Optional<SubscriptionPlan> existingPlan =
//                subscriptionPlanRepository.findByNameAndActiveTrue(createDto.getName());
//        if (existingPlan.isPresent()) {
//            throw new ConflictException("A subscription plan with this name already exists");
//        }


        Optional<SubscriptionPlan> existingPlan =
                subscriptionPlanRepository.findByNameAndType(
                        createDto.getName(),
                        createDto.getType()
                );

        if (existingPlan.isPresent()) {
            throw new ConflictException(
                    "Subscription plan already exists for type: " + createDto.getType()
            );
        }



        // Default values for FRANCHISEE
        if (createDto.getType() == PlanType.FRANCHISEE) {
            createDto.setMaxProperties(-1);
            createDto.setMaxTotalReels(-1);
            createDto.setMaxReelsPerProperty(-1);
            createDto.setDurationDays(365);
        }

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(createDto.getName())
                .description(createDto.getDescription())
                .type(createDto.getType())
                .type_s(createDto.getType_s())
                .price(createDto.getPrice())
                .marketingFee(createDto.getMarketingFee())
                .durationDays(createDto.getDurationDays())
                .maxProperties(createDto.getMaxProperties())
                .maxReelsPerProperty(createDto.getMaxReelsPerProperty())
                .maxTotalReels(createDto.getMaxTotalReels())
                .contentHideAfterDays(createDto.getContentHideAfterDays())
                .contentDeleteAfterDays(createDto.getContentDeleteAfterDays())
                .active(createDto.getActive() != null ? createDto.getActive() : true)
                .build();

        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        log.info("Created subscription plan: {}", savedPlan.getName());

        // 🔔 AUTO NOTIFICATION WHEN ADMIN CREATES PLAN
        notificationService.notifyDistrictPlanPublished(
                "ALL",                // agar district-wise nahi hai
                savedPlan.getId()
        );


        return mapToDto(savedPlan);
    }

    // ================= GET ALL (ONLY DEACTIVATED) =================
    @Override
    @Transactional(readOnly = true)
    public Page<SubscriptionPlanDto> getAllDeactivatedSubscriptionPlans(Pageable pageable) {
        return subscriptionPlanRepository.findByActiveFalse(pageable)
                .map(this::mapToDto);
    }


    // ================= GET ALL (ONLY ACTIVE) =================
    @Override
    @Transactional(readOnly = true)
    public Page<SubscriptionPlanDto> getAllSubscriptionPlans(Pageable pageable) {
        return subscriptionPlanRepository.findByActiveTrue(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubscriptionPlanDto> getAllPlansIncludingDeactivated(Pageable pageable) {
        return subscriptionPlanRepository.findAll(pageable)
                .map(this::mapToDto);
    }


    // ================= GET BY ID =================
    @Override
    @Transactional(readOnly = true)
    public SubscriptionPlanDto getSubscriptionPlanById(Long id) {
        return mapToDto(findPlanById(id));
    }

    // ================= GET BY TYPE =================
    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDto> getSubscriptionPlansByType(PlanType type) {
        return subscriptionPlanRepository.findByTypeAndActiveTrue(type)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ================= UPDATE =================
    @Override
    @Transactional
    public SubscriptionPlanDto updateSubscriptionPlan(Long id, CreateSubscriptionPlanDto updateDto) {

        SubscriptionPlan plan = findPlanById(id);

//        if (!plan.getName().equals(updateDto.getName())) {
//            subscriptionPlanRepository.findByNameAndActiveTrue(updateDto.getName())
//                    .ifPresent(p -> {
//                        throw new ConflictException("A subscription plan with this name already exists");
//                    });
//        }
        if (!plan.getName().equals(updateDto.getName())
                || plan.getType() != updateDto.getType()) {

            subscriptionPlanRepository
                    .findByNameAndType(updateDto.getName(), updateDto.getType())
                    .ifPresent(p -> {
                        if (!p.getId().equals(plan.getId())) {
                            throw new ConflictException(
                                    "Subscription plan already exists for type: " + updateDto.getType()
                            );
                        }
                    });
        }


        if (plan.getType() != updateDto.getType()) {
            long activeSubs = subscriptionRepository.countActiveByPlanId(id);
            if (activeSubs > 0) {
                throw new InvalidInputException(
                        "Cannot change plan type while there are active subscriptions");
            }
        }

        plan.setName(updateDto.getName());
        plan.setDescription(updateDto.getDescription());
        plan.setType(updateDto.getType());
        plan.setPrice(updateDto.getPrice());
        plan.setMarketingFee(updateDto.getMarketingFee());
        plan.setDurationDays(updateDto.getDurationDays());
        plan.setMaxProperties(updateDto.getMaxProperties());
        plan.setMaxReelsPerProperty(updateDto.getMaxReelsPerProperty());
        plan.setMaxTotalReels(updateDto.getMaxTotalReels());
        plan.setContentHideAfterDays(updateDto.getContentHideAfterDays());
        plan.setContentDeleteAfterDays(updateDto.getContentDeleteAfterDays());
        plan.setActive(updateDto.getActive());

        return mapToDto(subscriptionPlanRepository.save(plan));
    }

    // ================= ACTIVATE / DEACTIVATE =================
    @Override
    @Transactional
    public SubscriptionPlanDto setSubscriptionPlanActiveStatus(Long id, boolean active) {

        SubscriptionPlan plan = findPlanById(id);

        if (!active && plan.isActive()) {
            long activeSubs = subscriptionRepository.countActiveByPlanId(id);
            if (activeSubs > 0) {
                throw new InvalidInputException(
                        "Cannot deactivate plan while there are active subscriptions");
            }
        }

        plan.setActive(active);

        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);

// 🔔 sirf ACTIVE hone par notification
        if (active) {
            notificationService.notifyDistrictPlanPublished(
                    "ALL",
                    savedPlan.getId()
            );
        }
        return mapToDto(subscriptionPlanRepository.save(plan));
    }

    // ================= DELETE (FORCE SOFT DELETE) =================
    @Override
    @Transactional
    public void deleteSubscriptionPlan(Long id) {

        SubscriptionPlan plan = findPlanById(id);

        // 🔥 FORCE DELETE (NO ACTIVE SUBS CHECK)
        plan.setActive(false);

        subscriptionPlanRepository.save(plan);
        log.warn("FORCE deleted subscription plan (soft delete): {}", id);
    }

    // ================= HELPERS =================
    private SubscriptionPlan findPlanById(Long id) {
        return subscriptionPlanRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Subscription plan not found with ID: " + id));
    }

    private SubscriptionPlanDto mapToDto(SubscriptionPlan plan) {
        return SubscriptionPlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .type(plan.getType())
                .price(plan.getPrice())
                .marketingFee(plan.getMarketingFee())
                .durationDays(plan.getDurationDays())
                .maxProperties(plan.getMaxProperties())
                .maxReelsPerProperty(plan.getMaxReelsPerProperty())
                .maxTotalReels(plan.getMaxTotalReels())
                .contentHideAfterDays(plan.getContentHideAfterDays())
                .contentDeleteAfterDays(plan.getContentDeleteAfterDays())
                .active(plan.isActive())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .hasUnlimitedProperties(plan.hasUnlimitedProperties())
                .hasUnlimitedReels(plan.hasUnlimitedReels())
                .build();
    }
}