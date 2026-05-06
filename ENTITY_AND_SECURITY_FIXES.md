# Entity Recursion and Security Fixes

## Fixed Issues

### 1. StackOverflowError in Entity ToString Methods

The application was experiencing StackOverflowError exceptions due to infinite recursion in the `toString()` methods of entities with bidirectional relationships.

**Root Cause:**
- `User` entity had bidirectional relationship with `Property` through favorites
- `Property` entity had bidirectional relationship with `User` through favoritedBy, owner, and addedByUser
- When `toString()` was called, these entities would infinitely call each other's `toString()` methods

**Fix Applied:**
- Modified `User` entity to exclude favorites from `@ToString` 
- Replaced `@Data` in `Property` entity with explicit annotations (`@Getter`, `@Setter`, `@ToString`)
- Added proper exclusions for bidirectional relationships in `@ToString` in both entities

### 2. AccessDeniedException in Admin Endpoints

Some endpoints requiring the ADMIN role were receiving AccessDeniedExceptions.

**Root Cause:**
- Potential issues with role assignment in JWT authentication
- Need to verify whether user roles are properly loaded and converted to authorities

**Fix Applied:**
- Added enhanced logging for roles and authorities in JwtAuthenticationFilter
- Created a debug endpoint at `/api/performance/auth-debug` to inspect authentication state
- Added the endpoint to the permitted URLs in SecurityConfig

## Verification Steps

1. The application should start without StackOverflowError exceptions
2. Check the `/api/performance/auth-debug` endpoint to verify authentication and roles
3. If admin roles are still not working, examine the logs to see what roles are being assigned

## Next Steps if Issues Persist

1. **If StackOverflowError continues:** Check other entities for bidirectional relationships and add `@ToString.Exclude` annotations
2. **If AccessDeniedException continues:** 
   - Verify admin users in the database have the proper ADMIN role
   - Examine JWT token contents to ensure roles are included
   - Review how roles are mapped to authorities in Spring Security

A restart script (`restart-with-fixes.sh`) has been provided to restart the application with enhanced security debugging.
