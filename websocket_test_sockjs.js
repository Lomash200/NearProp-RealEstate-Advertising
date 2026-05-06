// WebSocket Test Client for NearProp using SockJS
const WebSocket = require('ws');

// Configuration
const token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMCIsInJvbGVzIjpbIlVTRVIiLCJBRE1JTiJdLCJzZXNzaW9uSWQiOiI4Y2Q4Mzc3Ny05MjZiLTRlZDItOTkwMy1iMGIzMzM4Y2Y5YmYiLCJpYXQiOjE3NDgyNjE0OTEsImV4cCI6MTc0ODg2NjI5MSwiaXNzIjoiTmVhcnByb3BCYWNrZW5kIn0.kQF3KOct_O84FYGBNhNnat4vW-jAxzULrKDNlzrRcz67efg6x9zKGl4Me3vwbN9kS6keVB7TjnvB6Y9eu3Xp6w";
const chatRoomId = 2;

// SockJS uses a different URL pattern
const sockjsServer = "ws://localhost:8080/api/ws";
const sockjsSessionId = Math.floor(Math.random() * 1000);
const wsUrl = `${sockjsServer}/${sockjsSessionId}/websocket`;

console.log(`Using SockJS URL: ${wsUrl}`);

// Connect to WebSocket server
console.log(`Connecting to ${wsUrl}...`);
const ws = new WebSocket(wsUrl);

// WebSocket event handlers
ws.on('open', () => {
  console.log('WebSocket connection established');
  
  // SockJS expects first a connection message
  console.log('Sending SockJS connection message...');
  ws.send(JSON.stringify(["websocket"]));
  
  // Then send the STOMP CONNECT frame wrapped in SockJS format
  const stompConnect = JSON.stringify([`CONNECT
accept-version:1.2
heart-beat:10000,10000

\0`]);
  
  console.log('Sending STOMP CONNECT frame...');
  ws.send(stompConnect);
  
  // Wait for CONNECTED frame before proceeding
  setTimeout(() => {
    // Subscribe to chat room topic
    const stompSubscribe = JSON.stringify([`SUBSCRIBE
id:sub-0
destination:/topic/chat/${chatRoomId}

\0`]);
    
    console.log(`Subscribing to /topic/chat/${chatRoomId}...`);
    ws.send(stompSubscribe);
    
    // Also subscribe to typing notifications
    const typingSubFrame = JSON.stringify([`SUBSCRIBE
id:sub-1
destination:/topic/chat/${chatRoomId}/typing

\0`]);
    
    console.log(`Subscribing to typing notifications...`);
    ws.send(typingSubFrame);
    
    // Wait a bit then send a typing indicator
    setTimeout(() => {
      const typingMsg = JSON.stringify(true);
      const typingFrame = JSON.stringify([`SEND
destination:/app/chat/${chatRoomId}/typing
content-type:application/json
content-length:${typingMsg.length}

${typingMsg}\0`]);
      
      console.log('Sending typing indicator (true)...');
      ws.send(typingFrame);
      
      // After 2 seconds, send a message
      setTimeout(() => {
        const messageContent = JSON.stringify({ content: "Hello from WebSocket test client!" });
        const messageFrame = JSON.stringify([`SEND
destination:/app/chat/${chatRoomId}/send
content-type:application/json
content-length:${messageContent.length}

${messageContent}\0`]);
        
        console.log('Sending message...');
        ws.send(messageFrame);
        
        // After sending the message, stop typing
        setTimeout(() => {
          const stopTypingMsg = JSON.stringify(false);
          const stopTypingFrame = JSON.stringify([`SEND
destination:/app/chat/${chatRoomId}/typing
content-type:application/json
content-length:${stopTypingMsg.length}

${stopTypingMsg}\0`]);
          
          console.log('Sending typing indicator (false)...');
          ws.send(stopTypingFrame);
          
          // Keep connection open for a while to see any responses
          console.log('Waiting for responses...');
          setTimeout(() => {
            // Disconnect gracefully
            const disconnectFrame = JSON.stringify([`DISCONNECT
receipt:bye

\0`]);
            
            console.log('Disconnecting...');
            ws.send(disconnectFrame);
            
            setTimeout(() => {
              ws.close();
              console.log('WebSocket connection closed');
            }, 500);
          }, 5000);
        }, 1000);
      }, 2000);
    }, 1000);
  }, 1000);
});

ws.on('message', (data) => {
  console.log('Received message:');
  
  try {
    // Parse SockJS message
    const sockJsMsg = JSON.parse(data);
    
    // SockJS messages come as arrays
    if (Array.isArray(sockJsMsg)) {
      const stompMsg = sockJsMsg[0];
      
      // Check if it's a STOMP frame
      if (typeof stompMsg === 'string') {
        const frameLines = stompMsg.split('\n');
        const command = frameLines[0];
        
        console.log(`STOMP Command: ${command}`);
        
        // Extract headers and body
        const headerEndIndex = frameLines.findIndex(line => line === '');
        if (headerEndIndex > 0) {
          const headers = frameLines.slice(1, headerEndIndex)
            .reduce((acc, header) => {
              if (header.includes(':')) {
                const [key, value] = header.split(':', 2);
                acc[key] = value;
              }
              return acc;
            }, {});
          
          console.log('Headers:', headers);
          
          // Get body (remove null terminator)
          const body = frameLines.slice(headerEndIndex + 1).join('\n').replace('\0', '');
          
          if (body) {
            try {
              const parsedBody = JSON.parse(body);
              console.log('Body (parsed):', parsedBody);
            } catch (e) {
              console.log('Body (raw):', body);
            }
          }
        } else {
          console.log('Raw STOMP frame:', stompMsg);
        }
      } else {
        console.log('SockJS message:', sockJsMsg);
      }
    } else {
      console.log('Non-array SockJS message:', sockJsMsg);
    }
  } catch (e) {
    console.log('Raw data:', data.toString());
  }
  console.log('--------------------------');
});

ws.on('error', (error) => {
  console.error('WebSocket error:', error);
});

ws.on('close', (code, reason) => {
  console.log(`WebSocket closed: ${code} - ${reason}`);
}); 