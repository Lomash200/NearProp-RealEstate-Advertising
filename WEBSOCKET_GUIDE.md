# NearProp WebSocket Chat Integration Guide

This guide explains how to use WebSockets for real-time chat functionality in the NearProp application.

## WebSocket Architecture

The NearProp application uses:
- Spring's WebSocket support with STOMP (Simple Text Oriented Messaging Protocol)
- SockJS for compatibility with browsers that don't support WebSockets
- JWT token authentication for securing WebSocket connections

## Connection Flow

1. **Authentication**: First, obtain a valid JWT token through the login process.

2. **Establish WebSocket Connection**: Connect to the WebSocket endpoint with your JWT token.
   ```
   ws://localhost:8080/api/ws?token=YOUR_JWT_TOKEN
   ```
   
   If using SockJS (recommended for browser compatibility):
   ```
   http://localhost:8080/api/ws?token=YOUR_JWT_TOKEN
   ```

3. **STOMP Connection**: After establishing the WebSocket connection, send a STOMP CONNECT frame:
   ```
   CONNECT
   accept-version:1.2
   heart-beat:10000,10000

   
   ```
   Note: The blank line and null byte (`\0`) at the end are important.

4. **Subscription**: Subscribe to relevant topics for receiving messages:
   - Chat room messages: `/topic/chat/{chatRoomId}`
   - Typing indicators: `/topic/chat/{chatRoomId}/typing`
   - Personal read receipts: `/user/queue/read-receipts`
   
   Example subscription frame:
   ```
   SUBSCRIBE
   id:sub-0
   destination:/topic/chat/2

   
   ```

## Sending Messages

### Regular Chat Message

```
SEND
destination:/app/chat/{chatRoomId}/send
content-type:application/json

{"content":"Your message text"}
```

### Admin Message

```
SEND
destination:/app/admin/chat/{chatRoomId}/send
content-type:application/json

{"content":"Official admin message"}
```

### Typing Indicator

```
SEND
destination:/app/chat/{chatRoomId}/typing
content-type:application/json

true
```

To stop the typing indicator:

```
SEND
destination:/app/chat/{chatRoomId}/typing
content-type:application/json

false
```

### Mark Message as Read

```
SEND
destination:/app/chat/message/{messageId}/read

```

## Message Format

Messages received from the server will have this structure:

```json
{
  "id": 123,
  "chatRoomId": 2,
  "content": "Message content",
  "sender": {
    "id": 10,
    "name": "User Name",
    "email": "user@example.com",
    "phone": "1234567890",
    "roles": ["USER", "ADMIN"]
  },
  "createdAt": "2025-05-27T15:51:01.537851",
  "status": "SENT",
  "mine": false,
  "adminMessage": false,
  "edited": false,
  "editedAt": null,
  "readAt": null,
  "deliveredAt": null,
  "reported": false,
  "warned": false,
  "attachments": []
}
```

## Testing with Different Tools

### 1. Using Browser JavaScript

```javascript
// Create a connection
const socket = new SockJS('http://localhost:8080/api/ws?token=YOUR_JWT_TOKEN');
const stompClient = Stomp.over(socket);

// Connect and subscribe
stompClient.connect({}, function(frame) {
  console.log('Connected: ' + frame);
  
  // Subscribe to chat room messages
  stompClient.subscribe('/topic/chat/2', function(message) {
    const messageData = JSON.parse(message.body);
    console.log('Received message:', messageData);
  });
  
  // Subscribe to typing indicators
  stompClient.subscribe('/topic/chat/2/typing', function(typingEvent) {
    const typingData = JSON.parse(typingEvent.body);
    console.log('Typing indicator:', typingData);
  });
});

// Send a message
function sendMessage(content) {
  stompClient.send("/app/chat/2/send", {}, JSON.stringify({content: content}));
}

// Send typing indicator
function sendTypingStatus(isTyping) {
  stompClient.send("/app/chat/2/typing", {}, JSON.stringify(isTyping));
}
```

### 2. Using Node.js

See the provided test scripts (`websocket_test.js` and `websocket_test_sockjs.js`) for examples of connecting to WebSockets using Node.js.

### 3. Using Postman

1. Create a new WebSocket request in Postman
2. Enter the URL: `ws://localhost:8080/api/ws?token=YOUR_JWT_TOKEN`
3. After connecting, use the Messages tab to send STOMP frames

## Common Issues

1. **403 Forbidden errors**:
   - Check that your JWT token is valid and not expired
   - Ensure you have the proper roles for the chat operations

2. **Connection issues**:
   - Verify the WebSocket server is running
   - Check that your token is correctly formatted in the URL

3. **Message not being delivered**:
   - Confirm you're subscribed to the correct topics
   - Check that you're sending to the correct destinations

## WebSocket Security

1. **Token Authentication**: Each WebSocket connection must include a valid JWT token
2. **Authorization**: Messages are validated based on the user's role
3. **Chat Room Permissions**: Users must be participants in a chat room to send/receive messages

## Best Practices

1. Implement reconnection logic if the connection drops
2. Handle WebSocket errors gracefully
3. Consider using a client library like SockJS or STOMP.js for browser applications
4. Implement proper error handling for failed message delivery
5. Add typing indicators with debounce to avoid too many events 