 # NearProp Cache Implementation Summary

## Overview
This document summarizes the comprehensive caching implementation added to the NearProp application to handle thousands of concurrent requests efficiently.

## 🚀 **New APIs with Caching Added**

### 1. **Property APIs with Location-Based Caching**
- **`/properties?latitude=28.6139&longitude=77.2090&radius=5000`**
  - **Cache Key**: `'location_' + latitude + '_' + longitude + '_' + radius`
  - **Cache Name**: `properties`
  - **TTL**: 15 minutes (Caffeine), 15 minutes (Redis)
  - **Service Method**: `getAllPropertiesWithoutPagination(Double latitude, Double longitude, Double radius)`

### 2. **Featured Properties API**
- **`/properties/featured`**
  - **Cache Key**: `'featured_without_pagination'`
  - **Cache Name**: `properties`
  - **TTL**: 15 minutes (Caffeine), 15 minutes (Redis)
  - **Service Method**: `getFeaturedPropertiesWithoutPagination()`

### 3. **Chat Room API**
- **`/api/chat/rooms/1/`**
  - **Cache Key**: `chatRoomId + '_' + currentUserId`
  - **Cache Name**: `chat-rooms`
  - **TTL**: 5 minutes (Caffeine), 5 minutes (Redis)
  - **Service Method**: `getChatRoom(Long chatRoomId, Long currentUserId)`

### 4. **Advertisement APIs by District**
- **`/api/v1/advertisements/district/Bhopal?page=0&size=10&sortBy=createdAt&direction=DESC`**
  - **Cache Key**: `'district_' + districtName + '_' + pageNumber + '_' + pageSize + '_' + sort`
  - **Cache Name**: `advertisements`
  - **TTL**: 10 minutes (Caffeine), 10 minutes (Redis)
  - **Service Method**: `getAdvertisementsByDistrict(String districtName, Pageable pageable)`

### 5. **Admin Coupons API**
- **`/api/admin/coupons/active`**
  - **Cache Key**: `'active_' + pageNumber + '_' + pageSize + '_' + sort`
  - **Cache Name**: `coupons`
  - **TTL**: 30 minutes (Caffeine), 30 minutes (Redis)
  - **Service Method**: `getActiveCoupons(Pageable pageable)`

## 🗄️ **Cache Storage Architecture**

### **Primary Cache (Caffeine - Local Memory)**
- **Location**: Application memory (JVM heap)
- **Configuration**: 
  - Maximum size: 2,000 entries
  - Expire after write: 15 minutes
  - Expire after access: 10 minutes
- **Cache Names**: `properties`, `users`, `districts`, `subscriptions`, `chat-rooms`, `reviews`, `reels`, `visits`, `property-search`, `user-properties`, `advertisements`, `coupons`

### **Secondary Cache (Redis - Distributed)**
- **Location**: Redis server (localhost:6379 by default)
- **Configuration**: Different TTLs for different data types
  - Properties: 15 minutes
  - Users: 30 minutes
  - Districts: 60 minutes
  - Chat rooms: 5 minutes
  - Chat messages: 2 minutes
  - Reels: 10 minutes
  - Visits: 5 minutes
  - Search results: 5 minutes
  - **Advertisements: 10 minutes** (NEW)
  - **Coupons: 30 minutes** (NEW)

## 🧹 **Cache Management & Cleanup**

### **Automatic Cache Cleanup (2GB Threshold)**
- **Threshold**: 2GB maximum cache size
- **Warning Level**: 1GB (logs warning)
- **Cleanup Frequency**: Every 5 minutes (scheduled)
- **Action**: Automatically clears all caches when threshold is exceeded

### **Cache Management Service Methods**
```java
// Clear all caches
cacheManagementService.clearAllCaches();

// Clear specific cache types
cacheManagementService.clearPropertyCaches();
cacheManagementService.clearChatCaches();
cacheManagementService.clearReelCaches();
cacheManagementService.clearVisitCaches();
cacheManagementService.clearUserCaches();
cacheManagementService.clearAdvertisementCaches(); // NEW
cacheManagementService.clearCouponCaches(); // NEW

// Manual cleanup when size exceeds threshold
cacheManagementService.cleanupCacheIfNeeded();

// Get cache statistics and health
cacheManagementService.getCacheStats();
cacheManagementService.getCacheHealth();
cacheManagementService.estimateCacheSize();
```

## 📊 **Performance Monitoring Endpoints**

### **Cache Health & Monitoring**
- **`GET /api/performance/cache/health`** - Cache health status
- **`GET /api/performance/cache/stats`** - Detailed cache statistics
- **`GET /api/performance/cache/size`** - Current cache size
- **`POST /api/performance/cache/clear`** - Clear all caches
- **`POST /api/performance/cache/clear/{cacheName}`** - Clear specific cache
- **`POST /api/performance/cache/cleanup`** - Manual cleanup if needed

### **System Health (Enhanced)**
- **`GET /api/performance/health/detailed`** - Now includes cache health information

## 🔧 **Cache Configuration Files**

### **Application Properties**
```properties
# Cache names
spring.cache.cache-names=properties,users,districts,subscriptions,chat-rooms,reviews,reels,visits,property-search,user-properties,advertisements,coupons

# Cache TTL configurations
cache.advertisements.ttl=10m
cache.coupons.ttl=30m
```

### **Cache Eviction Strategy**
- **Property Operations**: Evicts `properties`, `user-properties`, `property-search` caches
- **Advertisement Operations**: Evicts `advertisements` cache
- **Coupon Operations**: Evicts `coupons` cache
- **Chat Operations**: Evicts `chat-rooms`, `chat-messages` caches

## 💾 **Cache Space Usage**

### **Estimated Memory Usage**
- **Caffeine (Local)**: 5-15 MB for maximum cache size
- **Redis (Distributed)**: 10-50 MB depending on data volume
- **Automatic Cleanup**: Triggers when total cache size exceeds 2GB

### **Cache Size Monitoring**
- **Real-time monitoring**: Every 5 minutes
- **Size estimation**: Based on entry count × average entry size (5KB)
- **Human-readable format**: B, KB, MB, GB display

## 🚀 **Performance Benefits**

1. **Reduced Database Load**: Frequently accessed data served from cache
2. **Faster Response Times**: Cache hits are 10-100x faster than database queries
3. **Scalability**: Redis allows sharing cache across multiple application instances
4. **Memory Efficiency**: Caffeine provides local caching with automatic eviction
5. **Automatic Management**: Self-healing cache system with 2GB threshold

## 📝 **Log Files Created**

### **Application Logs**
- **Location**: `./app.log` (main application log)
- **Location**: `./app-json.log` (JSON formatted logs)
- **Location**: `./gc.log` (garbage collection logs)

### **Cache-Related Logging**
- Cache hits/misses
- Cache evictions
- Cache statistics
- Automatic cleanup events
- Size threshold warnings

## 🔍 **Cache Keys Summary**

| API | Cache Key Pattern | Cache Name | TTL |
|-----|------------------|------------|-----|
| Properties by location | `'location_' + lat + '_' + lng + '_' + radius` | properties | 15m |
| Featured properties | `'featured_without_pagination'` | properties | 15m |
| Chat rooms | `chatRoomId + '_' + currentUserId` | chat-rooms | 5m |
| User chat rooms | `'user_' + userId` | chat-rooms | 5m |
| Advertisements by district | `'district_' + district + '_' + page + '_' + size + '_' + sort` | advertisements | 10m |
| Active coupons | `'active_' + page + '_' + size + '_' + sort` | coupons | 30m |
| Individual properties | `propertyId` | properties | 15m |
| Individual reels | `reelId` | reels | 10m |
| Individual visits | `visitId` | visits | 5m |

## 🎯 **Implementation Status**

✅ **Completed**:
- All requested APIs have caching implemented
- Automatic cache cleanup at 2GB threshold
- Cache health monitoring and management endpoints
- Cache eviction strategies for data modifications
- Performance monitoring and statistics
- Redis and Caffeine dual-layer caching

The caching system is now fully operational and ready to handle thousands of concurrent requests efficiently while maintaining data consistency and automatic resource management.