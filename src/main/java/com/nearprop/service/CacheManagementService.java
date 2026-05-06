package com.nearprop.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class CacheManagementService {

    private final CacheManager cacheManager;
    private static final long MAX_CACHE_SIZE_BYTES = 2L * 1024L * 1024L * 1024L; // 2GB
    private static final long WARNING_CACHE_SIZE_BYTES = 1L * 1024L * 1024L * 1024L; // 1GB

    public CacheManagementService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Clear all caches
     */
    public void clearAllCaches() {
        log.info("Clearing all caches");
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("Cleared cache: {}", cacheName);
            }
        });
    }

    /**
     * Clear specific cache
     */
    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("Cleared cache: {}", cacheName);
        }
    }

    /**
     * Clear property-related caches
     */
    public void clearPropertyCaches() {
        clearCache("properties");
        clearCache("property-search");
        clearCache("user-properties");
        log.info("Cleared all property-related caches");
    }

    /**
     * Clear chat-related caches
     */
    public void clearChatCaches() {
        clearCache("chat-rooms");
        clearCache("chat-messages");
        log.info("Cleared all chat-related caches");
    }

    /**
     * Clear reel-related caches
     */
    public void clearReelCaches() {
        clearCache("reels");
        log.info("Cleared all reel-related caches");
    }

    /**
     * Clear visit-related caches
     */
    public void clearVisitCaches() {
        clearCache("visits");
        log.info("Cleared all visit-related caches");
    }

    /**
     * Clear user-related caches
     */
    public void clearUserCaches() {
        clearCache("users");
        log.info("Cleared all user-related caches");
    }

    /**
     * Clear advertisement-related caches
     */
    public void clearAdvertisementCaches() {
        clearCache("advertisements");
        log.info("Cleared all advertisement-related caches");
    }

    /**
     * Clear coupon-related caches
     */
    public void clearCouponCaches() {
        clearCache("coupons");
        log.info("Cleared all coupon-related caches");
    }

    /**
     * Get cache statistics
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Map<String, Object> cacheStats = new HashMap<>();
                cacheStats.put("name", cacheName);
                cacheStats.put("nativeCache", cache.getNativeCache().getClass().getSimpleName());
                
                // Try to get cache statistics if available
                if (cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
                    com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache = 
                        (com.github.benmanes.caffeine.cache.Cache<?, ?>) cache.getNativeCache();
                    cacheStats.put("estimatedSize", caffeineCache.estimatedSize());
                    cacheStats.put("stats", caffeineCache.stats());
                }
                
                stats.put(cacheName, cacheStats);
            }
        });
        
        return stats;
    }

    /**
     * Get all cache names
     */
    public java.util.Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }

    /**
     * Estimate cache size in bytes
     */
    public long estimateCacheSize() {
        long totalSize = 0;
        
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null && cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
                com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache = 
                    (com.github.benmanes.caffeine.cache.Cache<?, ?>) cache.getNativeCache();
                
                // Rough estimation: assume average entry size of 5KB
                long estimatedSize = caffeineCache.estimatedSize() * 5 * 1024;
                totalSize += estimatedSize;
            }
        }
        
        return totalSize;
    }

    /**
     * Get cache size in human readable format
     */
    public String getCacheSizeHumanReadable() {
        long sizeBytes = estimateCacheSize();
        return formatBytes(sizeBytes);
    }

    /**
     * Check if cache size exceeds threshold and clear if necessary
     */
    // @Scheduled(fixedRate = 300000) // Run every 5 minutes - DISABLED to prevent log files
    public void checkAndClearCacheIfNeeded() {
        long currentSize = estimateCacheSize();
        
        if (currentSize > MAX_CACHE_SIZE_BYTES) {
            log.warn("Cache size ({}) exceeds maximum threshold ({}). Clearing all caches.", 
                    formatBytes(currentSize), formatBytes(MAX_CACHE_SIZE_BYTES));
            clearAllCaches();
        } else if (currentSize > WARNING_CACHE_SIZE_BYTES) {
            log.warn("Cache size ({}) is approaching maximum threshold ({}). Consider clearing caches.", 
                    formatBytes(currentSize), formatBytes(MAX_CACHE_SIZE_BYTES));
        } else {
            log.debug("Cache size: {}", formatBytes(currentSize));
        }
    }

    /**
     * Manual cache cleanup when size exceeds threshold
     */
    public boolean cleanupCacheIfNeeded() {
        long currentSize = estimateCacheSize();
        
        if (currentSize > MAX_CACHE_SIZE_BYTES) {
            log.warn("Manual cache cleanup triggered. Size: {}, Threshold: {}", 
                    formatBytes(currentSize), formatBytes(MAX_CACHE_SIZE_BYTES));
            clearAllCaches();
            return true;
        }
        
        return false;
    }

    /**
     * Get cache health status
     */
    public Map<String, Object> getCacheHealth() {
        Map<String, Object> health = new HashMap<>();
        long currentSize = estimateCacheSize();
        
        health.put("currentSizeBytes", currentSize);
        health.put("currentSizeHumanReadable", formatBytes(currentSize));
        health.put("maxSizeBytes", MAX_CACHE_SIZE_BYTES);
        health.put("maxSizeHumanReadable", formatBytes(MAX_CACHE_SIZE_BYTES));
        health.put("warningSizeBytes", WARNING_CACHE_SIZE_BYTES);
        health.put("warningSizeHumanReadable", formatBytes(WARNING_CACHE_SIZE_BYTES));
        health.put("usagePercentage", (double) currentSize / MAX_CACHE_SIZE_BYTES * 100);
        health.put("needsCleanup", currentSize > MAX_CACHE_SIZE_BYTES);
        health.put("nearThreshold", currentSize > WARNING_CACHE_SIZE_BYTES);
        
        return health;
    }

    /**
     * Format bytes to human readable string
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
} 