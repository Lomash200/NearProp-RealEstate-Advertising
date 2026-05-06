# WebSocket Testing Tools for NearProp

This directory contains tools for testing the WebSocket functionality of the NearProp application, particularly the chat system.

## Available Test Tools

1. **index.html** - Comprehensive WebSocket testing client with STOMP and direct WebSocket support
2. **direct-test.html** - Simple WebSocket testing client for direct WebSocket connections
3. **dual-chat.html** - Dual view chat client that simulates both buyer and seller perspectives simultaneously

## Understanding WebSocket Communication

### Connection Flow

1. **Authentication**: The WebSocket connection is authenticated using the JWT token in the URL query parameter
   ```
   ws://localhost:8080/api/ws?token=YOUR_JWT_TOKEN
   ```
   
   **Important Note:** Only the `/api/ws` path is currently working. Other paths like `/ws`, `/socket`, or `/websocket` may result in connection errors.

2. **STOMP Protocol**: Once connected, the application uses STOMP (Simple Text Oriented Messaging Protocol) over WebSocket for message exchange

### Destinations

1. **Chat Room Messages**:
   - Subscribe: `/topic/chat/{chatRoomId}`
   - Send: `/app/chat/{chatRoomId}/send`

2. **Typing Indicators**:
   - Subscribe: `/topic/chat/{chatRoomId}/typing`
   - Send: `/app/chat/{chatRoomId}/typing`

3. **Personal Notifications**:
   - Subscribe: `/user/queue/messages`

### User Roles in Chat

- **Regular User**: Standard users who can send and receive messages
- **Property Owner (SELLER)**: Property owners who can respond to inquiries
- **Admin**: System administrators who can intervene in conversations

## Testing Features

### Dual Chat View (NEW)

The `dual-chat.html` tool allows you to test both sides of a conversation simultaneously:

1. **Buyer View**: Left panel showing the conversation from the buyer's perspective
2. **Seller View**: Right panel showing the conversation from the property owner's perspective

This helps to verify that:
- Messages are correctly delivered to both parties
- Typing indicators work in both directions
- Sender information (roles, names) appears correctly
- Messages are styled differently based on sender role

To use:
1. Enter the Chat Room ID (e.g., 3 for property ID 3)
2. Connect using both the buyer and seller JWT tokens
3. Test messaging from both perspectives

### Direct WebSocket Test

The `direct-test.html` tool tests raw WebSocket connections without the STOMP protocol:

1. Enter the WebSocket URL and your JWT token
2. Connect and send messages directly
3. Test with property owners by using a seller JWT token

### Comprehensive Test Client

The `index.html` tool provides a complete testing environment with advanced features:

1. Connection management for both STOMP and direct WebSocket
2. Message sending with typing indicators
3. Selection of different user roles (regular user, property owner, admin)
4. History of sent and received messages

## Running the Test Tools

1. Start the NearProp backend server
2. Host these files using a simple HTTP server:

```bash
# From the websocket-test directory
python3 -m http.server 8001
```

3. Open the test tools in your browser:
   - http://localhost:8001/index.html
   - http://localhost:8001/direct-test.html
   - http://localhost:8001/dual-chat.html

## Troubleshooting

1. **Connection Issues**:
   - Check that your token is valid and not expired
   - Verify the WebSocket URL is correct (must use `/api/ws` path)
   - Ensure the NearProp server is running

2. **Authentication Problems**:
   - JWT must be valid and not expired
   - For property owner functionality, the token must contain the SELLER role

3. **Message Delivery Issues**:
   - Ensure you're subscribed to the correct topics
   - Verify you're sending to the correct destinations
   - Check that the chat room ID is correct

4. **Testing Seller Responses**:
   - Use the seller JWT token provided or generate one by logging in as a property owner
   - In dual-chat.html, both connections are maintained simultaneously
   - Verify that messages from sellers are styled differently 