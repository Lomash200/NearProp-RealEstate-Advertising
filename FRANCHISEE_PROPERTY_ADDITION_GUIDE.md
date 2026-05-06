# Franchisee Property Addition Feature Guide

This document explains how franchisees can add properties on behalf of other users (Sellers, Property Advisors, and Developers) in the NearProp system.

## Overview

Franchisees can now add properties on behalf of sellers, property advisors, and developers. This feature allows franchisees to assist property owners who may not be tech-savvy or have limited access to the platform. The property will be owned by the original user, but will be marked as "added by franchisee" for tracking purposes.

## Permanent User IDs

All users in the system now have a permanent ID that follows this format:

- Regular Users: `RANPU(Year)(month)(date)1,2,3,4,...`
- Sellers: `RANPS(Year)(month)(date)1,2,3,4,...`
- Property Advisors: `RANPPA(Year)(month)(date)1,2,3,4,...`
- Developers: `RANPD(Year)(month)(date)1,2,3,4,...`

For example: `RANPS202506101` for a seller registered on June 10, 2025 (first seller of the day).

These permanent IDs are used to identify users across the system and are included in API responses.

## Permanent Property IDs

All properties in the system now have a permanent ID that follows this format:

- Properties: `RANP(Year)(month)(date)1,2,3,4,...`

For example: `RANP202506101` for a property created on June 10, 2025 (first property of the day).

These permanent IDs are used to identify properties across the system and are included in API responses. You can use these IDs to look up properties directly using the dedicated endpoint.

## API Endpoints

### Validate User ID

```
GET /franchisee/properties/validate-id/{permanentId}
```

Validates if a user with the given permanent ID exists in the system.

**Response:**
- `true` if the user exists
- `false` if the user does not exist

### Add Property on Behalf of User (JSON)

```
POST /franchisee/properties
```

**Request Body:**
```json
{
  "ownerPermanentId": "RANPS202506101",
  "propertyDetails": {
    "title": "Property Title",
    "description": "Property Description",
    "type": "HOUSE",
    "status": "FOR_SALE",
    "price": 5000000,
    "area": 1200,
    "sizePostfix": "sq.ft.",
    "address": "123 Main Street",
    "districtId": 1,
    "city": "City Name",
    "state": "State Name",
    "pincode": "123456",
    "bedrooms": 3,
    "bathrooms": 2,
    "amenities": ["Parking", "Security"],
    "features": ["Garden", "Balcony"]
  }
}
```

### Add Property on Behalf of User (Form Data)

```
POST /franchisee/properties/form
```

**Form Data Parameters:**
- `ownerPermanentId`: The permanent ID of the property owner
- `propertyFormDto`: The property form data
- `images`: Property images (optional)
- `video`: Property video (optional)

### Get Property by Permanent ID

```
GET /properties/by-id/{permanentId}
```

Retrieves a property by its permanent ID.

## Property Response

When a property is added by a franchisee on behalf of another user, the property response will include the following additional fields:

```json
{
  "id": 123,
  "permanentId": "RANP202506101",
  "title": "Property Title",
  "ownerPermanentId": "RANPS202506101",
  "addedByUser": {
    "id": 456,
    "name": "Franchisee Name",
    "email": "franchisee@example.com",
    "phone": "+919876543210",
    "roles": ["FRANCHISEE"]
  },
  "addedByFranchisee": true,
  "owner": {
    "id": 789,
    "name": "Seller Name",
    "email": "seller@example.com",
    "phone": "+919876543211",
    "roles": ["SELLER"]
  }
}
```

## Testing

A test script is provided to test the franchisee property addition feature:

```bash
./test_franchisee_property_addition.sh
```

This script:
1. Registers a franchisee and a seller
2. Verifies OTP for both users
3. Validates the seller's permanent ID
4. Gets available districts
5. Adds a property on behalf of the seller
6. Verifies the property details

## Implementation Details

The following changes were made to implement this feature:

1. Added `permanentId` field to the `User` entity
2. Added `permanentId`, `ownerPermanentId`, `addedByUser`, and `addedByFranchisee` fields to the `Property` entity
3. Created the `IdGenerator` utility class to generate permanent IDs for users and properties
4. Updated the user and property creation processes to generate and assign permanent IDs
5. Created a new controller and service for franchisee property operations
6. Added database migration script to add the new fields
7. Added API endpoints to retrieve properties by permanent ID

## Security Considerations

- Only users with the `FRANCHISEE` role can add properties on behalf of other users
- The system validates that the user with the provided permanent ID exists before adding a property
- The property owner's information is preserved, and the franchisee is only recorded as the "added by" user 