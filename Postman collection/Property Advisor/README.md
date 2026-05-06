# NearProp Property Advisor API Collection

This Postman collection provides a comprehensive set of API endpoints designed specifically for Property Advisors using the NearProp real estate platform.

## Collection Contents

The collection is organized into the following sections:

1. **Authentication** - Login and role management APIs
2. **Property Management** - APIs for creating, updating, and managing properties
3. **Reel Management** - APIs for creating and managing property video reels
4. **Visit Management** - APIs for scheduling and managing property visits
5. **Chat Management** - APIs for communication with property buyers/sellers
6. **Analytics** - APIs for accessing property and business analytics
7. **Subscription Management** - APIs for managing subscription plans
8. **Payment** - APIs for processing payments

## Environment Setup

Before using this collection, set up the following environment variables in Postman:

- `baseUrl`: The base URL of the NearProp API (e.g., https://api.nearprop.com)
- `authToken`: Will be automatically set after login
- `propertyId`: Will be automatically set after creating a property
- `reelId`: Will be automatically set after creating a reel
- `visitId`: Will be automatically set after scheduling a visit
- `chatRoomId`: Will be automatically set after creating a chat room
- `messageId`: Will be automatically set after sending a message
- `subscriptionId`: Will be automatically set after creating a subscription
- `paymentReferenceId`: Will be automatically set after initiating a payment

## Usage Instructions

1. **Authentication Flow**:
   - Use the "Login" request to authenticate and get a token
   - The token will be automatically saved to your environment

2. **Property Flow**:
   - Create a property using either "Create Property" or "Create Property with Form"
   - Retrieve property details, update, or delete as needed
   - Activate the property with a subscription

3. **Reel Flow**:
   - Upload reels for your properties
   - Manage reel interactions (comments, likes)

4. **Visit Flow**:
   - Manage property visits scheduled by potential buyers
   - Update visit status, cancel visits, etc.

5. **Chat Flow**:
   - Create chat rooms for property discussions
   - Manage messages and participants

6. **Analytics Flow**:
   - Access various analytics about your properties and business performance

7. **Subscription Flow**:
   - View available plans
   - Create and manage subscriptions

8. **Payment Flow**:
   - Process payments for subscriptions
   - Verify payment status and receipts

## Useful Tips

- All endpoints requiring authentication will use the `authToken` environment variable automatically
- Most creation endpoints have test scripts that automatically save IDs to your environment variables
- Use the appropriate content types for each request (JSON for most requests, multipart/form-data for file uploads)

## Troubleshooting

- If you get 401 Unauthorized errors, your token may have expired. Try logging in again.
- If you get 403 Forbidden errors, your account may not have the Property Advisor role assigned.
- If you get 404 Not Found errors for properties, ensure you're using the correct property IDs.

## Additional Resources

- For more information on the NearProp API, refer to the official documentation.
- For questions or issues, contact the NearProp support team.

---

This collection was created to streamline the Property Advisor workflow within the NearProp platform.
