# NearProp API Testing Report

## Overview
This report documents the testing process and results for the NearProp API endpoints, specifically focusing on the franchisee management functionality.

## Testing Methodology
- Individual endpoint testing using curl commands
- Authentication using different user role tokens (admin, user, seller, developer)
- Automated script to test multiple endpoints sequentially
- Verification of response codes and data integrity

## Key Findings

### Issue 1: API Path Prefix
- **Problem**: All endpoints needed to be prefixed with `/api`
- **Cause**: The application is configured with `server.servlet.context-path=/api` in application.properties
- **Solution**: Updated all endpoint URLs in the collection to include the `/api` prefix

### Issue 2: Authentication and Authorization
- **Problem**: Some admin-only endpoints were returning 403 Forbidden errors
- **Cause**: JWT token validation issues and session management problems
- **Solution**: 
  - Added enhanced logging to the JwtAuthenticationFilter
  - Modified SecurityConfig to properly handle role-based access
  - Created test endpoints to verify authentication flow

### Issue 3: Endpoint Permissions
- **Problem**: Public endpoints that should be accessible without authentication were secured
- **Cause**: Missing entries in the SecurityConfig permitAll list
- **Solution**: Updated SecurityConfig to explicitly allow public endpoints like property-districts

## Working Endpoints

### District Management
- `GET /api/property-districts` - Get all districts
- `GET /api/property-districts/{id}` - Get district by ID
- `GET /api/property-districts/by-state/{state}` - Get districts by state
- `GET /api/property-districts/states` - Get all states

### Franchise Request Management
- `GET /api/franchisee/requests/my-requests` - Get current user's franchise requests
- `GET /api/franchisee/requests/is-district-assigned/{districtId}` - Check if district is assigned
- `GET /api/franchisee/test/ping` - Test endpoint to verify authentication

## Recommendations
1. Update client-side code to use correct API paths with the `/api` prefix
2. Review and fix role-based permissions on admin endpoints
3. Implement proper error handling for authentication failures
4. Consider adding API versioning for better maintainability
5. Add comprehensive API documentation using Swagger/OpenAPI

## Test Script
A test script (`test_api_endpoints.sh`) has been created to automatically test all endpoints with appropriate authorization tokens. This script should be run after any significant changes to the API.

## Recent Fixes
1. Added proper context path handling for all endpoints
2. Enhanced logging in authentication filter for better debugging
3. Created test endpoints to verify authentication flow
4. Updated security configuration to allow public endpoints
5. Fixed admin role authorization issues

## Test Date: June 3, 2025

## Authentication APIs

| API Endpoint | Method | Status | Notes |
|--------------|--------|--------|-------|
| `/api/v1/auth/register` | POST | ✅ Pass | Successfully registers a new user |
| `/api/v1/auth/login` | POST | ✅ Pass | Successfully sends OTP to registered mobile number |
| `/api/v1/auth/verify-otp` | POST | ✅ Pass | Successfully verifies OTP and returns JWT token |
| `/api/v1/auth/request-role` | POST | ✅ Pass | Successfully submits role request |

## User APIs

| API Endpoint | Method | Status | Notes |
|--------------|--------|--------|-------|
| `/api/v1/users/profile` | GET | ✅ Pass | Successfully retrieves current user profile |
| `/api/v1/users/{userId}` | GET | ✅ Pass | Admin can successfully retrieve user by ID |

## Role Management APIs

| API Endpoint | Method | Status | Notes |
|--------------|--------|--------|-------|
| `/api/v1/roles/{userId}/add/{role}` | POST | ✅ Pass | Admin can successfully add role to user |

## Property District APIs

| API Endpoint | Method | Status | Notes |
|--------------|--------|--------|-------|
| `/api/property-districts/states` | GET | ✅ Pass | Successfully retrieves all states |
| `/api/property-districts/by-state/{state}` | GET | ✅ Pass | Successfully retrieves districts by state |
| `/api/api/districts/test/{id}` | GET | ✅ Pass | Successfully tests if a district exists |

## Property APIs

| API Endpoint | Method | Status | Notes |
|--------------|--------|--------|-------|
| `/api/properties` | POST | ❌ Fail | Database constraint violation with district relationship |
| `/api/properties/search` | GET | ✅ Pass | Successfully searches for properties (returns empty result) |
| `/api/properties/stats/by-type` | GET | ✅ Pass | Successfully retrieves property statistics by type |
| `/api/properties/admin/pending` | GET | ✅ Pass | Successfully retrieves pending properties for admin |
| `/api/properties/states` | GET | ✅ Pass | Successfully retrieves all states with properties |

## Issues Found

### 1. Property Creation Issue
- **Description**: When creating a property, there's a database constraint violation with the district relationship
- **Error Message**: `null value in column "district" of relation "properties" violates not-null constraint`
- **Attempted Fix**: Changed the district relationship in Property entity from LAZY to EAGER loading and added `updatable = false` to ensure proper persistence
- **Status**: Still failing, needs further investigation

### 2. Incorrect User Profile Endpoint
- **Description**: The endpoint `/api/v1/users/me` doesn't exist, the correct endpoint is `/api/v1/users/profile`
- **Status**: Documentation updated to reflect correct endpoint 