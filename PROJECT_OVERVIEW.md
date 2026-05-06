# NearProp Project Overview

## Project Description
NearProp is a comprehensive real estate application backend developed in Spring Boot 3.x that enables property transactions between various stakeholders including property sellers, advisors, developers, users (buyers/renters), and franchisees. The system uses role-based access control (RBAC), phone number-based OTP authentication, and JWT tokens for security.

## Implementation Status by Phase

### Phase 1: Authentication and Role Management ✅ (Completed)
- **Authentication System**
  - Phone number-based OTP authentication with session management
  - Support for both registration and login flows with verification
  - JWT token generation with appropriate claims (userId, roles, sessionId, etc.)
  - Different token expiration rules (non-expiring for ROLE_USER, 7-day for others)
  - Session tracking and management (single-device for regular users)

- **Role Management**
  - Role-based access control with multiple user roles:
    - ROLE_USER: Browse properties, schedule visits, interact with reels
    - ROLE_SELLER: List and manage properties for sale/rent
    - ROLE_ADVISOR: Advanced property management and reels
    - ROLE_DEVELOPER: List buildings with multiple units
    - ROLE_FRANCHISEE: District-level management
    - ADMIN: Full system control
  - Role request system for users to request seller/advisor/developer roles
  - Admin approval workflow for role requests
  - Support for users holding multiple roles simultaneously

### Phase 2: Property Management ✅ (Completed)
- **Property Listings**
  - Property creation, retrieval, updating, and deletion
  - Support for different property types (Buy, Rent, Lease, PG, Commercial)
  - Property status tracking (Available, Sold, Rented)
  - Property approval workflow (admin approval required for listings)
  - Featured property support with admin selection

- **Search and Filters**
  - Basic search implementation with filters
    - Property type, status, price range, BHKs, amenities
    - City and location-based search
  - Sorting options (price, date, relevance)

- **User Features**
  - Property browsing with filters
  - Property detail views with comprehensive information
  - Property favorites/wishlist functionality

### Phase 3: Video REELs and Chat ✅ (Completed)
- **Video REELs**
  - Upload and processing of property video reels
  - Storage management with appropriate sizing and formats
  - Social interactions for reels:
    - Likes: Users can like/unlike reels
    - Comments: Users can comment on reels (multiple comments supported)
    - Shares: Share reels via public links
    - Views: Track view counts and engagement
  - Reel discovery mechanisms:
    - Property-specific reels
    - User-specific reels
    - Location-based reels (city, district)
    - Feed-based discovery
  - Reel bookmarks/saves for later viewing

- **Real-time Chat System**
  - WebSocket-based property inquiry chat
  - Message persistence and retrieval
  - Chat thread management by property
  - User-to-seller/advisor/developer communication
  - Message history and conversation tracking

- **Reviews & Ratings**
  - Property reviews and ratings system
  - User review management (create, update, delete)
  - Rating statistics and aggregation for properties
  - User review history

### Phase 4: Visits and Subscriptions ✅ (Completed)
- **Visit Scheduling** ✅ (Completed)
  - Calendar-based property visit booking
  - Visit approval and management workflow
  - Visit status tracking (pending, approved, completed, canceled)
  - Visit history for both property owners and interested users

- **Subscription Management** ✅ (Completed)
  - Role-specific subscription plans:
    - Sellers: 300 INR/month per property
    - Property Advisors: Tiered plans (Basic: 1500 INR/month, Premium: 3000 INR/month)
    - Developers: Standard plan at 5000 INR/month
    - Franchisees: One-time annual fee of 50,000 INR + marketing fee
  - Subscription purchase and renewal workflow
  - Subscription status tracking (active, expired, canceled)
  - Plan-specific limitations on properties and reels
  - Auto-renewal support
  - Content visibility control based on subscription status
  - Automatic content hiding and deletion after configurable periods
  - Special handling of franchisee subscriptions (content always visible)
  - Marketing fee handling for franchise plans (initial subscriptions only)
  - Comprehensive subscription APIs

### Phase 5: Analytics and Final Features ✅ (Completed)
- **Analytics Dashboards** ✅ (Implemented)
  - Role-specific analytics dashboards for all user types
  - Property performance analytics with engagement metrics
  - Reel analytics with audience insights and engagement rates
  - Visit analytics with conversion tracking and scheduling patterns
  - Subscription analytics with renewal predictions and revenue forecasting
  - Revenue analytics for admin and franchisee roles
  - User engagement analytics with content performance metrics
  - System-wide analytics for administrators with health monitoring

- **Franchisee District Management** ✅ (Implemented)
  - District data loaded from JSON file with ~800 Indian districts
  - Franchise request system for users to apply for district ownership
  - Admin approval workflow for franchise requests with document verification
  - One franchisee per district restriction implemented
  - District-level property and user management implemented
  - Revenue sharing system (60% for franchisees) implemented
  - Franchisee performance metrics tracking
  - Revenue transaction recording and commission calculation
  - Payment status tracking for franchisee commissions

- **Multi-language Support** ✅ (Implemented)
  - Support for English and Hindi throughout the application
  - Comprehensive internationalization framework with message resources
  - User language preference storage and retrieval
  - Locale-specific formatting for dates, numbers, and currencies
  - Language switching API with cookie-based persistence
  - User preferences system for personalized settings

- **Payment Gateway Integration** ✅ (Implemented)
  - Razorpay payment gateway integration for secure online payments
  - Comprehensive payment transaction tracking with status management
  - Support for subscription payments, refunds, and receipts
  - Webhook handling for real-time payment status updates
  - Payment security with signature verification
  - Payment analytics and reporting
  - Integration with subscription system for automatic activation

## Technical Implementation Details

### Authentication and Security
- **JWT Implementation**
  - Custom JWT filter using JJWT library for token validation
  - Token structure with claims: userId, roles, sessionId, exp, iat, iss
  - Appropriate token expiration rules implemented
  - Protected endpoints with @PreAuthorize annotations

- **Session Management**
  - UserSession entity tracks active sessions
  - Session invalidation for non-franchisee roles on new login
  - Device info, IP, and timestamp tracking for all logins

- **OTP Verification**
  - OTP generation and validation logic
  - Temporary OTP storage with expiration
  - Ready for Twilio integration (currently using console/mock for development)

### Database Structure
- **Core Entities**
  - User: Authentication details and profile information
  - Role: Available system roles
  - RoleRequest: Role request tracking and approval status
  - Property: Comprehensive property details and approval status
  - UserSession: Active session tracking
  - OTP: OTP storage and validation
  - Reel: Video reel metadata
  - ReelInteraction: Tracks likes, comments, saves, and shares
  - Review: Property ratings and reviews
  - Visit: Property visit scheduling
  - Chat and ChatMessage: Chat threads and messages
  - SubscriptionPlan: Available subscription plans with pricing, limits, and content management settings
  - Subscription: User subscription tracking with content visibility status
  - District: Geographical districts with revenue share settings
  - FranchiseeDistrict: Maps franchisees to districts with performance metrics
  - DistrictRevenue: Tracks revenue transactions and franchisee commissions

- **Database Migrations**
  - Flyway migrations for schema evolution
  - Proper constraints and indices for performance
  - Recently fixed constraint issue with reel interactions to support multiple comments
  - Added subscription tables and default plans in V28 migration
  - Added franchisee district management tables in V29 migration
  - Added franchise request tables in V30 migration
  - Updated district references in V31 migration
  - Removed redundant district table in V32 migration
  - Added subscription content management fields in V33 migration

### API Structure
- **Authentication**
  - `POST /api/v1/auth/register` - Register new user
  - `POST /api/v1/auth/login` - Request OTP for login
  - `POST /api/v1/auth/verify-otp` - Verify OTP and get JWT token

- **User and Role Management**
  - `GET /api/v1/users/profile` - Get current user profile
  - `POST /api/v1/roles/{userId}/request` - Request a new role
  - `GET /api/v1/roles/my-requests` - Get user's role requests
  - `GET /api/v1/roles/requests/pending` - Get pending role requests (admin)
  - `POST /api/v1/roles/requests/{requestId}/process` - Process role request (admin)

- **Property Management**
  - `POST /api/properties` - Create new property
  - `GET /api/properties` - Get all approved properties
  - `GET /api/properties/{id}` - Get property by ID
  - `PUT /api/properties/{id}` - Update property
  - `DELETE /api/properties/{id}` - Delete property
  - `PUT /api/properties/{id}/approve` - Approve property (admin)
  - `PUT /api/properties/{id}/featured` - Mark property as featured (admin)
  - `GET /api/properties/search` - Search properties with filters

- **REELs**
  - `POST /api/reels` - Upload a new property video REEL
  - `GET /api/reels/{id}` - Get REEL by ID
  - `PUT /api/reels/{id}` - Update REEL details
  - `DELETE /api/reels/{id}` - Delete a REEL
  - `GET /api/reels/property/{propertyId}` - Get REELs for a specific property
  - `GET /api/reels/user/{userId}` - Get REELs created by a user
  - `GET /api/reels/discover` - Get trending/recommended REELs
  - `POST /api/reels/{reelId}/like` - Like/unlike a REEL
  - `POST /api/reels/{reelId}/comment` - Add a comment to a REEL
  - `GET /api/reels/{reelId}/comments` - Get comments for a REEL
  - `POST /api/reels/{reelId}/save` - Save/bookmark a REEL
  - `DELETE /api/reels/{reelId}/save` - Remove saved REEL
  - `GET /api/reels/saved` - Get saved REELs
  - `POST /api/reels/{reelId}/share` - Share a REEL
  - `POST /api/reels/{reelId}/view` - Record a view of a REEL
  - `GET /api/reels/nearby` - Get REELs near a location

- **Reviews & Ratings**
  - `POST /api/reviews` - Create a property review
  - `PUT /api/reviews/{id}` - Update a review
  - `DELETE /api/reviews/{id}` - Delete a review
  - `GET /api/reviews/{id}` - Get a review by ID
  - `GET /api/reviews/property/{propertyId}` - Get reviews for a property
  - `GET /api/reviews/user/{userId}` - Get reviews by a user
  - `GET /api/reviews/property/{propertyId}/rating` - Get rating statistics for a property

- **Chat System**
  - `GET /api/chats/property/{propertyId}` - Get chat thread for a property
  - `POST /api/chats/message` - Send a message (non-WebSocket fallback)
  - WebSocket endpoint: `/ws` for real-time messaging

- **Visit Scheduling**
  - `POST /api/visits` - Schedule a property visit
  - `GET /api/visits/{id}` - Get visit details
  - `PUT /api/visits/{id}/status` - Update visit status
  - `GET /api/visits/property/{propertyId}` - Get visits for a property
  - `GET /api/visits/user` - Get visits scheduled by current user

- **Subscriptions**
  - `GET /subscriptions/plans` - Get all available subscription plans
  - `GET /subscriptions/plans/{type}` - Get subscription plans by type
  - `POST /subscriptions` - Purchase a new subscription
  - `GET /subscriptions/{id}` - Get subscription details
  - `GET /subscriptions/my-subscriptions` - Get current user's subscriptions
  - `POST /subscriptions/{id}/cancel` - Cancel a subscription
  - `POST /subscriptions/{id}/renew` - Renew a subscription

- **Admin Subscription Management**
  - `POST /api/admin/subscription-plans` - Create new subscription plan
  - `GET /api/admin/subscription-plans` - Get all subscription plans
  - `GET /api/admin/subscription-plans/{id}` - Get subscription plan by ID
  - `GET /api/admin/subscription-plans/type/{type}` - Get plans by type
  - `PUT /api/admin/subscription-plans/{id}` - Update subscription plan
  - `PUT /api/admin/subscription-plans/{id}/activate` - Activate a plan
  - `PUT /api/admin/subscription-plans/{id}/deactivate` - Deactivate a plan
  - `DELETE /api/admin/subscription-plans/{id}` - Delete a subscription plan

- **Franchisee Management**
  - `GET /api/districts` - Get all districts from JSON data
  - `GET /api/districts/{id}` - Get district by ID
  - `GET /api/districts/states` - Get all states
  - `GET /api/districts/by-state/{state}` - Get districts by state
  - `GET /api/districts/by-name/{name}` - Get district by name
  - `GET /api/districts/assigned` - Get district assignment status
  
  - `POST /api/franchisee/requests` - Submit a franchise request
  - `GET /api/franchisee/requests/{id}` - Get request by ID
  - `GET /api/franchisee/requests/status/{status}` - Get requests by status
  - `GET /api/franchisee/requests/my-requests` - Get user's franchise requests
  - `PUT /api/franchisee/requests/{id}/approve` - Approve a franchise request (admin)
  - `PUT /api/franchisee/requests/{id}/reject` - Reject a franchise request (admin)
  
  - `GET /api/franchisee/assignments` - Get all franchisee district assignments
  - `PUT /api/franchisee/assignments/{id}` - Update franchisee district assignment
  - `DELETE /api/franchisee/assignments/{id}` - Terminate franchisee district assignment
  
  - `GET /api/franchisee/revenue/district/{districtId}` - Get revenue for district
  - `GET /api/franchisee/revenue/franchisee/{franchiseeId}` - Get revenue for franchisee
  - `POST /api/franchisee/revenue/record` - Record new revenue transaction
  - `PUT /api/franchisee/revenue/{id}/payment-status` - Update payment status
  - `GET /api/franchisee/revenue/stats/franchisee/{franchiseeId}` - Get franchisee revenue statistics
  - `GET /api/franchisee/revenue/stats/district/{districtId}` - Get district revenue statistics

## Recent Improvements

1. **Enhanced Subscription Management System**
   - Implemented comprehensive subscription plan types (BASIC, PREMIUM, PRO, FRANCHISEE)
   - Added special marketing fee processing for franchisee plans (charged only on initial subscriptions)
   - Implemented content visibility control tied to subscription status
   - Created configurable periods for content hiding and deletion after subscription expiry
   - Added automatic content management for expired subscriptions with special handling for franchisee content
   - Added admin subscription plan management API
   - Created detailed Postman collection for testing subscription management

2. **Payment Gateway Integration**
   - Integrated Razorpay payment gateway for secure online payments
   - Implemented payment transaction tracking with comprehensive status management
   - Added support for subscription payments, refunds, and receipts
   - Created webhook handling for real-time payment status updates
   - Implemented signature verification for payment security
   - Added detailed payment analytics and reporting

3. **Advanced Analytics System**
   - Implemented role-specific analytics dashboards
   - Added property performance analytics with engagement metrics
   - Created reel performance analytics with audience insights
   - Added visit analytics with conversion tracking
   - Implemented subscription analytics with renewal predictions
   - Added revenue analytics for admin and franchisee roles
   - Created user engagement analytics with content performance metrics
   - Added system-wide analytics for administrators

4. **Multi-language Support**
   - Implemented internationalization with support for English and Hindi
   - Added user language preferences with persistent settings
   - Created localized message resources for all UI elements
   - Implemented locale switching API
   - Added language-specific formatting for dates, numbers, and currencies
   - Created user preferences system for personalized settings

5. **Fixed REEL Multiple Comments Issue**
   - Updated database constraints to allow multiple comments from the same user on different reels
   - Fixed UI to properly display all comments

## Current Status

The NearProp application is now feature-complete with all major systems implemented and tested. The application provides a comprehensive platform for real estate transactions with robust subscription management, secure payment processing, detailed analytics, and multi-language support.

The system is currently deployed and running on port 8081, with all core functionality operational. The application has been thoroughly tested with Postman collections for each major feature area.

## Next Steps

1. **Performance Optimization**
   - Implement caching for frequently accessed data
   - Optimize database queries for analytics dashboards
   - Add pagination for large data sets
   - Implement database indexing for common search patterns

2. **Mobile App Integration**
   - Create mobile-specific API endpoints
   - Implement push notification service
   - Add mobile-specific authentication flows
   - Develop mobile-optimized media delivery

3. **Advanced Search Features**
   - Implement geo-spatial search capabilities
   - Add property recommendation engine
   - Create saved search functionality with alerts
   - Implement AI-powered property matching

4. **Enhanced Security**
   - Implement rate limiting for all API endpoints
   - Add IP-based fraud detection
   - Implement two-factor authentication for sensitive operations
   - Regular security audits and penetration testing

## Technology Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: PostgreSQL for persistent storage
- **Security**: Spring Security with JWT authentication
- **WebSocket**: For real-time chat functionality
- **ORM**: JPA/Hibernate
- **Storage**: AWS S3 for video and document storage
- **Migrations**: Flyway for database schema evolution
- **Object Mapping**: MapStruct
- **Code Reduction**: Lombok
- **Testing**: JUnit 5, Mockito

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── nearprop/
│   │           ├── config/           # Configuration classes
│   │           ├── controller/       # REST controllers (Auth, User, Property, REELs, Chat, etc.)
│   │           ├── dto/             # Data Transfer Objects
│   │           ├── entity/          # JPA entities (User, Role, Property, Reel, Visit, etc.)
│   │           ├── exception/       # Custom exceptions
│   │           ├── mapper/          # MapStruct mappers
│   │           ├── repository/      # JPA repositories
│   │           ├── security/        # JWT utilities and filters
│   │           ├── service/         # Service interfaces and implementations
│   │           └── util/            # Utility classes
│   └── resources/
│       ├── application.properties   # Main configuration
│       └── db/
│           └── migration/           # Flyway database migrations
```

## Testing Strategy

- **Unit Tests**: Tests for individual components with mocked dependencies
- **Integration Tests**: Tests for API endpoints and service integrations
- **API Testing**: Manual testing via Postman collections, including:
  - Authentication flows
  - Property management operations
  - REELs functionality with social interactions
  - Chat operations
  - Visit scheduling
  - Subscription management with plan creation and content visibility
- **All collections are regularly updated to test new features and bug fixes**

## Requirements Coverage

- ✅ Authentication and Role Management (Phase 1): 100% complete
- ✅ Property Management (Phase 2): 100% complete
- ✅ Video REELs and Chat (Phase 3): 100% complete
- ✅ Visits and Subscriptions (Phase 4): 100% complete
- ✅ Analytics and Final Features (Phase 5): 100% complete

The project is now feature-complete. Future development will focus on performance optimization, mobile app integration, advanced search features, and enhanced security measures as outlined in the Next Steps section.
