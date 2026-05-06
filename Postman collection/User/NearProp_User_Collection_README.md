# NearProp User Collection

This comprehensive Postman collection contains all user-accessible APIs for the NearProp platform, organized in a logical flow from authentication to property management and user interactions.

## Collection Structure

The collection is organized into the following sections:

1. **Authentication**
   - User registration and login
   - OTP verification

2. **User Profile**
   - User profile management

3. **Property Management**
   - Property search and browsing
   - Property creation (for sellers)
   - Property details

4. **Favorites**
   - Add/remove properties to favorites
   - View favorite properties

5. **Reviews**
   - Create and manage property reviews
   - View property reviews

6. **Property Visits**
   - Schedule property visits
   - Manage visit status

7. **Chat**
   - Property inquiry chat rooms
   - Message management

8. **Reels**
   - Property reel feed
   - Like and save reels

9. **Subscription & Payment**
   - View subscription plans
   - Manage subscriptions
   - Process payments

10. **Localization**
    - Change language/locale
    - Get supported languages

## Setup Instructions

1. Import the collection into Postman
2. Set up the environment variables using the provided NearProp-Environment-Updated.json file
3. Start with the authentication endpoints to obtain a valid token

## Variables

The collection uses the following variables (already set in the NearProp-Environment-Updated.json):

- `baseUrl`: Base URL of the API (default: http://localhost:8080)
- `apiPrefix`: API prefix for all endpoints except auth (default: /api)
- `authPrefix`: Prefix for authentication endpoints (default: /api/v1/auth)
- `userToken`: JWT token for a regular user (USER role)
- `sellerToken`: JWT token for a seller user (SELLER role)
- `propertyId`: ID of a property for testing
- `visitId`: ID of a visit for testing
- `reelId`: ID of a reel for testing
- And other context-specific variables

## Usage Notes

- Independent API endpoints (those that don't depend on other API calls) are placed first
- Dependent API endpoints follow in a logical flow
- Test scripts automatically store important IDs in environment variables for use in subsequent requests

## Troubleshooting

- If you encounter authentication issues, ensure your token is valid and not expired
- For 404 errors, check that the resource IDs are correct
- For 400 errors, verify your request payload matches the expected format

## API Sequence Flow

For a typical user journey, follow this sequence:

1. Register/Login → Get user token
2. Browse properties → Find a property of interest
3. View property details → Get more information
4. Add to favorites → Save for later
5. Schedule a visit → Plan to see the property
6. Create a chat → Inquire about the property
7. Write a review → Share your experience 