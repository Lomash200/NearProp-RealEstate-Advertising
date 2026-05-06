package com.nearprop.scheduler;

import com.nearprop.service.franchisee.DistrictRevenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Scheduler for handling payment status updates and checks
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatusScheduler {

    private final DistrictRevenueService districtRevenueService;
    
    /**
     * Check for pending transactions older than 24 hours
     * Runs every 2 hours
     */
    @Scheduled(fixedRate = 7200000) // 2 hours in milliseconds
    public void checkStuckPendingTransactions() {
        log.info("Running scheduled check for stuck pending transactions");
        String threadName = Thread.currentThread().getName();
        log.info("Running scheduled task on thread: {}", threadName);
        // Set cutoff time to 24 hours ago
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        
        try {
            int processedCount = districtRevenueService.handleStuckPendingTransactions(cutoffTime);
            log.info("Processed {} stuck pending transactions", processedCount);
        } catch (Exception e) {
            log.error("Error processing stuck pending transactions", e);
        }
    }
}
