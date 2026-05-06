# NearProp Property Update Approval System Guide

This guide explains the property update approval system in NearProp, where property owners can submit update requests that require approval from admins and/or district franchisees before changes are applied.

## Overview

The property update approval system allows:

1. Property owners to submit update requests for their properties
2. Franchisees to submit update requests on behalf of property owners
3. Admin and district franchisees to review and approve/reject update requests
4. Tracking of both old and new values for each field being updated

## Key Features

- Update requests can modify one field, multiple fields, or all fields
- System tracks both old and new values for each field
- Admins can approve or reject changes with comments
- District franchisees can approve or reject changes for properties in their district
- Owners can see rejection reasons and resubmit changes
- First reviewer (admin or franchisee) to act on a request determines the outcome

## API Endpoints

### Property Owner Endpoints

#### Submit a property update request

```
POST /api/property-updates
Content-Type: multipart/form-data
```

Parameters:
- `propertyId`: ID of the property to update (required)
- `requestNotes`: Notes about the update request (optional)
- Various property fields to update (title, description, price, etc.)
- `images`: New property images (optional)
- `video`: New property video (optional)

#### Get my update requests

```
GET /api/property-updates/my-requests
```

Returns all update requests created by the current user.

#### Get a specific update request

```
GET /api/property-updates/{requestId}
```

#### Get a specific update request by permanent ID

```
GET /api/property-updates/by-permanent-id/{requestId}
```

#### Cancel an update request

```
POST /api/property-updates/{requestId}/cancel
```

### Franchisee Endpoints

#### Submit a property update request on behalf of an owner

```
POST /api/franchisee/property-updates
Content-Type: multipart/form-data
```

Parameters:
- `ownerPermanentId`: Permanent ID of the property owner (required)
- `propertyId`: ID of the property to update (required)
- `requestNotes`: Notes about the update request (optional)
- Various property fields to update (title, description, price, etc.)
- `images`: New property images (optional)
- `video`: New property video (optional)

#### Get update requests created by the franchisee

```
GET /api/franchisee/property-updates/my-requests
```

#### Get pending update requests in the franchisee's district

```
GET /api/franchisee/property-updates/district/pending
```

#### Get pending update requests that need franchisee review

```
GET /api/franchisee/property-updates/district/pending-review
```

#### Review an update request (franchisee)

```
POST /api/franchisee/property-updates/review
Content-Type: application/json
```

Request body:
```json
{
  "requestId": 123,
  "approved": true,
  "reviewerType": "FRANCHISEE",
  "notes": "Looks good, approved",
  "rejectionReason": null
}
```

#### Cancel an update request

```
POST /api/franchisee/property-updates/{requestId}/cancel
```

### Admin Endpoints

#### Get all pending update requests

```
GET /api/property-updates/admin/pending
```

#### Get pending update requests that need admin review

```
GET /api/property-updates/admin/pending-review
```

#### Review an update request (admin)

```
POST /api/property-updates/admin/review
Content-Type: application/json
```

Request body:
```json
{
  "requestId": 123,
  "approved": true,
  "reviewerType": "ADMIN",
  "notes": "Approved from admin side",
  "rejectionReason": null
}
```

## Request Status Flow

1. **PENDING**: Initial state when the request is created
2. **APPROVED**: When the request is approved by both admin and franchisee (if applicable)
3. **REJECTED**: When the request is rejected by either admin or franchisee
4. **CANCELLED**: When the request is cancelled by the owner or franchisee

## Approval Rules

- For properties in a district with a franchisee:
  - Either admin OR franchisee approval is sufficient
  - The first one to review (approve/reject) determines the outcome
  - Once reviewed by one party, the request is considered processed
  
- For properties without a district franchisee:
  - Only admin approval is required

## Example Usage Scenarios

### Scenario 1: Property Owner Updates Their Property

1. Owner submits update request with new title and price
2. Request is visible to both admin and district franchisee
3. Admin approves the request
4. District franchisee approves the request
5. Property is updated with the new values

### Scenario 2: Franchisee Submits Update on Behalf of Owner

1. Franchisee submits update request for an owner's property
2. Request is visible to admin
3. Admin approves or rejects the request
4. If approved, property is updated with the new values

### Scenario 3: Rejection Handling

1. Owner submits update request
2. Admin or franchisee rejects the request with a reason
3. Owner sees the rejection reason
4. Owner can submit a new update request with corrections

## Response Format

All endpoints return data in the following format:

```json
{
  "id": 123,
  "requestId": "RANPUR20250611001",
  "propertyId": 456,
  "propertyTitle": "3BHK Apartment in Indore",
  "propertyPermanentId": "PROP20250101001",
  "requestedBy": {
    "id": 789,
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "9876543210",
    "permanentId": "USER20250101001",
    "profileImageUrl": "https://example.com/profile.jpg",
    "roles": ["USER", "SELLER"]
  },
  "reviewedByAdmin": {
    "id": 101,
    "name": "Admin User",
    "email": "admin@example.com",
    "phone": "9876543211",
    "permanentId": "ADMIN20250101001",
    "profileImageUrl": "https://example.com/admin.jpg",
    "roles": ["ADMIN"]
  },
  "reviewedByFranchisee": {
    "id": 102,
    "name": "Franchisee User",
    "email": "franchisee@example.com",
    "phone": "9876543212",
    "permanentId": "FRAN20250101001",
    "profileImageUrl": "https://example.com/franchisee.jpg",
    "roles": ["FRANCHISEE"]
  },
  "status": "APPROVED",
  "requestNotes": "Updating price and title",
  "adminNotes": "Approved from admin side",
  "franchiseeNotes": "Approved from franchisee side",
  "rejectionReason": null,
  "district": "Indore",
  "adminReviewed": true,
  "franchiseeReviewed": true,
  "adminApproved": true,
  "franchiseeApproved": true,
  "oldValues": {
    "title": "2BHK Apartment in Indore",
    "price": "5000000"
  },
  "newValues": {
    "title": "3BHK Apartment in Indore",
    "price": "7000000"
  },
  "submittedAt": "2025-06-11T10:30:00",
  "adminReviewedAt": "2025-06-11T11:15:00",
  "franchiseeReviewedAt": "2025-06-11T12:00:00",
  "updatedAt": "2025-06-11T12:00:00",
  "franchiseeRequest": false,
  "franchisee": null
}
```
