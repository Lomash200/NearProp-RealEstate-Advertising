# WebSocket Endpoint Updates for Postman Collection

The following modifications should be made to the NearProp.postman_collection.json file to update the WebSocket endpoints:

## WebSocket Connection URL

Update the WebSocket connection URL in the "WebSocket Connection" request:

```diff
- "raw": "ws://{{baseUrl}}ws?token={{userToken}}",
+ "raw": "ws://{{baseUrl}}api/ws?token={{userToken}}",
```

## WebSocket STOMP Destinations

Update the STOMP destinations in the WebSocket sections:

### Send WebSocket Message

```diff
{
    "destination": "/app/chat/{{chat_room_id}}/send",
    "body": {
        "content": "Hello via WebSocket!",
        "attachmentIds": []
    }
}
```

### Send Typing Indicator

```diff
{
    "destination": "/app/chat/{{chat_room_id}}/typing",
    "body": true
}
```

### Mark Message as Read via WebSocket

```diff
{
    "destination": "/app/chat/message/{{message_id}}/read"
}
```

## Subscription Topics

Update the subscription topics:

### Subscribe to Chat Room Updates

```diff
- "raw": "/topic/chat/{{chat_room_id}}",
+ "raw": "/topic/chat/{{chat_room_id}}",
```

### Subscribe to User Chat Notifications

```diff
- "raw": "/user/queue/messages",
+ "raw": "/user/queue/messages",
```

## Additional WebSocket Documentation

Add the following note to the WebSocket Configuration section description:

```
WebSocket connections must use the `/api/ws` endpoint for authentication with a valid JWT token. 
Property owners with the SELLER role can use their tokens to view and respond to property inquiries.
```

## Testing with Different User Roles

Add the following test script to the WebSocket Connection request:

```javascript
// Extract JWT payload to determine user roles
try {
    const token = pm.variables.get("userToken");
    const tokenParts = token.split('.');
    const payload = JSON.parse(atob(tokenParts[1]));
    
    console.log("User roles from token: " + JSON.stringify(payload.roles));
    
    // Check if user has SELLER role
    if (payload.roles && payload.roles.includes("SELLER")) {
        console.log("This user is a property owner and can respond to property inquiries.");
    } else {
        console.log("This is a regular user without property owner permissions.");
    }
} catch (e) {
    console.error("Error parsing token: " + e);
}
``` 