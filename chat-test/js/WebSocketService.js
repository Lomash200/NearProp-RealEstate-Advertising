class WebSocketService {
    constructor(role, url, token, chatRoomId) {
        this.role = role;
        this.url = url;
        this.token = token;
        this.chatRoomId = chatRoomId;
        this.socket = null;
        this.stompClient = null;
        this.onMessage = null;
        this.onTyping = null;
        this.onStatusChange = null;
        this.onDebug = null;
        console.log(`[DEBUG] WebSocketService constructor: role=${role}, url=${url}, token=${token}, chatRoomId=${chatRoomId}`);
    }

    debug(message) {
        if (this.onDebug) {
            this.onDebug(`[${this.role}] ${message}`);
        }
        console.log(`[${this.role}] ${message}`);
    }

    connect() {
        const wsUrl = `${this.url}?token=${this.token}`;
        console.log(`[DEBUG] WebSocketService.connect: wsUrl=${wsUrl}`);
        this.debug(`Connecting to: ${wsUrl}`);
        
        // Create WebSocket
        this.socket = new WebSocket(wsUrl);
        
        // WebSocket event handlers
        this.socket.onopen = () => {
            this.debug(`WebSocket connection established successfully!`);
            this.setupStompClient();
            
            // Enable UI even if STOMP connection fails
            setTimeout(() => {
                if (this.onStatusChange) {
                    this.onStatusChange('WS Connected');
                }
            }, 1000); // 1 second backup
        };
        
        this.socket.onmessage = (event) => {
            this.debug(`Direct WebSocket message received: ${event.data.substring(0, 50)}...`);
            try {
                const data = JSON.parse(event.data);
                
                // Handle different message types
                if (data.message && data.message.content) {
                    // Regular chat message
                    const messageData = data.message;
                    this.debug(`Parsed WebSocket message: ${messageData.content}`);
                    
                    // Pass message to callback
                    if (this.onMessage) {
                        this.onMessage(messageData);
                    }
                } else if (data.content !== undefined) {
                    // It might be a direct message format
                    this.debug(`Direct message format detected: ${data.content}`);
                    
                    // Create standardized message object
                    const messageData = {
                        id: data.id,
                        content: data.content,
                        createdAt: data.createdAt || new Date().toISOString(),
                        status: data.status || 'SENT',
                        sender: data.sender || {
                            id: data.senderId,
                            name: data.senderName || (data.roles && data.roles.includes('SELLER') ? 'Property Owner' : data.roles && data.roles.includes('ADMIN') ? 'Admin' : 'Buyer'),
                            roles: data.roles || []
                        }
                    };
                    
                    // Pass message to callback
                    if (this.onMessage) {
                        this.onMessage(messageData);
                    }
                } else if (data.type === 'TYPING') {
                    // Typing indicator
                    const typingData = data;
                    this.debug(`Typing notification from: ${typingData.username || 'unknown'}`);
                    
                    // Pass typing notification to callback
                    if (this.onTyping) {
                        this.onTyping(typingData);
                    }
                } else {
                    this.debug(`Unknown message format: ${JSON.stringify(data).substring(0, 100)}...`);
                }
            } catch (err) {
                this.debug(`Error processing WebSocket message: ${err.message}`);
                console.error(`[DEBUG] Error processing WebSocket message:`, err, 'Token:', this.token, 'URL:', wsUrl);
            }
        };
        
        this.socket.onerror = (error) => {
            this.debug(`WebSocket error occurred. Check server logs.`);
            console.error(`[DEBUG] WebSocket error:`, error, 'Token:', this.token, 'URL:', wsUrl);
            if (this.onStatusChange) {
                this.onStatusChange('Error');
            }
        };
        
        this.socket.onclose = (event) => {
            let reason;
            switch(event.code) {
                case 1000: reason = "Normal closure"; break;
                case 1002: reason = "Protocol error"; break;
                case 1003: reason = "Unsupported data"; break;
                case 1005: reason = "No status received"; break;
                case 1006: reason = "Abnormal closure"; break;
                case 1007: reason = "Invalid frame payload data"; break;
                case 1008: reason = "Policy violation"; break;
                case 1009: reason = "Message too big"; break;
                case 1010: reason = "Missing extension"; break;
                case 1011: reason = "Internal error"; break;
                case 1012: reason = "Service restart"; break;
                case 1013: reason = "Try again later"; break;
                case 1014: reason = "Bad gateway"; break;
                case 1015: reason = "TLS handshake"; break;
                default: reason = "Unknown"; break;
            }
            
            this.debug(`WebSocket closed: ${event.code} - ${reason}`);
            console.warn(`[DEBUG] WebSocket closed: code=`, event.code, 'reason=', reason, 'Token:', this.token, 'URL:', wsUrl);
            if (this.onStatusChange) {
                this.onStatusChange('Disconnected');
            }
            
            // Try alternative connection if abnormal closure
            if (event.code === 1006) {
                this.debug(`Trying alternative connection after abnormal closure...`);
                this.tryAlternativeConnection();
            }
        };
    }

    tryAlternativeConnection() {
        // Extract base URL without /api/ws part
        const baseServer = this.url.split('/api/ws')[0];
        const alternativeUrl = `${baseServer}/api/ws`;
        
        this.debug(`Trying alternative URL: ${alternativeUrl}`);
        
        // Wait a moment before trying again
        setTimeout(() => {
            this.url = alternativeUrl;
            this.connect();
        }, 1500);
    }

    setupStompClient() {
        this.debug(`Setting up STOMP client...`);
        
        // Create StompJS client
        this.stompClient = new StompJs.Client({
            webSocketFactory: () => this.socket,
            debug: (str) => {
                // Filter and log important debug messages
                if (str.includes('CONNECTED') || 
                    str.includes('SUBSCRIBE') || 
                    str.includes('MESSAGE') ||
                    str.includes('ERROR') || 
                    str.includes('SEND') ||
                    str.includes('DISCONNECT')) {
                    this.debug(`[STOMP] ${str}`);
                }
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000
        });
        
        // STOMP connect handler
        this.stompClient.onConnect = (frame) => {
            this.debug(`STOMP connected successfully!`);
            if (this.onStatusChange) {
                this.onStatusChange('Connected');
            }
            ChatUIService.showNotification(`${this.role.charAt(0).toUpperCase() + this.role.slice(1)} connected.`);
            
            try {
                // Subscribe to chat room
                this.debug(`Subscribing to /topic/chat/${this.chatRoomId}`);
                this.stompClient.subscribe(`/topic/chat/${this.chatRoomId}`, (message) => {
                    try {
                        const messageData = JSON.parse(message.body);
                        this.debug(`Received message via STOMP: ${messageData.content}`);
                        
                        // Pass message to callback
                        if (this.onMessage) {
                            this.onMessage(messageData);
                        }
                    } catch (err) {
                        this.debug(`Error processing STOMP message: ${err.message}`);
                    }
                });
                
                // Subscribe to typing notifications
                this.debug(`Subscribing to /topic/chat/${this.chatRoomId}/typing`);
                this.stompClient.subscribe(`/topic/chat/${this.chatRoomId}/typing`, (notification) => {
                    try {
                        const typingData = JSON.parse(notification.body);
                        this.debug(`Typing notification from: ${typingData.username}`);
                        
                        // Pass typing notification to callback
                        if (this.onTyping) {
                            this.onTyping(typingData);
                        }
                    } catch (err) {
                        this.debug(`Error processing typing notification: ${err.message}`);
                    }
                });
                
                // Subscribe to read receipts
                this.debug(`Subscribing to /user/queue/read-receipts`);
                this.stompClient.subscribe('/user/queue/read-receipts', (receipt) => {
                    this.debug(`Read receipt received: ${receipt.body}`);
                });
                
                // Send a test message to ensure connection is working
                this.stompClient.publish({
                    destination: `/app/chat/${this.chatRoomId}/test`,
                    body: JSON.stringify({ type: "CONNECT_TEST" })
                });
                
                this.debug(`All subscriptions completed`);
            } catch (err) {
                this.debug(`Error in STOMP connect handler: ${err.message}`);
                console.error(`Error in STOMP connect handler:`, err);
            }
        };
        
        // STOMP error handler
        this.stompClient.onStompError = (frame) => {
            this.debug(`STOMP error: ${frame.headers.message}`);
            console.error(`STOMP error:`, frame.headers.message);
            if (this.onStatusChange) {
                this.onStatusChange('Error');
            }
        };
        
        // Activate STOMP connection
        this.debug(`Activating STOMP connection...`);
        try {
            this.stompClient.activate();
            
            // Add a fallback in case onConnect isn't triggered
            setTimeout(() => {
                if (this.onStatusChange) {
                    this.onStatusChange('Connected*');
                }
            }, 5000); // 5 seconds timeout
        } catch (err) {
            this.debug(`Error activating STOMP: ${err.message}`);
            console.error(`Error activating STOMP:`, err);
            if (this.onStatusChange) {
                this.onStatusChange('Fallback');
            }
        }
    }

    disconnect() {
        if (this.stompClient) {
            if (typeof this.stompClient.deactivate === 'function') {
                this.stompClient.deactivate();
            } else if (typeof this.stompClient.disconnect === 'function') {
                this.stompClient.disconnect();
            }
            this.stompClient = null;
        }
        
        if (this.socket) {
            if (this.socket.readyState === WebSocket.OPEN || this.socket.readyState === WebSocket.CONNECTING) {
                this.socket.close();
            }
            this.socket = null;
        }
        
        if (this.onStatusChange) {
            this.onStatusChange('Disconnected');
        }
    }

    sendMessage(content, callbacks = {}) {
        const { onSuccess, onError } = callbacks;
        
        if (!content || !content.trim()) {
            this.debug(`Message content is empty, not sending`);
            return;
        }
        
        // Create a placeholder message for immediate feedback
        const placeholderMessage = {
            content: content,
            createdAt: new Date().toISOString(),
            status: 'SENDING',
            tempId: `temp-${Date.now()}`,
            sender: {
                name: this.role.charAt(0).toUpperCase() + this.role.slice(1),
                roles: this.getRolesByRoleName(this.role)
            }
        };
        
        // Call success callback with placeholder message
        if (onSuccess) {
            onSuccess(placeholderMessage);
        }
        
        // Try to send message via STOMP
        if (this.stompClient && this.stompClient.connected) {
            const destination = `/app/chat/${this.chatRoomId}/send`;
            
            try {
                this.debug(`Sending message to ${destination}: ${content}`);
                
                this.stompClient.publish({
                    destination: destination,
                    body: JSON.stringify({ content: content }),
                    headers: { 'content-type': 'application/json' }
                });
                
                this.debug(`Message sent successfully via STOMP`);
                return;
            } catch (error) {
                this.debug(`Error sending message via STOMP: ${error.message}`);
                console.error(`Error sending message via STOMP:`, error);
            }
        }
        
        // If STOMP fails, send via HTTP
        this.sendMessageViaHttp(content, placeholderMessage.tempId, { onSuccess, onError });
    }
    
    getRolesByRoleName(roleName) {
        switch (roleName.toLowerCase()) {
            case 'admin':
                return ['ADMIN', 'USER'];
            case 'seller':
                return ['SELLER', 'USER'];
            case 'user':
            default:
                return ['USER'];
        }
    }

    sendMessageViaHttp(content, tempMessageId, callbacks = {}) {
        const { onSuccess, onError } = callbacks;
        const serverUrl = this.url.replace('ws:', 'http:').split('/api/ws')[0];
        const apiUrl = `${serverUrl}/api/chat/rooms/${this.chatRoomId}/messages`;
        
        this.debug(`Sending message via HTTP API: ${apiUrl}`);
        
        fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${this.token}`
            },
            body: JSON.stringify({ content: content })
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error(`HTTP error: ${response.status} ${response.statusText}`);
            }
        })
        .then(data => {
            this.debug(`Message sent successfully via HTTP API, ID: ${data.id}`);
            
            // Create confirmed message
            const confirmedMessage = {
                id: data.id,
                content: content,
                createdAt: data.createdAt || new Date().toISOString(),
                status: 'SENT',
                tempId: tempMessageId,
                sender: {
                    id: data.sender?.id,
                    name: data.sender?.name || this.role.charAt(0).toUpperCase() + this.role.slice(1),
                    roles: data.sender?.roles || this.getRolesByRoleName(this.role)
                }
            };
            
            // Call success callback with confirmed message
            if (onSuccess) {
                onSuccess(confirmedMessage);
            }
        })
        .catch(error => {
            this.debug(`Error sending via HTTP API: ${error.message}`);
            console.error(`Error sending via HTTP API:`, error);
            
            // Create error message
            const errorMessage = {
                content: content,
                createdAt: new Date().toISOString(),
                status: 'ERROR',
                tempId: tempMessageId,
                sender: {
                    name: this.role.charAt(0).toUpperCase() + this.role.slice(1),
                    roles: this.getRolesByRoleName(this.role)
                }
            };
            
            // Call error callback with error message
            if (onError) {
                onError(errorMessage);
            }
        });
    }

    sendTypingIndicator(isTyping) {
        // Try to send via STOMP if connected
        if (this.stompClient && this.stompClient.connected) {
            const destination = `/app/chat/${this.chatRoomId}/typing`;
            
            try {
                this.debug(`Sending typing indicator (${isTyping ? 'start' : 'stop'}) to ${destination}`);
                
                this.stompClient.publish({
                    destination: destination,
                    body: JSON.stringify(isTyping)
                });
                
                return;
            } catch (error) {
                this.debug(`Error sending typing indicator via STOMP: ${error.message}`);
            }
        }
        
        // Try to send via direct WebSocket if STOMP fails
        if (this.socket && this.socket.readyState === WebSocket.OPEN) {
            try {
                const destination = `/app/chat/${this.chatRoomId}/typing`;
                const message = {
                    destination: destination,
                    body: JSON.stringify(isTyping)
                };
                
                this.socket.send(JSON.stringify(message));
                this.debug(`Typing indicator sent via direct WebSocket`);
            } catch (error) {
                this.debug(`Error sending typing indicator via direct WebSocket: ${error.message}`);
            }
        }
    }

    markMessageAsRead(messageId) {
        // Try to send via STOMP if connected
        if (this.stompClient && this.stompClient.connected) {
            try {
                this.debug(`Sending read receipt via STOMP for message: ${messageId}`);
                
                this.stompClient.publish({
                    destination: `/app/chat/${this.chatRoomId}/read/${messageId}`,
                    body: JSON.stringify({ messageId: messageId })
                });
                
                return;
            } catch (error) {
                this.debug(`Error sending read receipt via STOMP: ${error.message}`);
            }
        }
        
        // Try to send via direct WebSocket if STOMP fails
        if (this.socket && this.socket.readyState === WebSocket.OPEN) {
            try {
                const message = {
                    destination: `/app/chat/${this.chatRoomId}/read/${messageId}`,
                    body: JSON.stringify({ messageId: messageId })
                };
                
                this.socket.send(JSON.stringify(message));
                this.debug(`Read receipt sent via direct WebSocket`);
                return;
            } catch (error) {
                this.debug(`Error sending read receipt via direct WebSocket: ${error.message}`);
            }
        }
        
        // Fall back to HTTP request
        this.sendReadReceiptViaHttp(messageId);
    }

    sendReadReceiptViaHttp(messageId) {
        const serverUrl = this.url.replace('ws:', 'http:').split('/api/ws')[0];
        const apiUrl = `${serverUrl}/api/chat/messages/${messageId}/read`;
        
        this.debug(`Sending read receipt via HTTP API: ${apiUrl}`);
        
        fetch(apiUrl, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${this.token}`
            }
        })
        .then(response => {
            if (response.ok) {
                this.debug(`Read receipt sent successfully via HTTP API`);
            } else {
                this.debug(`HTTP API error: ${response.status} ${response.statusText}`);
            }
        })
        .catch(error => {
            this.debug(`Error sending read receipt via HTTP API: ${error.message}`);
        });
    }
} 