# NearProp - Enterprise Real Estate Management Platform

<p align="center">
  <img src="https://img.shields.io/badge/version-1.0.0-blue.svg" alt="Version 1.0.0">
  <img src="https://img.shields.io/badge/java-17-orange.svg" alt="Java 17">
  <img src="https://img.shields.io/badge/spring%20boot-3.x-brightgreen.svg" alt="Spring Boot 3.x">
  <img src="https://img.shields.io/badge/postgresql-12+-purple.svg" alt="PostgreSQL 12+">
  <img src="https://img.shields.io/badge/license-Proprietary-red.svg" alt="License">
</p>

## Table of Contents

- [Introduction](#introduction)
- [Core Features](#core-features)
  - [User Management](#user-management)
  - [Property Management](#property-management)
  - [Franchisee System](#franchisee-system)
  - [Subscription and Payment](#subscription-and-payment)
  - [Media Management](#media-management)
  - [Communication Features](#communication-features)
  - [Advertisement System](#advertisement-system)
  - [Analytics and Reporting](#analytics-and-reporting)
- [System Architecture](#system-architecture)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)
- [Installation Guide](#installation-guide)
- [Configuration](#configuration)
- [Security Features](#security-features)
- [User Roles and Permissions](#user-roles-and-permissions)
- [Development Guidelines](#development-guidelines)
- [Testing Guidelines](#testing-guidelines)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Introduction

NearProp is an enterprise-grade real estate management platform developed using Spring Boot that revolutionizes property transactions and management. The platform connects various stakeholders including buyers, sellers, property advisors, developers, and franchisees through a comprehensive ecosystem of integrated services and features.

### Problem Statement

The real estate market faces significant challenges such as:

1. **Information Asymmetry**: Buyers and sellers operate with imbalanced information access
2. **Geographic Fragmentation**: Real estate operations vary significantly by location/district
3. **Process Complexity**: Multiple stakeholders involved in the transaction lifecycle
4. **Verification Challenges**: Property ownership and details verification is difficult
5. **Marketing Limitations**: Property visibility and marketing can be limited
6. **Revenue Management**: Complex commission structures for agents/franchisees

### Solution Overview

NearProp addresses these challenges through:

1. **Unified Platform**: A centralized system connecting all stakeholders
2. **District-based Operations**: Geographic organization with franchisee management
3. **Role-based Access**: Specialized functionality for different user roles
4. **Verification System**: Property approval and verification workflows
5. **Multi-channel Promotion**: Property listings, reels, and advertisements
6. **Transparent Revenue**: Clear revenue sharing between company and franchisees

### Key Differentiators

- **District-based Franchising Model**: Unique geographical division of operations
- **Property Reels**: Short-form video content for property showcase
- **Comprehensive Subscription Model**: Tailored plans for different user roles
- **Real-time Communication**: WebSocket-based chat between stakeholders
- **Advanced Analytics**: Detailed performance metrics for all stakeholders
- **Multi-language Support**: Content available in multiple languages

## Core Features

### User Management

NearProp implements a sophisticated user management system with the following features:

#### Authentication and Identity
- **Phone OTP-based Authentication**: Secure login using one-time passwords sent to mobile
- **JWT Token Generation**: Role-specific token creation with appropriate expiration
- **Session Management**: Tracking active user sessions with single-device enforcement
- **Role-based Access Control**: Granular permissions based on user roles

#### User Profile Management
- **Comprehensive Profile Data**: Name, contact information, address, profile image
- **Preference Settings**: Language, notification preferences, UI customization
- **Follow/Following System**: User following functionality for updates
- **Activity Tracking**: User engagement and interaction history

#### Role Management
- **Multiple Role Support**: User, Seller, Advisor, Developer, Franchisee, Admin
- **Role Request System**: Process for requesting elevated privileges
- **Admin Approval Workflow**: Review and approval for role change requests
- **Multi-role Assignment**: Users can have multiple roles simultaneously

#### Integration Features
- **Email Communication**: Automated notifications for account activities
- **SMS Notifications**: Important alerts via SMS
- **Social Sharing**: Integration with social platforms

### Property Management

The property management system forms the core of NearProp with the following features:

#### Property Listing
- **Multi-type Properties**: Support for Buy, Rent, Lease, PG, and Commercial properties
- **Detailed Attributes**: Comprehensive property specifications
- **Multiple Classifications**: Based on type, status, features, and amenities
- **Rich Media Support**: Multiple images, videos, and virtual tours
- **Location Management**: Precise geocoding and district mapping

#### Search and Discovery
- **Advanced Search Engine**: Multi-parameter filtering
- **Geolocation Search**: Location-based property discovery
- **Saved Searches**: User can save search criteria
- **Personalized Recommendations**: Based on user preferences
- **Featured Properties**: Admin-promoted property listings

#### Property Lifecycle
- **Status Management**: Available, Sold, Rented, Under Contract
- **Approval Workflow**: Admin verification process
- **Property Updates**: Change tracking and history
- **Activation/Deactivation**: Based on subscription status
- **Stock Management**: For developers with multiple units

#### User Interactions
- **Favorites System**: Save properties for later viewing
- **Visit Scheduling**: Book property tours
- **Reviews & Ratings**: User feedback system
- **Property Sharing**: Share listings with others
- **Reporting**: Flag inappropriate listings

### Franchisee System

NearProp features a unique district-based franchisee management system:

#### District Management
- **Geographic Division**: District definition and boundaries
- **District Assignment**: Mapping franchisees to districts
- **District Hierarchy**: State and city-level organization
- **GeoJSON Support**: Geographic data storage and visualization
- **Coverage Analysis**: District coverage metrics

#### Franchisee Operations
- **Franchisee Registration**: Application and approval process
- **District-level Management**: Local operation control
- **Property Addition**: Adding properties in assigned districts
- **Revenue Tracking**: District-level income monitoring
- **Performance Analytics**: KPI tracking for districts

#### Revenue Management
- **Revenue Sharing Model**: Typically 60/40 split (franchisee/company)
- **Automatic Calculation**: System-generated revenue figures
- **Commission Tracking**: Transparent commission accounting
- **Withdrawal System**: Process for franchisees to withdraw funds
- **Financial Reporting**: Detailed financial statements

#### Administration
- **Approval Workflows**: For franchisee applications
- **District Allocation**: Admin tools for district assignment
- **Performance Monitoring**: Franchisee activity tracking
- **Revenue Verification**: Admin review of revenue calculations
- **Withdrawal Approval**: Processing withdrawal requests

### Subscription and Payment

NearProp includes a comprehensive subscription management system:

#### Subscription Plans
- **Role-based Plans**: Different plans for sellers, advisors, developers
- **Tiered Features**: Basic, Standard, Premium offerings
- **Duration Options**: Monthly, quarterly, annual plans
- **Custom Plans**: Special packages for high-volume users
- **Feature Limitations**: Clear limits on properties, reels, etc.

#### Payment Processing
- **Razorpay Integration**: Secure payment gateway
- **Multiple Payment Methods**: Credit/debit cards, UPI, net banking
- **Coupon System**: Discount code application
- **Auto-renewal**: Optional subscription auto-renewal
- **Payment Verification**: Transaction confirmation and receipt

#### Subscription Management
- **Expiry Handling**: Grace period and expiration workflows
- **Renewal Reminders**: Automated notifications
- **Subscription History**: Record of past subscriptions
- **Plan Switching**: Upgrade/downgrade functionality
- **Refund Processing**: Admin tools for refund management

#### Content Management
- **Content Visibility**: Based on subscription status
- **Grace Period**: Content remains visible briefly after expiry
- **Content Hiding**: Automatic removal from public view
- **Content Deletion**: Optional permanent removal
- **Reactivation**: Quick restoration on renewal

### Media Management

NearProp offers robust media management capabilities:

#### Image Management
- **Multiple Property Images**: Support for multiple images per property
- **Image Optimization**: Automatic resizing and compression
- **Image Ordering**: Custom sorting of property images
- **Image Moderation**: Review system for uploaded content
- **AWS S3 Storage**: Scalable cloud storage solution

#### Reel Videos
- **Property Reels**: Short-form video content creation
- **Reel Processing**: Transcoding and optimization
- **Thumbnail Generation**: Automatic preview creation
- **Reel Analytics**: View counts and engagement metrics
- **Reel Moderation**: Content review and approval

#### File Storage
- **AWS S3 Integration**: Cloud storage for all media
- **Local Storage Fallback**: Alternative storage option
- **Directory Organization**: Structured file management
- **Access Control**: Secured file access
- **CDN Support**: Fast content delivery

#### Media Processing
- **Video Transcoding**: Format conversion
- **Image Resizing**: Dynamic image resizing
- **Thumbnail Creation**: Automatic thumbnail generation
- **Metadata Extraction**: File information parsing
- **Watermarking**: Optional content protection

### Communication Features

NearProp implements comprehensive communication capabilities:

#### Chat System
- **WebSocket-based Messaging**: Real-time chat functionality
- **Private Conversations**: One-on-one messaging
- **Group Chats**: Multi-user conversations
- **Media Sharing**: Support for image/file attachments
- **Chat History**: Persistent message storage

#### Email Notifications
- **Transactional Emails**: Activity-based notifications
- **Property Updates**: Changes to watched properties
- **System Alerts**: Important system announcements
- **Customizable Templates**: HTML email templates
- **Email Preferences**: User notification settings

#### In-app Notifications
- **Real-time Alerts**: Instant notification delivery
- **Notification Center**: Centralized notification management
- **Read Status Tracking**: Track seen/unseen notifications
- **Action-based Notifications**: Activity-triggered alerts
- **Notification Preferences**: User customization options

#### Multi-language Support
- **Interface Localization**: UI in multiple languages
- **Content Translation**: Property details translation
- **Language Preferences**: User language settings
- **Regional Formatting**: Date, time, currency localization
- **Translation API Integration**: Automatic translation services

### Advertisement System

NearProp includes a sophisticated advertisement management system:

#### Advertisement Creation
- **Campaign Management**: Structured ad campaigns
- **Target Audience Selection**: User segment targeting
- **Placement Options**: Various ad placements
- **Creative Tools**: Ad design assistance
- **Scheduling**: Campaign start/end dates

#### Performance Tracking
- **Impression Counting**: View tracking
- **Click Monitoring**: Interaction tracking
- **Conversion Analytics**: Effectiveness measurement
- **User Engagement**: Time spent, interactions
- **ROI Calculation**: Return on investment metrics

#### Advertisement Administration
- **Approval Workflow**: Review process
- **Content Guidelines**: Advertisement standards
- **Pricing Models**: Cost structure for advertisers
- **Budget Management**: Spending controls
- **Performance Reports**: Detailed analytics

#### Featured Properties
- **Premium Placement**: Enhanced visibility
- **Spotlight Rotation**: Featured property rotation
- **Visual Differentiation**: Distinct styling
- **Performance Analytics**: Featured property metrics
- **Duration Management**: Feature time periods

### Analytics and Reporting

NearProp delivers comprehensive analytics across all modules:

#### User Analytics
- **Registration Metrics**: New user statistics
- **Engagement Tracking**: Activity measurement
- **Retention Analysis**: User retention rates
- **Demographic Insights**: User population data
- **Behavior Patterns**: User journey mapping

#### Property Analytics
- **Listing Performance**: View and inquiry metrics
- **Search Analytics**: What users are searching for
- **Interaction Data**: How users engage with properties
- **Conversion Tracking**: Inquiry to visit conversion
- **Comparative Analysis**: Performance benchmarking

#### Financial Analytics
- **Revenue Tracking**: Income from all sources
- **Subscription Performance**: Plan popularity and revenue
- **Payment Analysis**: Transaction metrics
- **Franchisee Revenue**: District-level financial data
- **Forecast Models**: Predictive financial trends

#### System Analytics
- **Platform Usage**: Overall system utilization
- **Performance Metrics**: System response times
- **Error Tracking**: Issue identification and logging
- **User Flow Analysis**: Navigation patterns
- **Feature Adoption**: Usage of specific features

## System Architecture

NearProp follows a modern, layered architecture that adheres to best practices for enterprise Java applications. The system is designed to be scalable, maintainable, and secure.

### Architectural Layers

#### 1. Presentation Layer
The presentation layer is composed of REST API controllers that handle HTTP requests and responses. This layer is responsible for:
- Request validation
- Authentication and authorization checks
- Response formatting
- Exception handling
- WebSocket endpoint management for real-time communication

Key components:
- `com.nearprop.controller.*`: REST API controllers
- `com.nearprop.exception.GlobalExceptionHandler`: Centralized exception handling

#### 2. Business Logic Layer
This layer contains the core business logic of the application:
- Service components implementing business rules
- Transaction management
- Integration with external services
- Data validation and processing

Key components:
- `com.nearprop.service.*`: Service interfaces and implementations
- `com.nearprop.mapper.*`: DTO to entity mapping

#### 3. Data Access Layer
The data access layer provides interfaces to persistent storage:
- JPA repositories for database operations
- Custom queries for complex data retrieval
- Entity definitions with relationships

Key components:
- `com.nearprop.repository.*`: Spring Data JPA repositories
- `com.nearprop.entity.*`: JPA entity classes

#### 4. Common/Utility Layer
Shared functionality used across the application:
- Utility classes
- Constants
- Common DTOs
- Helper methods

Key components:
- `com.nearprop.util.*`: Utility classes
- `com.nearprop.dto.*`: Data Transfer Objects

### Key Architectural Patterns

#### Repository Pattern
The application uses the repository pattern to abstract data access and provide a clean interface for working with domain entities. Spring Data JPA repositories provide CRUD operations and custom query methods.

#### Service Layer Pattern
Business logic is encapsulated in service classes that implement specific interfaces, promoting loose coupling, testability, and separation of concerns.

#### DTO (Data Transfer Object) Pattern
DTOs are used to transfer data between the client and server, decoupling the internal entity structure from the external API representation.

#### Dependency Injection
Spring's dependency injection is used throughout the application to promote loose coupling and testability of components.

### Cross-Cutting Concerns

#### Security
- Authentication filter for JWT token validation
- Method-level security with Spring Security annotations
- Role-based access control

#### Logging
- Structured logging with SLF4J and Logback
- Different log levels for development and production
- Request/response logging

#### Configuration
- Externalized configuration using properties files
- Environment-specific settings
- Secrets management

#### Exception Handling
- Centralized exception handling with global exception handler
- Custom exceptions for different scenarios
- Consistent error responses

## Technology Stack

NearProp leverages a modern technology stack for robust, scalable operations:

### Core Framework
- **Java 17**: Latest LTS version with enhanced language features
- **Spring Boot 3.x**: Modern application framework with auto-configuration
- **Spring MVC**: Web framework for building RESTful services
- **Spring Data JPA**: Simplified data access layer with repository abstraction

### Security
- **Spring Security**: Authentication and authorization framework
- **JWT (JSON Web Tokens)**: Stateless authentication mechanism
- **BCrypt**: Secure password hashing algorithm

### Database & Storage
- **PostgreSQL 12+**: High-performance, feature-rich relational database
- **Flyway**: Database migration and version control
- **AWS S3**: Scalable cloud storage for media files
- **Hibernate**: Object-relational mapping framework

### Real-time Communication
- **WebSocket**: Protocol for real-time, bidirectional communication
- **STOMP**: Simple Text Oriented Messaging Protocol over WebSockets
- **SockJS**: WebSocket emulation for browsers without native support

### Integration
- **Razorpay SDK**: Payment gateway integration
- **AWS SDK**: Amazon Web Services integration
- **Google Maps API**: Geolocation and mapping services
- **Spring Mail**: Email integration
- **Twilio API**: SMS services (optional)

### Build & Deployment
- **Maven**: Dependency management and build automation
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework for unit tests
- **Docker**: Container platform (optional)

### Monitoring & Logging
- **SLF4J**: Logging facade
- **Logback**: Logging implementation
- **Spring Actuator**: Application health monitoring and metrics

## Project Structure

NearProp follows a standard Spring Boot project structure with modular organization:

```
src/main/java/com/nearprop/
├── NearPropApplication.java            # Main application class
├── advertisement/                      # Advertisement module
│   ├── controller/                     # Advertisement controllers
│   ├── dto/                            # Advertisement DTOs
│   ├── entity/                         # Advertisement entities
│   ├── mapper/                         # Advertisement mappers
│   ├── repository/                     # Advertisement repositories
│   └── service/                        # Advertisement services
├── config/                             # Application configuration
│   ├── AwsConfig.java                  # AWS S3 configuration
│   ├── EmailConfig.java                # Email configuration
│   ├── JwtConfig.java                  # JWT configuration
│   ├── SecurityConfig.java             # Security configuration
│   └── WebSocketConfig.java            # WebSocket configuration
├── controller/                         # API controllers
│   ├── admin/                          # Admin-specific controllers
│   ├── analytics/                      # Analytics controllers
│   ├── franchisee/                     # Franchisee controllers
│   ├── AuthController.java             # Authentication controller
│   ├── ChatController.java             # Chat controller
│   ├── PaymentController.java          # Payment controller
│   ├── PropertyController.java         # Property controller
│   └── UserController.java             # User controller
├── dto/                                # Data Transfer Objects
│   ├── admin/                          # Admin DTOs
│   ├── analytics/                      # Analytics DTOs
│   ├── auth/                           # Authentication DTOs
│   ├── chat/                           # Chat DTOs
│   ├── franchisee/                     # Franchisee DTOs
│   ├── payment/                        # Payment DTOs
│   └── role/                           # Role DTOs
├── entity/                             # JPA entities
│   ├── User.java                       # User entity
│   ├── Property.java                   # Property entity
│   ├── Subscription.java               # Subscription entity
│   └── District.java                   # District entity
├── exception/                          # Exception handling
│   ├── GlobalExceptionHandler.java     # Central exception handler
│   ├── ResourceNotFoundException.java   # Not found exception
│   └── UnauthorizedException.java      # Auth exception
├── geolocation/                        # Geolocation services
│   ├── controller/                     # Geo controllers
│   ├── service/                        # Geo services
│   └── util/                           # Geo utilities
├── mapper/                             # Object mappers
├── model/                              # Additional models
├── repository/                         # Data repositories
│   ├── franchisee/                     # Franchisee repositories
│   ├── UserRepository.java             # User repository
│   ├── PropertyRepository.java         # Property repository
│   └── SubscriptionRepository.java     # Subscription repository
├── security/                           # Security components
│   ├── JwtAuthenticationFilter.java    # JWT filter
│   ├── JwtUtil.java                    # JWT utility
│   └── UserPrincipal.java              # User security details
├── service/                            # Business services
│   ├── admin/                          # Admin services
│   │   └── impl/                       # Admin implementations
│   ├── analytics/                      # Analytics services
│   │   └── impl/                       # Analytics implementations
│   ├── franchisee/                     # Franchisee services
│   │   └── impl/                       # Franchisee implementations
│   ├── impl/                           # Service implementations
│   ├── AuthService.java                # Auth service interface
│   ├── PropertyService.java            # Property service interface
│   └── UserService.java                # User service interface
└── util/                               # Utilities and helpers
```

### Resources Structure

```
src/main/resources/
├── application.properties              # Main configuration
├── application-secrets.properties      # Sensitive configuration
├── db/                                 # Database migrations
│   └── migration/                      # Flyway migrations
├── messages/                           # Localization
│   ├── general_en.properties           # English messages
│   └── general_hi.properties           # Hindi messages
└── templates/                          # Email templates
    └── email/                          # Email HTML templates
```

## API Documentation

NearProp provides comprehensive API documentation through Postman collections. These collections are organized by functional area and can be imported into Postman for testing.

### Available Postman Collections

1. **NearProp_New_APIs_Collection.json**: Core API endpoints
2. **Advertisement.postman_collection.json**: Advertisement management
3. **subscription_management.postman_collection.json**: Subscription endpoints
4. **updated_NearProp.postman_collection.json**: Complete API collection

### Core API Endpoints

#### Authentication APIs
```
POST /api/v1/auth/register          # Register a new user
POST /api/v1/auth/login             # Login with phone number
POST /api/v1/auth/verify-otp        # Verify OTP code
POST /api/v1/auth/logout            # Logout user
```

#### Property APIs
```
GET    /api/properties              # List properties with filters
POST   /api/properties              # Create a property
GET    /api/properties/{id}         # Get property by ID
PUT    /api/properties/{id}         # Update property
DELETE /api/properties/{id}         # Delete property
POST   /api/properties/{id}/images  # Upload property images
```

#### Franchisee APIs
```
GET    /api/franchisee/districts            # List franchisee districts
POST   /api/franchisee/districts            # Create a district
GET    /api/franchisee/withdrawals          # List withdrawal requests
POST   /api/franchisee/withdrawals          # Create withdrawal request
GET    /api/franchisee/revenue              # Get revenue statistics
```

#### Subscription APIs
```
GET    /api/subscriptions/plans             # List subscription plans
POST   /api/subscriptions                   # Create subscription
GET    /api/subscriptions                   # List user subscriptions
GET    /api/subscriptions/{id}              # Get subscription details
PUT    /api/subscriptions/{id}/cancel       # Cancel subscription
```

#### Payment APIs
```
POST   /api/payments/initiate               # Initiate payment
POST   /api/payments/verify                 # Verify payment
GET    /api/payments/history                # Get payment history
```

#### Chat APIs
```
GET    /api/chat/rooms                      # List chat rooms
POST   /api/chat/rooms                      # Create chat room
GET    /api/chat/rooms/{id}/messages        # Get messages
POST   /api/chat/rooms/{id}/messages        # Send message
```

#### Advertisement APIs
```
GET    /api/v1/advertisements               # List advertisements
POST   /api/v1/advertisements               # Create advertisement
GET    /api/v1/advertisements/analytics     # Get analytics
```

### API Response Format

All API endpoints follow a consistent response format:

```json
{
  "status": "success",
  "message": "Operation completed successfully",
  "data": {
    // Response data here
  },
  "timestamp": "2023-06-16T10:15:30Z"
}
```

Error responses follow this format:

```json
{
  "status": "error",
  "message": "Error message describing what went wrong",
  "code": "ERROR_CODE",
  "timestamp": "2023-06-16T10:15:30Z"
}
```

## Installation Guide

This section provides step-by-step instructions for setting up NearProp on your development or production environment.

### Prerequisites

- JDK 17 or higher
- PostgreSQL 12 or higher
- Maven 3.8+ or Gradle
- Git
- AWS Account (for S3 storage features)
- Razorpay Account (for payment processing)

### Development Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/Sandy-7061/NearProp.git
cd NearProp
```

#### 2. Configure Database

Create a PostgreSQL database and user:

```bash
psql -U postgres -c "CREATE DATABASE nearprop_db"
psql -U postgres -c "CREATE USER nearprop_user WITH PASSWORD 'your_password'"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE nearprop_db TO nearprop_user"
```

#### 3. Configure Application Properties

Update `src/main/resources/application.properties` with your database details:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/nearprop_db
spring.datasource.username=nearprop_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

#### 4. Configure External Services

Update `application-secrets.yml` with your service credentials:

```yaml
# AWS Configuration
aws:
  accessKey: YOUR_AWS_ACCESS_KEY
  secretKey: YOUR_AWS_SECRET_KEY
  region: YOUR_AWS_REGION
  
# Razorpay Configuration
payment:
  gateway:
    razorpay:
      key: YOUR_RAZORPAY_KEY
      secret: YOUR_RAZORPAY_SECRET
```

#### 5. Build the Application

```bash
./mvnw clean install
```

#### 6. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on port 8080 (or the port specified in your configuration).

### Production Deployment

For production deployment, additional steps are recommended:

#### 1. Set Production Profile

```bash
export SPRING_PROFILES_ACTIVE=prod
```

#### 2. Configure HTTPS

Update your application properties for HTTPS:

```properties
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your-keystore-password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

#### 3. Database Connection Pooling

Configure connection pooling for production:

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=30000
```

#### 4. Memory Configuration

Optimize JVM settings:

```bash
java -Xms512m -Xmx2048m -jar nearprop.jar
```

#### 5. Monitoring Setup

Enable Spring Boot Actuator endpoints for monitoring:

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

## Configuration

NearProp offers extensive configuration options to customize the application for different environments.

### Core Configuration

The primary configuration is stored in `application.properties`:

```properties
# Server Configuration
server.port=${PORT:8080}
server.servlet.context-path=/api
server.error.include-message=always
server.error.include-binding-errors=always

# Database Configuration
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/nearprop_db}
spring.datasource.username=${DB_USERNAME:nearprop_user}
spring.datasource.password=${DB_PASSWORD:your_password}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.open-in-view=false

# File Upload Configuration
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=${FILE_UPLOAD_DIR:./uploads}
```

### Secrets Configuration

Sensitive configuration is stored in `application-secrets.yml`:

```yaml
# Email Configuration
spring:
  mail:
    host: ${SMTP_HOST}
    port: ${SMTP_PORT}
    username: ${SMTP_USERNAME}
    password: ${SMTP_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

# JWT Configuration
jwt:
  secret: ${JWT_SECRET}
  expiration:
    token: 86400000  # 24 hours
    user: 3153600000000  # 100 years
    other: 604800000  # 7 days

# AWS Configuration
aws:
  accessKey: ${AWS_ACCESS_KEY}
  secretKey: ${AWS_SECRET_KEY}
  region: ${AWS_REGION}
  s3:
    bucket: ${AWS_S3_BUCKET}

# Razorpay Configuration
payment:
  gateway:
    razorpay:
      key: ${RAZORPAY_KEY}
      secret: ${RAZORPAY_SECRET}
      api:
        url: ${RAZORPAY_API_URL:https://api.razorpay.com/v1}
```

### Environment-specific Configuration

You can create environment-specific profiles like `application-dev.properties` and `application-prod.properties` to override settings for different environments.

### Property Hierarchy

NearProp uses Spring Boot's property resolution order:

1. Command line arguments
2. System environment variables
3. Application properties (from .yml or .properties files)
4. Default properties defined in the code

### Logging Configuration

Logging is configured via `logback-spring.xml`:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/app.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
    
    <logger name="com.nearprop" level="DEBUG" />
</configuration>
```

### Flyway Database Migration

Database migrations are managed with Flyway:

```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true
```

Migration scripts are located in `src/main/resources/db/migration/` and follow the naming pattern `V{version}__{description}.sql`.

## Security Features

### Authentication System

NearProp implements a robust authentication system:

1. **Phone OTP Authentication**: 
   - Users register with phone number
   - OTP is sent to verify phone number
   - OTP verification completes authentication

2. **JWT Token Generation**:
   - Tokens include userId, roles, and sessionId
   - Different expiration times based on user roles:
     - Non-expiring tokens for regular users (ROLE_USER only)
     - 7-day tokens for other roles (SELLER, ADVISOR, etc.)

3. **Session Management**:
   - Each login creates a new session
   - Sessions are tracked in the database
   - Single-device enforcement for regular users
   - Session verification on token validation

### Authorization Controls

1. **Role-Based Access Control (RBAC)**:
   - Endpoint-level authorization using Spring Security
   - Method-level authorization with @PreAuthorize
   - API access based on assigned roles
   - Hierarchical permission structure

2. **Resource Ownership Verification**:
   - Property owners can only update their own properties
   - Franchisees can only manage districts assigned to them
   - Sellers can only manage their own listings

### Data Security

1. **Password Encryption**:
   - BCrypt password hashing
   - No plaintext password storage

2. **HTTPS Communication**:
   - Secure data transmission
   - TLS for API requests

3. **Sensitive Data Handling**:
   - JWT secrets secured in application-secrets.yml
   - API keys stored securely
   - Logs sanitized of sensitive information

### API Security

1. **CSRF Protection**:
   - Cross-Site Request Forgery prevention

2. **XSS Prevention**:
   - Input validation to prevent script injection
   - Proper output encoding

3. **Rate Limiting**:
   - Protection against brute force attacks
   - API request throttling

4. **Input Validation**:
   - All API inputs are validated
   - Data type and format verification
   - Constraint validation

## User Roles and Permissions

NearProp implements a comprehensive role-based access control system with the following roles:

### ROLE_USER
The base role assigned to all registered users.

**Permissions**:
- Browse properties
- Contact property sellers
- Schedule property visits
- Save favorite properties
- Submit reviews and ratings
- View and interact with reels
- Participate in chats

### ROLE_SELLER
For users who want to list properties for sale or rent.

**Permissions**:
- All USER permissions
- Create and manage property listings
- Upload property images
- Create property reels
- Manage property visits
- View seller analytics
- Respond to reviews

### ROLE_ADVISOR
For real estate advisors and agents.

**Permissions**:
- All SELLER permissions
- Create multiple property listings
- Enhanced property management features
- Access to advisor-specific analytics
- Create branded content

### ROLE_DEVELOPER
For real estate developers with multiple units.

**Permissions**:
- All ADVISOR permissions
- Manage building projects with multiple units
- Track unit inventory and sales
- Access to developer analytics dashboard
- Bulk property management tools

### ROLE_FRANCHISEE
For district-level franchisees managing operations in specific areas.

**Permissions**:
- Manage assigned districts
- View district-level analytics
- Track district revenue
- Add properties in their districts
- Process withdrawal requests
- View franchisee dashboard

### ROLE_ADMIN
For system administrators with full access.

**Permissions**:
- Complete system access
- User management
- Role approval
- Property approval
- Subscription management
- System configuration
- Analytics and reporting 

## Development Guidelines

### Code Quality
- **Static Code Analysis**: Use tools like Checkstyle, FindBugs, and PMD
- **Unit Testing**: Implement JUnit tests for all business logic
- **Integration Testing**: Use tools like Selenium for UI testing
- **Code Reviews**: Regular peer reviews

### Documentation
- **API Documentation**: Use tools like Swagger or Postman
- **User Manual**: Create a comprehensive user guide
- **Developer Guide**: Document project architecture and development process

## Testing Guidelines

### Unit Testing
- **JUnit**: Use for individual component testing
- **Mockito**: Mock objects for dependency injection

### Integration Testing
- **Selenium**: Web UI testing
- **Postman**: API testing

### Acceptance Testing
- **Cucumber**: BDD testing for user stories

## Deployment

### Deployment Strategy
- **Containerization**: Use Docker for containerized deployment
- **CI/CD Pipeline**: Implement Jenkins or GitHub Actions for automated deployment

### Monitoring and Logging
- **Prometheus**: Metrics collection
- **Grafana**: Visualization and alerting
- **ELK Stack**: Log aggregation and analysis

## Troubleshooting

### Common Issues
- **Database Connection**: Check database status and logs
- **External Service**: Verify connectivity and logs
- **Application Crash**: Use tools like JFR for profiling

### Debugging Techniques
- **Console Logging**: Use System.out.println for debugging
- **Remote Debugging**: Use tools like VisualVM or IntelliJ IDEA

## License

NearProp is a proprietary software developed by the company. All rights reserved. 