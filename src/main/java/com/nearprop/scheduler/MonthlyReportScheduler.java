package com.nearprop.scheduler;

import com.nearprop.service.franchisee.MonthlyRevenueReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Scheduler to generate monthly revenue reports at the end of each month
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MonthlyReportScheduler {

    private final MonthlyRevenueReportService monthlyRevenueReportService;
    
    /**
     * Generate monthly revenue reports at 11:45 PM on the last day of each month
     * This gives a buffer of 15 minutes before the month ends
     */
    @Scheduled(cron = "0 45 23 L * ?")
    public void generateMonthlyReports() {
        log.info("Starting scheduled monthly revenue report generation at {}", LocalDateTime.now());
        
        try {
            int reportsGenerated = monthlyRevenueReportService.generateMonthlyReports();
            log.info("Completed scheduled monthly revenue report generation. Generated {} reports", reportsGenerated);
        } catch (Exception e) {
            log.error("Error in scheduled monthly revenue report generation", e);
        }
    }
    
    /**
     * Backup scheduler that runs on the 1st of each month at 1:00 AM
     * This is a fallback in case the end-of-month scheduler fails
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void backupMonthlyReportGeneration() {
        log.info("Starting backup monthly revenue report generation at {}", LocalDateTime.now());
        
        try {
            int reportsGenerated = monthlyRevenueReportService.generateMonthlyReports();
            log.info("Completed backup monthly revenue report generation. Generated {} reports", reportsGenerated);
        } catch (Exception e) {
            log.error("Error in backup monthly revenue report generation", e);
        }
    }
} 