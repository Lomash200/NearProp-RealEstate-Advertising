# NearProp Chat Testing Tool

This tool allows you to test the NearProp chat system with all three roles (User/Buyer, Seller, and Admin) simultaneously. It provides a convenient way to test and debug chat-related features and APIs.

## Features

- Three-panel interface to test all roles simultaneously
- Real-time WebSocket communication
- Typing indicators
- Message status tracking (sent, read, etc.)
- API testing interface for REST endpoints
- Debug logging
- Token management
- Create new chat rooms

## Setup

1. Open `index.html` in a web browser
2. Configure the WebSocket URL (e.g., `ws://localhost:8080/api/ws`)
3. Enter JWT tokens for each role
4. Enter chat room ID or create a new room
5. Click "Connect All" to establish connections

## Using the Chat Interface

- Each panel represents a different role (User, Seller, Admin)
- Type messages in the text area and click "Send" or press Enter
- Use the "Start Typing" and "Stop Typing" buttons to test typing indicators
- Click "Mark as Read" on messages to test read receipts

## Testing APIs

1. Go to the "API Testing" tab
2. Select the HTTP method (GET, POST, PUT, PATCH, DELETE)
3. Enter the endpoint (e.g., `/api/chat/rooms/3`)
4. Select the role to use for authentication
5. Add query parameters or request body in JSON format if needed
6. Click "Send Request" to make the API call
7. View the response in the response area

## Common API Endpoints

- Get chat rooms: `GET /api/chat/rooms`
- Get specific chat room: `GET /api/chat/rooms/{roomId}`
- Create chat room: `POST /api/chat/rooms` with `{"propertyId": 8}`
- Get messages: `GET /api/chat/rooms/{roomId}/messages`
- Send message: `POST /api/chat/rooms/{roomId}/messages` with `{"content": "Hello"}`
- Mark message as read: `PUT /api/chat/messages/{messageId}/read`
- Get chat room participants: `GET /api/chat/rooms/{roomId}/participants`
- Add participant (admin only): `POST /api/chat/rooms/{roomId}/participants` with `{"userId": 5}`
- Remove participant (admin only): `DELETE /api/chat/rooms/{roomId}/participants/{userId}`
- Close chat room: `PATCH /api/chat/rooms/{roomId}/close`

## Troubleshooting

- If connections fail, check the WebSocket URL and tokens
- If messages don't appear, check the chat room ID
- Check the debug log for detailed error messages
- Try disconnecting and reconnecting all users 