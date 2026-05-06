# NearProp New APIs Documentation

## Overview

This document provides detailed information about the newly implemented APIs in the NearProp application:

1. User Profile APIs (including permanent ID and AWS profile image uploads)
2. Developer Property APIs (with unit types, counts, and stock management)
3. Public Property Filter APIs (without authentication requirements)
4. Fixed Franchisee Districts API

## 1. User Profile APIs

### 1.1 Get User Profile (with Permanent ID)

Retrieves the current user's profile information including the permanent ID.

- **URL:** `/v1/users/profile`
- **Method:** `GET`
- **Authentication:** Required
- **Response:**
  ```json
  {
    "status": "success",
    "message": "User profile retrieved successfully",
    "data": {
      "id": 1,
      "permanentId": "usr-123456789",
      "name": "John Doe",
      "email": "john.doe@example.com",
      "mobileNumber": "+919876543210",
      "profileImageUrl": "https://nearprop-documents.s3.ap-south-1.amazonaws.com/profiles/1/john_doe_profile_pic.jpg",
      "roles": ["USER", "SELLER"]
    }
  }
  ```

### 1.2 Upload Profile Image

Uploads a user profile image to AWS S3 with a standardized naming convention and updates the user profile.

- **URL:** `/v1/users/profile/image`
- **Method:** `POST`
- **Authentication:** Required
- **Content-Type:** `multipart/form-data`
- **Request Parameters:**
  - `image` (file): The profile image file to upload
- **Implementation Details:**
  - Image is uploaded to AWS S3 using the format: `profiles/{userId}/{username}_profile_pic.extension`
  - User profile is updated with the new image URL
- **Response:**
  ```json
  {
    "status": "success",
    "message": "Profile image uploaded successfully",
    "data": {
      "id": 1,
      "permanentId": "usr-123456789",
      "name": "John Doe",
      "email": "john.doe@example.com",
      "mobileNumber": "+919876543210",
      "profileImageUrl": "https://nearprop-documents.s3.ap-south-1.amazonaws.com/profiles/1/john_doe_profile_pic.jpg",
      "roles": ["USER", "SELLER"]
    }
  }
  ```

## 2. Developer Property APIs

### 2.1 Create Developer Property

Creates a new property listing with developer-specific fields like unit type, unit count, and stock.

- **URL:** `/properties/developer-form`
- **Method:** `POST`
- **Authentication:** Required (DEVELOPER role)
- **Content-Type:** `multipart/form-data`
- **Request Parameters:**
  - Basic Property Information:
    - `title` (string): Property title
    - `description` (string): Detailed property description
    - `type` (enum): Property type (APARTMENT, HOUSE, VILLA, COMMERCIAL, etc.)
    - `status` (enum): Property status (FOR_SALE, FOR_RENT, etc.)
    - `label` (enum, optional): Property label (FEATURED, HOT_OFFER, etc.)
    - `price` (number): Property price
    - `area` (number): Property area
    - `sizePostfix` (string, optional): Size unit (sq ft, sq m, etc.)
  - Location Information:
    - `address` (string): Property address
    - `districtId` (number): District ID
    - `city` (string, optional): City
    - `state` (string, optional): State
    - `pincode` (string, optional): Pincode/ZIP
    - `latitude` (number, optional): Latitude
    - `longitude` (number, optional): Longitude
  - Property Features:
    - `bedrooms` (number): Number of bedrooms
    - `bathrooms` (number): Number of bathrooms
    - `amenities` (JSON string, optional): Property amenities as JSON array
    - `features` (JSON string, optional): Property features as JSON array
  - Developer-Specific Fields:
    - `unitType` (string): Unit type (1BHK, 2BHK, etc.)
    - `unitCount` (number): Total number of units
    - `stock` (number): Number of units available for sale/rent
  - Media:
    - `images` (files, optional): Property images
    - `video` (file, optional): Property video
- **Implementation Details:**
  - Media files are uploaded to AWS S3
  - Email notifications are sent to the developer
  - All developer-specific fields are validated and required
- **Response:**
  ```json
  {
    "id": 1,
    "permanentId": "prop-123456789",
    "title": "Modern Luxury Apartment Complex",
    "description": "Brand new luxury apartment complex with premium amenities...",
    "type": "APARTMENT",
    "status": "FOR_SALE",
    "price": 5000000,
    "area": 1200,
    "unitType": "2BHK",
    "unitCount": 100,
    "stock": 80,
    "imageUrls": ["https://nearprop-documents.s3.ap-south-1.amazonaws.com/properties/..."],
    "approved": false,
    "active": true
  }
  ```

### 2.2 Update Property Stock

Updates the stock (available units) of a developer property.

- **URL:** `/properties/{propertyId}/update-stock`
- **Method:** `PUT`
- **Authentication:** Required (DEVELOPER role)
- **URL Parameters:**
  - `propertyId` (number): ID of the property to update
- **Query Parameters:**
  - `stock` (number): New stock value
- **Implementation Details:**
  - Only the owner of the property can update the stock
  - Email notifications are sent to the developer
- **Response:**
  ```json
  {
    "id": 1,
    "permanentId": "prop-123456789",
    "title": "Modern Luxury Apartment Complex",
    "unitType": "2BHK",
    "unitCount": 100,
    "stock": 75
  }
  ```

## 3. Public Property Filter APIs

### 3.1 Get All Properties

Gets all approved properties with pagination and sorting. No authentication required.

- **URL:** `/public/properties`
- **Method:** `GET`
- **Authentication:** Not required
- **Query Parameters:**
  - `page` (number, default: 0): Page number (0-based)
  - `size` (number, default: 10): Page size
  - `sortBy` (string, default: "createdAt"): Field to sort by
  - `direction` (string, default: "DESC"): Sort direction (ASC or DESC)
- **Response:** Paginated list of property data

### 3.2 Filter Properties

Filters properties based on multiple criteria. All filter parameters are optional.

- **URL:** `/public/properties/filter`
- **Method:** `GET`
- **Authentication:** Not required
- **Query Parameters:**
  - Status and Type:
    - `category` (enum, optional): Property status/category (FOR_SALE, FOR_RENT, etc.)
    - `propertyType` (enum, optional): Property type (APARTMENT, HOUSE, COMMERCIAL, etc.)
  - Location:
    - `city` (string, optional): City name
    - `district` (string, optional): District name
    - `latitude` (number, optional): Latitude for location-based search
    - `longitude` (number, optional): Longitude for location-based search
    - `radius` (number, optional): Radius in kilometers for location-based search
  - Price and Area:
    - `minPrice` (number, optional): Minimum price
    - `maxPrice` (number, optional): Maximum price
    - `minArea` (number, optional): Minimum area
    - `maxArea` (number, optional): Maximum area
  - Features:
    - `bedrooms` (number, optional): Number of bedrooms
    - `bathrooms` (number, optional): Number of bathrooms
  - Text Search:
    - `keyword` (string, optional): Keyword to search in title and description
  - Pagination:
    - `page` (number, default: 0): Page number (0-based)
    - `size` (number, default: 10): Page size
    - `sortBy` (string, default: "createdAt"): Field to sort by
    - `direction` (string, default: "DESC"): Sort direction (ASC or DESC)
- **Implementation Details:**
  - Only returns approved and active properties
  - Location-based search uses approximate bounding box calculations
  - Text search is case-insensitive and searches in title and description
- **Response:** Paginated list of property data matching the filter criteria

### 3.3 Get Featured Properties

Gets featured properties with pagination.

- **URL:** `/public/properties/featured`
- **Method:** `GET`
- **Authentication:** Not required
- **Query Parameters:**
  - `page` (number, default: 0): Page number (0-based)
  - `size` (number, default: 5): Page size
- **Response:** Paginated list of featured property data

### 3.4 Get Property By ID

Gets a specific property by ID. Only returns approved and active properties.

- **URL:** `/public/properties/{propertyId}`
- **Method:** `GET`
- **Authentication:** Not required
- **URL Parameters:**
  - `propertyId` (number): ID of the property to retrieve
- **Response:** Property data or 404 Not Found if property is not approved or active

### 3.5 Get All Districts

Gets all available districts.

- **URL:** `/public/properties/districts`
- **Method:** `GET`
- **Authentication:** Not required
- **Response:** List of district names

### 3.6 Get All States

Gets all available states.

- **URL:** `/public/properties/states`
- **Method:** `GET`
- **Authentication:** Not required
- **Response:** List of state names

### 3.7 Get Property Count By Type

Gets the count of properties by type.

- **URL:** `/public/properties/stats/by-type`
- **Method:** `GET`
- **Authentication:** Not required
- **Response:**
  ```json
  {
    "status": "success",
    "message": "Property counts by type",
    "data": {
      "APARTMENT": 25,
      "HOUSE": 15,
      "VILLA": 8,
      "COMMERCIAL": 12
    }
  }
  ```

## 4. Franchisee Districts API

### 4.1 Get Districts By Franchisee ID

Gets districts assigned to a franchisee by franchisee user ID. Returns an empty list for invalid IDs.

- **URL:** `/franchisee/districts/by-franchisee/{franchiseeId}`
- **Method:** `GET`
- **Authentication:** Required (ADMIN or FRANCHISEE role)
- **URL Parameters:**
  - `franchiseeId` (number): ID of the franchisee user
- **Implementation Details:**
  - Enhanced error handling to return empty list instead of error for invalid IDs
  - Better null checks to prevent exceptions
- **Response:**
  ```json
  [
    {
      "id": 1,
      "name": "Andheri East",
      "state": "Maharashtra",
      "city": "Mumbai",
      "pincode": "400093",
      "revenueSharePercentage": 5.0,
      "latitude": 19.1136,
      "longitude": 72.8697,
      "radiusKm": 10.0,
      "active": true
    }
  ]
  ```

## Testing

A comprehensive test script `test-all-new-apis.sh` is provided to validate all the new APIs. The script includes:

1. User Profile API tests (requires authentication)
2. Developer Property API tests (requires authentication and developer role)
3. Public Property Filter API tests (no authentication required)
4. Franchisee Districts API test (requires authentication)

To run the tests:
```bash
chmod +x test-all-new-apis.sh
./test-all-new-apis.sh
```

## Postman Collection

A Postman collection file `NearProp_New_APIs_Collection.json` is also provided with examples for all the new APIs. Import this file into Postman to easily test the APIs with sample data. 