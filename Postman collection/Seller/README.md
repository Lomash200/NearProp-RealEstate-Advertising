# NearProp Seller/Developer/Property Advisor API Collection

This directory contains a unified Postman collection for Seller, Developer, and Property Advisor roles in the NearProp application.

## Collection File

**NearProp_Seller_Unified.json** - Complete collection with all APIs organized into the following sections:

1. **Authentication** - Login, registration, and token management
2. **Property Management** - Create, update, delete, and list properties
3. **Visit Management** - Handle property visits and appointments
4. **Reel Management** - Create, update, and manage property reels
5. **Chat Management** - Manage property-related chat conversations
6. **Analytics** - Access analytics data for properties, reels, and visits
7. **Subscription Management** - Manage property subscriptions

## How to Use

1. Import the unified collection file into Postman
2. Set up an environment with the following variables:
   - `baseUrl`: The base URL of your NearProp API (e.g., `http://localhost:8080`)
   - `authToken`: Will be set automatically after login
   - `propertyId`: Property ID for testing
   - `reelId`: Reel ID for testing
   - `chatRoomId`: Chat room ID for testing
   - `subscriptionId`: Subscription ID for testing
   - `visitId`: Visit ID for testing

3. Start with the Authentication endpoints to obtain a valid auth token
4. Use the other sections for specific functionality

## Authentication

Before using any of the endpoints that require authentication, you need to:

1. Use the "Login" request in the Authentication folder
2. Enter valid credentials for a Seller/Developer/Property Advisor account
3. The auth token will be automatically set for subsequent requests

## Common Workflows

### Property Listing Workflow

1. Login to get authentication token
2. Create a new property
3. Activate the property with a subscription
4. Upload reels for the property
5. Manage visits to the property
6. Track property analytics

### Subscription Management Workflow

1. Browse available subscription plans
2. Create a new subscription
3. Use the subscription to activate properties
4. Renew or cancel subscriptions as needed

## Testing Notes

- Most endpoints require the appropriate role permissions (SELLER, ADVISOR, or DEVELOPER)
- The collection uses test scripts to extract and set variables like property IDs, reel IDs, etc.
- File upload endpoints require you to set the correct file paths in your local environment

## Error Handling

The API returns standard HTTP status codes:
- 200/201: Success
- 400: Bad Request (invalid input)
- 401: Unauthorized (invalid or missing token)
- 403: Forbidden (insufficient permissions)
- 404: Not Found
- 500: Server Error

Error responses include detailed messages to help diagnose issues. 