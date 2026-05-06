# NearProp Developer Collection

This collection contains all the API endpoints required for Developer role in the NearProp application.

## Setup

1. Import the `NearProp_Developer_Collection_Final.json` file into Postman
2. Set up an environment with the following variables:
   - `baseUrl`: The base URL of your NearProp API (e.g., `http://localhost:8080` or `https://api.nearprop.com`)
   - The collection automatically sets the following variables during execution:
     - `authToken`: Authentication token after login
     - `propertyId`: ID of the created property
     - `reelId`: ID of the uploaded reel
     - `visitId`: ID of the scheduled visit
     - `chatRoomId`: ID of the created chat room
     - `messageId`: ID of the sent message
     - `subscriptionId`: ID of the created subscription
     - `paymentReferenceId`: Reference ID for payments

## Sections

### 1. Authentication
- Login
- Request Developer Role
- Verify Email
- Get My Role Requests

### 2. Property Management
- Create Property
- Get Property
- Update Property
- Delete Property
- Search Properties

### 3. Reel Management
- Upload Reel
- Get Reel Details
- Get My Reels
- Get Property Reels
- Delete Reel
- Add Comment to Reel
- Like/Unlike Reel

### 4. Visit Management
- Schedule Visit
- Get Visit Details
- Get My Property Visits
- Update Visit Status
- Cancel Visit

### 5. Chat Management
- Create Chat Room
- Get Chat Room
- Get User Chat Rooms
- Get Property Chat Rooms
- Add Participant to Chat Room
- Close/Reopen Chat Room
- Send Message
- Get Chat Messages
- Get Recent/Unread Messages
- Mark Messages as Read
- Edit/Delete Message
- Upload Attachment

### 6. Analytics
- Get Property Analytics
- Get Reel Analytics

### 7. Subscription Management
- Get All Subscription Plans
- Get Developer Subscription Plans
- Create Subscription
- Get Subscription
- Get My Subscriptions
- Cancel/Renew Subscription
- Check Property Limits

### 8. Payment
- Initiate Payment
- Verify Payment
- Check Payment Status
- Get Payment Receipt
- Cancel Payment
- Request Refund

## Usage Flow

1. Start by using the Login endpoint to authenticate and get a token
2. Use the Property Management endpoints to create and manage properties
3. Create reels for properties using the Reel Management endpoints
4. Manage property visits with the Visit Management endpoints
5. Communicate with users through the Chat Management endpoints
6. View performance metrics using the Analytics endpoints
7. Subscribe to premium plans using the Subscription Management endpoints
8. Process payments using the Payment endpoints

## Developer-Specific Notes

- As a Developer, you can create and manage multiple property projects
- Analytics provide insights into property engagement and performance
- Subscription plans determine the number of properties you can list
- The chat system allows direct communication with potential buyers
- Visit management helps coordinate property showings efficiently 