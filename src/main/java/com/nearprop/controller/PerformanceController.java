package com.nearprop.controller;

import com.nearprop.service.CacheManagementService;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/performance")
public class PerformanceController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheManagementService cacheManagementService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("application", "NearProp");
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/health/detailed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDetailedSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // Database Connection Pool Status
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDS = (HikariDataSource) dataSource;
            HikariPoolMXBean poolMXBean = hikariDS.getHikariPoolMXBean();
            
            Map<String, Object> dbPool = new HashMap<>();
            dbPool.put("activeConnections", poolMXBean.getActiveConnections());
            dbPool.put("idleConnections", poolMXBean.getIdleConnections());
            dbPool.put("totalConnections", poolMXBean.getTotalConnections());
            dbPool.put("threadsAwaitingConnection", poolMXBean.getThreadsAwaitingConnection());
            dbPool.put("maxPoolSize", hikariDS.getMaximumPoolSize());
            dbPool.put("minIdle", hikariDS.getMinimumIdle());
            
            health.put("databasePool", dbPool);
        }
        
        // JVM Memory Status
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        Map<String, Object> memory = new HashMap<>();
        memory.put("heapUsed", memoryBean.getHeapMemoryUsage().getUsed());
        memory.put("heapMax", memoryBean.getHeapMemoryUsage().getMax());
        memory.put("heapCommitted", memoryBean.getHeapMemoryUsage().getCommitted());
        memory.put("nonHeapUsed", memoryBean.getNonHeapMemoryUsage().getUsed());
        memory.put("nonHeapMax", memoryBean.getNonHeapMemoryUsage().getMax());
        
        health.put("memory", memory);
        
        // Thread Status
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Map<String, Object> threads = new HashMap<>();
        threads.put("totalStarted", threadBean.getTotalStartedThreadCount());
        threads.put("active", threadBean.getThreadCount());
        threads.put("peak", threadBean.getPeakThreadCount());
        threads.put("daemon", threadBean.getDaemonThreadCount());
        
        health.put("threads", threads);
        
        // Cache Status
        Map<String, Object> cacheStatus = new HashMap<>();
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Map<String, Object> cacheInfo = new HashMap<>();
                cacheInfo.put("name", cacheName);
                cacheInfo.put("nativeCache", cache.getNativeCache().getClass().getSimpleName());
                cacheStatus.put(cacheName, cacheInfo);
            }
        });
        
        health.put("caches", cacheStatus);
        
        // Cache Health
        health.put("cacheHealth", cacheManagementService.getCacheHealth());
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/cache/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        return ResponseEntity.ok(cacheManagementService.getCacheStats());
    }

    @GetMapping("/cache/health")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCacheHealth() {
        return ResponseEntity.ok(cacheManagementService.getCacheHealth());
    }

    @PostMapping("/cache/clear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> clearAllCaches() {
        cacheManagementService.clearAllCaches();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "All caches cleared successfully");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cache/clear/{cacheName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> clearSpecificCache(@PathVariable String cacheName) {
        cacheManagementService.clearCache(cacheName);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cache '" + cacheName + "' cleared successfully");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cache/clear/property")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> clearPropertyCaches() {
        cacheManagementService.clearPropertyCaches();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Property caches cleared successfully");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cache/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cleanupCacheIfNeeded() {
        boolean cleaned = cacheManagementService.cleanupCacheIfNeeded();
        Map<String, Object> response = new HashMap<>();
        if (cleaned) {
            response.put("message", "Cache cleanup performed - size exceeded 2GB threshold");
        } else {
            response.put("message", "No cache cleanup needed - size is within limits");
        }
        response.put("cleaned", cleaned);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cache/size")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCacheSize() {
        Map<String, Object> response = new HashMap<>();
        response.put("sizeBytes", cacheManagementService.estimateCacheSize());
        response.put("sizeHumanReadable", cacheManagementService.getCacheSizeHumanReadable());
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth-debug")
    public ResponseEntity<Map<String, Object>> getAuthDebugInfo() {
        Map<String, Object> response = new HashMap<>();
        
        // Get the current authentication
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            response.put("authenticated", auth.isAuthenticated());
            response.put("principal", auth.getPrincipal().toString());
            
            // Extract roles/authorities
            var authorities = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(java.util.stream.Collectors.toList());
            response.put("authorities", authorities);
            
            // Check for specific roles
            boolean hasAdminRole = authorities.contains("ROLE_ADMIN");
            response.put("hasAdminRole", hasAdminRole);
            
            // If principal is User entity
            if (auth.getPrincipal() instanceof com.nearprop.entity.User) {
                var user = (com.nearprop.entity.User) auth.getPrincipal();
                response.put("userId", user.getId());
                response.put("userRoles", user.getRoles());
            }
        } else {
            response.put("authenticated", false);
            response.put("message", "No authentication found in SecurityContext");
        }
        
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}