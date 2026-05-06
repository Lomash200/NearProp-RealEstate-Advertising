# NearProp API Documentation

This document provides information about the new APIs added to the NearProp application.

## Table of Contents

1. [Mock Monthly Report Generation API](#mock-monthly-report-generation-api)
2. [Franchisee Dashboard API](#franchisee-dashboard-api)
3. [Child Safety Standards API](#child-safety-standards-api)

## Mock Monthly Report Generation API

These endpoints allow generating mock monthly revenue report data that will be stored in the same database structure used by the existing monthly report APIs.

### Generate Mock Report for a Specific Franchisee

**Endpoint:** `POST /api/mock-reports/franchisee/{franchiseeId}`

**Authorization:** Admin role required

**Path Parameters:**
- `franchiseeId` - ID of the franchisee

**Query Parameters:**
- `year` (optional) - Year for the report (defaults to current year if not provided)
- `month` (optional) - Month for the report (defaults to current month if not provided)

**Response:**
```json
{
  "success": true,
  "message": "Mock report generated successfully",
  "data": {
    "id": 123,
    "year": 2023,
    "month": 5,
    "generatedAt": "2023-05-15T10:30:45",
    "reportStatus": "PENDING",
    "franchiseeId": 456,
    "franchiseeName": "John Doe",
    "businessName": "Business 456",
    "districtId": 789,
    "districtName": "Central District",
    "state": "Karnataka",
    "totalListings": 120,
    "activeListings": 85,
    "soldListings": 25,
    "pendingListings": 10,
    "totalSubscriptions": 45,
    "activeSubscriptions": 38,
    "expiredSubscriptions": 7,
    "totalRevenue": 75000.00,
    "franchiseeRevenue": 52500.00,
    "platformRevenue": 22500.00,
    "walletBalance": 30000.00,
    "pendingWithdrawal": 5000.00
  }
}
```

### Generate Mock Reports for All Franchisees

**Endpoint:** `POST /api/mock-reports/all-franchisees`

**Authorization:** Admin role required

**Query Parameters:**
- `year` (optional) - Year for the reports (defaults to current year if not provided)
- `month` (optional) - Month for the reports (defaults to current month if not provided)

**Response:**
```json
{
  "success": true,
  "message": "Mock reports generated successfully for all franchisees",
  "data": [
    {
      "id": 123,
      "year": 2023,
      "month": 5,
      "franchiseeId": 456,
      "franchiseeName": "John Doe",
      "districtId": 789,
      "districtName": "Central District",
      "state": "Karnataka",
      "totalRevenue": 75000.00,
      "franchiseeRevenue": 52500.00,
      "platformRevenue": 22500.00
      // other fields omitted for brevity
    },
    {
      "id": 124,
      "year": 2023,
      "month": 5,
      "franchiseeId": 457,
      "franchiseeName": "Jane Smith",
      // other fields omitted for brevity
    }
  ]
}
```

### Generate Mock Report for a Specific District

**Endpoint:** `POST /api/mock-reports/district/{districtId}`

**Authorization:** Admin or Franchisee role required

**Path Parameters:**
- `districtId` - ID of the district

**Query Parameters:**
- `year` (optional) - Year for the report (defaults to current year if not provided)
- `month` (optional) - Month for the report (defaults to current month if not provided)

**Response:**
```json
{
  "success": true,
  "message": "Mock report generated successfully for district",
  "data": {
    "id": 123,
    "year": 2023,
    "month": 5,
    "generatedAt": "2023-05-15T10:30:45",
    "reportStatus": "PENDING",
    "franchiseeId": 456,
    "franchiseeName": "John Doe",
    "businessName": "Business 456",
    "districtId": 789,
    "districtName": "Central District",
    "state": "Karnataka",
    // other fields omitted for brevity
  }
}
```

### Generate Mock Reports for Franchisee's Districts

**Endpoint:** `POST /api/mock-reports/my-districts`

**Authorization:** Franchisee role required

**Query Parameters:**
- `year` (optional) - Year for the reports (defaults to current year if not provided)
- `month` (optional) - Month for the reports (defaults to current month if not provided)

**Response:**
```json
{
  "success": true,
  "message": "Mock reports generated successfully for your districts",
  "data": [
    {
      "id": 123,
      "year": 2023,
      "month": 5,
      "districtId": 789,
      "districtName": "Central District",
      "state": "Karnataka",
      // other fields omitted for brevity
    },
    {
      "id": 124,
      "year": 2023,
      "month": 5,
      "districtId": 790,
      "districtName": "North District",
      "state": "Karnataka",
      // other fields omitted for brevity
    }
  ]
}
```

### Generate Mock Reports for a Specific Franchisee's Districts (Admin Only)

**Endpoint:** `POST /api/mock-reports/franchisee/{franchiseeId}/districts`

**Authorization:** Admin role required

**Path Parameters:**
- `franchiseeId` - ID of the franchisee

**Query Parameters:**
- `year` (optional) - Year for the reports (defaults to current year if not provided)
- `month` (optional) - Month for the reports (defaults to current month if not provided)

**Response:**
```json
{
  "success": true,
  "message": "Mock reports generated successfully for franchisee districts",
  "data": [
    {
      "id": 123,
      "year": 2023,
      "month": 5,
      "districtId": 789,
      "districtName": "Central District",
      "state": "Karnataka",
      // other fields omitted for brevity
    },
    {
      "id": 124,
      "year": 2023,
      "month": 5,
      "districtId": 790,
      "districtName": "North District",
      "state": "Karnataka",
      // other fields omitted for brevity
    }
  ]
}
```

## Franchisee Dashboard API

These endpoints provide comprehensive dashboard data for franchisees.

### Get Franchisee Dashboard

**Endpoint:** `GET /api/franchisee/dashboard`

**Authorization:** Franchisee role required

**Query Parameters:**
- `startDate` (optional) - Start date for filtering data (ISO format: YYYY-MM-DD)
- `endDate` (optional) - End date for filtering data (ISO format: YYYY-MM-DD)

**Response:**
```json
{
  "success": true,
  "message": "Dashboard data retrieved successfully",
  "data": {
    "franchiseeId": 456,
    "franchiseeName": "John Doe",
    "businessName": "Business 456",
    "generatedAt": "2023-05-15T10:30:45",
    "districtIds": [789, 790],
    "totalListings": 120,
    "activeListings": 85,
    "soldListings": 25,
    "pendingListings": 10,
    "totalRevenue": 75000.00,
    "franchiseeRevenue": 52500.00,
    "platformRevenue": 22500.00,
    "walletBalance": 30000.00,
    "pendingWithdrawal": 5000.00,
    "totalSubscriptions": 45,
    "activeSubscriptions": 38,
    "expiredSubscriptions": 7,
    "listingTimeSeriesData": [
      {
        "timeUnit": "daily",
        "labels": ["2023-05-01", "2023-05-02", "2023-05-03"],
        "series": {
          "active": [80, 82, 85],
          "sold": [22, 23, 25],
          "pending": [10, 10, 10]
        }
      }
    ],
    "revenueTimeSeriesData": [
      // time series data for revenue
    ],
    "subscriptionTimeSeriesData": [
      // time series data for subscriptions
    ],
    "recentActivity": [
      {
        "userId": 101,
        "userName": "User 1",
        "action": "viewed",
        "timestamp": "2023-05-15T09:30:00",
        "propertyId": "PROP123",
        "propertyTitle": "3BHK Apartment"
      }
    ],
    "districtPerformance": [
      {
        "districtId": 789,
        "districtName": "Central District",
        "state": "Karnataka",
        "totalListings": 70,
        "activeListings": 50,
        "soldListings": 15,
        "pendingListings": 5,
        "totalRevenue": 45000.00,
        "franchiseeRevenue": 31500.00,
        "platformRevenue": 13500.00,
        // other metrics omitted for brevity
      }
    ],
    "activeSubscriptionUsers": [
      {
        "userId": 101,
        "name": "User 1",
        "email": "user1@example.com",
        "phone": "9991234567",
        "subscriptionId": 12345,
        "subscriptionPlan": "Premium",
        "startDate": "2023-04-01",
        "endDate": "2023-07-01",
        "status": "ACTIVE",
        "amount": 2999.00,
        // other fields omitted for brevity
      }
    ],
    "listingData": {
      "totalListings": 120,
      "activeListings": 85,
      "soldListings": 25,
      "pendingListings": 10,
      "featuredListings": 15,
      "premiumListings": 20,
      "newListingsThisMonth": 30,
      "soldListingsThisMonth": 12
    },
    "revenueData": {
      "totalRevenue": 75000.00,
      "franchiseeRevenue": 52500.00,
      "platformRevenue": 22500.00,
      "walletBalance": 30000.00,
      "pendingWithdrawal": 5000.00,
      "lastWithdrawalDate": "2023-05-01",
      "lastWithdrawalAmount": 20000.00
    },
    "subscriptionData": {
      "totalSubscriptions": 45,
      "activeSubscriptions": 38,
      "expiredSubscriptions": 7,
      "newSubscriptionsThisMonth": 15,
      "renewalsThisMonth": 8,
      "averageSubscriptionValue": 1666.67,
      "totalSubscriptionRevenue": 75000.00
    }
  }
}
```

### Get District Performance for Franchisee

**Endpoint:** `GET /api/franchisee/dashboard/districts`

**Authorization:** Franchisee role required

**Query Parameters:**
- `startDate` (optional) - Start date for filtering data (ISO format: YYYY-MM-DD)
- `endDate` (optional) - End date for filtering data (ISO format: YYYY-MM-DD)

**Response:**
```json
{
  "success": true,
  "message": "District performance data retrieved successfully",
  "data": [
    {
      "districtId": 789,
      "districtName": "Central District",
      "state": "Karnataka",
      "totalListings": 70,
      "activeListings": 50,
      "soldListings": 15,
      "pendingListings": 5,
      "featuredListings": 10,
      "totalRevenue": 45000.00,
      "franchiseeRevenue": 31500.00,
      "platformRevenue": 13500.00,
      "totalSubscriptions": 25,
      "activeSubscriptions": 22,
      "expiredSubscriptions": 3,
      "listingTimeSeriesData": [
        // time series data for listings
      ],
      "revenueTimeSeriesData": [
        // time series data for revenue
      ],
      "subscriptionTimeSeriesData": [
        // time series data for subscriptions
      ],
      "totalUsers": 100,
      "activeUsers": 80,
      "newUsersThisMonth": 15,
      "revenueRank": 1,
      "listingsRank": 2,
      "subscriptionsRank": 1,
      "revenueGrowth": 15.5,
      "listingsGrowth": 8.3,
      "subscriptionsGrowth": 12.0
    }
  ]
}
```

### Get Single District Performance

**Endpoint:** `GET /api/franchisee/dashboard/districts/{districtId}`

**Authorization:** Franchisee role required

**Path Parameters:**
- `districtId` - ID of the district

**Query Parameters:**
- `startDate` (optional) - Start date for filtering data (ISO format: YYYY-MM-DD)
- `endDate` (optional) - End date for filtering data (ISO format: YYYY-MM-DD)

**Response:**
```json
{
  "success": true,
  "message": "District performance data retrieved successfully",
  "data": {
    "districtId": 789,
    "districtName": "Central District",
    "state": "Karnataka",
    "totalListings": 70,
    "activeListings": 50,
    "soldListings": 15,
    "pendingListings": 5,
    "featuredListings": 10,
    "totalRevenue": 45000.00,
    "franchiseeRevenue": 31500.00,
    "platformRevenue": 13500.00,
    "totalSubscriptions": 25,
    "activeSubscriptions": 22,
    "expiredSubscriptions": 3,
    // other fields omitted for brevity
  }
}
```

### Admin Endpoints

The following endpoints are available for admin users:

- `GET /api/franchisee/dashboard/admin/franchisee/{franchiseeId}` - Get dashboard data for a specific franchisee
- `GET /api/franchisee/dashboard/admin/franchisee/{franchiseeId}/districts` - Get district performance data for a specific franchisee
- `GET /api/franchisee/dashboard/admin/districts/{districtId}` - Get performance data for a specific district

These endpoints have the same response structure as their franchisee counterparts but require admin role authorization.

## Child Safety Standards API

This public API provides information about NearProp's child safety and CSAE standards.

### Get Safety Standards (Plain Text)

**Endpoint:** `GET /api/safety-standards`

**Authorization:** None required (public API)

**Response Content Type:** `text/plain`

**Response:**
```
NearProp Child Safety and CSAE Standards

At NearProp, we are committed to providing a safe environment for all users, with special attention to child safety and protection against Child Sexual Abuse and Exploitation (CSAE).

Our Commitments:

1. Age Restrictions: NearProp services are designed for adults aged 18 and above. We do not knowingly collect or solicit personal information from anyone under 18.

2. Content Monitoring: We employ both automated systems and human review to detect and remove any inappropriate content that may violate our policies.

3. Reporting Mechanisms: We provide clear and accessible ways for users to report concerning content or behavior.

4. Swift Action: We promptly investigate all reports and take appropriate action, including content removal and account termination when necessary.

5. Cooperation with Authorities: We cooperate fully with law enforcement agencies in investigations related to child safety and CSAE.

6. Regular Policy Updates: We continuously review and update our safety policies to address emerging threats and improve protection measures.

7. User Education: We provide resources to educate users about online safety.

For more information or to report concerns, please contact safety@nearprop.com

Last Updated: 2023-05-15
```

### Get Safety Standards (JSON)

**Endpoint:** `GET /api/safety-standards`

**Authorization:** None required (public API)

**Response Content Type:** `application/json`

**Response:**
```json
{
  "success": true,
  "message": "Safety standards retrieved successfully",
  "data": "NearProp Child Safety and CSAE Standards\n\nAt NearProp, we are committed to providing a safe environment for all users, with special attention to child safety and protection against Child Sexual Abuse and Exploitation (CSAE).\n\nOur Commitments:\n\n1. Age Restrictions: NearProp services are designed for adults aged 18 and above. We do not knowingly collect or solicit personal information from anyone under 18.\n\n2. Content Monitoring: We employ both automated systems and human review to detect and remove any inappropriate content that may violate our policies.\n\n3. Reporting Mechanisms: We provide clear and accessible ways for users to report concerning content or behavior.\n\n4. Swift Action: We promptly investigate all reports and take appropriate action, including content removal and account termination when necessary.\n\n5. Cooperation with Authorities: We cooperate fully with law enforcement agencies in investigations related to child safety and CSAE.\n\n6. Regular Policy Updates: We continuously review and update our safety policies to address emerging threats and improve protection measures.\n\n7. User Education: We provide resources to educate users about online safety.\n\nFor more information or to report concerns, please contact safety@nearprop.com\n\nLast Updated: 2023-05-15"
}
``` 