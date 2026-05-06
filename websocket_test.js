// WebSocket Test Client for NearProp
const WebSocket = require('ws');

// Configuration
const token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMCIsInJvbGVzIjpbIlVTRVIiLCJBRE1JTiJdLCJzZXNzaW9uSWQiOiI4Y2Q4Mzc3Ny05MjZiLTRlZDItOTkwMy1iMGIzMzM4Y2Y5YmYiLCJpYXQiOjE3NDgyNjE0OTEsImV4cCI6MTc0ODg2NjI5MSwiaXNzIjoiTmVhcnByb3BCYWNrZW5kIn0.kQF3KOct_O84FYGBNhNnat4vW-jAxzULrKDNlzrRcz67efg6x9zKGl4Me3vwbN9kS6keVB7TjnvB6Y9eu3Xp6w";
const chatRoomId = 2;
const wsUrl = `ws://localhost:8080/api/ws?token=${token}`;

// STOMP frame builder
function buildStompFrame(command, headers = {}, body = '') {
  const headersString = Object.entries(headers)
    .map(([key, value]) => `${key}:${value}`)
    .join('\n');
  
  return `${command}\n${headersString}\n\n${body}\0`;
}

// Connect to WebSocket server
console.log(`Connecting to ${wsUrl}...`);
const ws = new WebSocket(wsUrl);

// WebSocket event handlers
ws.on('open', () => {
  console.log('WebSocket connection established');
  
  // Send STOMP CONNECT frame
  const connectFrame = buildStompFrame('CONNECT', {
    'accept-version': '1.2',
    'heart-beat': '10000,10000'
  });
  
  console.log('Sending STOMP CONNECT frame...');
  ws.send(connectFrame);
  
  // Wait for CONNECTED frame before proceeding
  setTimeout(() => {
    // Subscribe to chat room topic
    const subscribeFrame = buildStompFrame('SUBSCRIBE', {
      'id': 'sub-0',
      'destination': `/topic/chat/${chatRoomId}`
    });
    
    console.log(`Subscribing to /topic/chat/${chatRoomId}...`);
    ws.send(subscribeFrame);
    
    // Also subscribe to typing notifications
    const typingSubFrame = buildStompFrame('SUBSCRIBE', {
      'id': 'sub-1',
      'destination': `/topic/chat/${chatRoomId}/typing`
    });
    
    console.log(`Subscribing to typing notifications...`);
    ws.send(typingSubFrame);
    
    // Wait a bit then send a typing indicator
    setTimeout(() => {
      const typingFrame = buildStompFrame('SEND', {
        'destination': `/app/chat/${chatRoomId}/typing`,
        'content-type': 'application/json',
      }, JSON.stringify(true));
      
      console.log('Sending typing indicator (true)...');
      ws.send(typingFrame);
      
      // After 2 seconds, send a message
      setTimeout(() => {
        const messageFrame = buildStompFrame('SEND', {
          'destination': `/app/chat/${chatRoomId}/send`,
          'content-type': 'application/json',
        }, JSON.stringify({
          'content': 'Hello from WebSocket test client!'
        }));
        
        console.log('Sending message...');
        ws.send(messageFrame);
        
        // After sending the message, stop typing
        setTimeout(() => {
          const stopTypingFrame = buildStompFrame('SEND', {
            'destination': `/app/chat/${chatRoomId}/typing`,
            'content-type': 'application/json',
          }, JSON.stringify(false));
          
          console.log('Sending typing indicator (false)...');
          ws.send(stopTypingFrame);
          
          // Keep connection open for a while to see any responses
          console.log('Waiting for responses...');
          setTimeout(() => {
            // Disconnect gracefully
            const disconnectFrame = buildStompFrame('DISCONNECT', {
              'receipt': 'bye'
            });
            
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
    // Extract STOMP frames from WebSocket message
    const message = data.toString();
    
    // Simple parsing of STOMP frames
    const frameLines = message.split('\n');
    const command = frameLines[0];
    
    console.log(`STOMP Command: ${command}`);
    
    // Extract headers and body
    const headerEndIndex = frameLines.findIndex(line => line === '');
    const headers = frameLines.slice(1, headerEndIndex)
      .reduce((acc, header) => {
        const [key, value] = header.split(':');
        acc[key] = value;
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