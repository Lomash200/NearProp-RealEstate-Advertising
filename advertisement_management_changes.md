# Advertisement Management System Changes

## Overview
The advertisement management system has been updated to handle form data submissions for creating and updating advertisements, as well as to track different types of clicks and views more effectively.

## Key Changes

### 1. Form Data Handling
- Modified `AdvertisementController` to accept `multipart/form-data` instead of JSON
- Updated endpoint mappings with `@ModelAttribute` annotation
- Added file upload handling for banner images and videos

### 2. Click Tracking
- Created separate endpoints for different types of clicks:
  - `/v1/advertisements/{id}/click/website`
  - `/v1/advertisements/{id}/click/whatsapp`
  - `/v1/advertisements/{id}/click/phone`
  - `/v1/advertisements/{id}/click/instagram`
  - `/v1/advertisements/{id}/click/facebook`
  - `/v1/advertisements/{id}/click/youtube`
  - `/v1/advertisements/{id}/click/twitter`
  - `/v1/advertisements/{id}/click/linkedin`
- Added generic click endpoint with type parameter: `/v1/advertisements/{id}/click?type=TYPE`
- Implemented asynchronous click recording to prevent impact on user experience

### 3. View Tracking
- Automatically tracks views when advertisement details are fetched
- Records district information when provided as query parameter
- Implemented asynchronous view recording to prevent impact on user experience

### 4. Analytics Improvements
- Enhanced admin analytics dashboard with more detailed metrics
- Added social media click breakdown analytics
- Provided district-based analytics for targeted advertisements
- Implemented time-based analytics with daily breakdowns

### 5. File Storage
- Utilized existing file storage configuration
- Added support for uploading banner images and videos
- Files are stored in organized directories with unique names

## Testing
A shell script (`test-advertisement-api.sh`) has been created to test the new APIs, covering:
- Advertisement creation with form data
- View and click tracking
- Analytics retrieval
- Advertisement updates and deletion

## How to Use

### Creating Advertisements with Form Data
```bash
curl -X POST "http://localhost:8080/v1/advertisements" \
  -H "Authorization: Bearer $TOKEN" \
  -F "title=Test Advertisement" \
  -F "description=This is a test advertisement" \
  -F "bannerImage=@/path/to/image.jpg" \
  -F "targetLocation=Indore, MP, India" \
  -F "latitude=22.7196" \
  -F "longitude=75.8577" \
  -F "radiusKm=10" \
  -F "districtName=Indore" \
  -F "validFrom=2025-06-01T00:00:00" \
  -F "validUntil=2025-12-31T23:59:59"
```

### Recording Clicks
```bash
# Record a WhatsApp click
curl -X POST "http://localhost:8080/v1/advertisements/{id}/click/whatsapp"

# Record a specific click type
curl -X POST "http://localhost:8080/v1/advertisements/{id}/click?type=INSTAGRAM"
```

### Viewing Analytics
```bash
# Get analytics for a specific advertisement
curl -X GET "http://localhost:8080/v1/advertisements/{id}/analytics" \
  -H "Authorization: Bearer $TOKEN"

# Get social media analytics
curl -X GET "http://localhost:8080/v1/advertisements/analytics/social-media" \
  -H "Authorization: Bearer $TOKEN"
``` 