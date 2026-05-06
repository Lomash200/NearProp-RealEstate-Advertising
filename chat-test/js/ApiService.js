class ApiService {
    constructor(baseUrl, token) {
        this.baseUrl = baseUrl;
        this.token = token;
        console.log('[DEBUG] ApiService constructor: baseUrl=', baseUrl, 'token=', token);
    }

    setToken(token) {
        this.token = token;
    }

    setBaseUrl(baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    async makeRequest(method, endpoint, body = null, params = null) {
        try {
            console.log('[DEBUG] ApiService.makeRequest: token=', this.token, 'endpoint=', endpoint);
            const url = new URL(`${this.baseUrl}${endpoint}`);
            
            // Add query parameters if provided
            if (params) {
                Object.keys(params).forEach(key => {
                    if (params[key] !== null && params[key] !== undefined) {
                        url.searchParams.append(key, params[key]);
                    }
                });
            }
            
            const options = {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${this.token}`
                }
            };
            
            if (body && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
                options.body = JSON.stringify(body);
            }
            
            const response = await fetch(url, options);
            let data;
            
            try {
                // Try to parse as JSON, but don't fail if not JSON
                const text = await response.text();
                data = text ? JSON.parse(text) : {};
            } catch (e) {
                // Return raw response if not JSON
                data = { raw: await response.text() };
            }
            
            return {
                status: response.status,
                statusText: response.statusText,
                data: data,
                headers: Object.fromEntries(response.headers.entries()),
                ok: response.ok
            };
        } catch (error) {
            console.error(`API error (${endpoint}):`, error);
            return {
                status: -1,
                statusText: error.message,
                data: null,
                headers: {},
                ok: false,
                error: error.message
            };
        }
    }
    
    // Chat Room APIs
    
    // Get all chat rooms for current user
    async getChatRooms(params = {}) {
        return await this.makeRequest('GET', '/api/chat/rooms', null, params);
    }
    
    // Get a specific chat room
    async getChatRoom(roomId) {
        return await this.makeRequest('GET', `/api/chat/rooms/${roomId}`);
    }
    
    // Create a new chat room
    async createChatRoom(propertyId) {
        return await this.makeRequest('POST', '/api/chat/rooms', { propertyId });
    }
    
    // Close a chat room
    async closeChatRoom(roomId) {
        return await this.makeRequest('PATCH', `/api/chat/rooms/${roomId}/close`);
    }
    
    // Get chat room participants
    async getChatRoomParticipants(roomId) {
        return await this.makeRequest('GET', `/api/chat/rooms/${roomId}/participants`);
    }
    
    // Add participant to chat room (admin only)
    async addChatRoomParticipant(roomId, userId) {
        return await this.makeRequest('POST', `/api/chat/rooms/${roomId}/participants`, { userId });
    }
    
    // Remove participant from chat room (admin only)
    async removeChatRoomParticipant(roomId, userId) {
        return await this.makeRequest('DELETE', `/api/chat/rooms/${roomId}/participants/${userId}`);
    }
    
    // Chat Message APIs
    
    // Get messages from a chat room
    async getChatMessages(roomId, params = {}) {
        return await this.makeRequest('GET', `/api/chat/rooms/${roomId}/messages`, null, params);
    }
    
    // Send a message to a chat room
    async sendChatMessage(roomId, content) {
        return await this.makeRequest('POST', `/api/chat/rooms/${roomId}/messages`, { content });
    }
    
    // Edit a message
    async editChatMessage(messageId, content) {
        return await this.makeRequest('PUT', `/api/chat/messages/${messageId}`, { content });
    }
    
    // Delete a message
    async deleteChatMessage(messageId) {
        return await this.makeRequest('DELETE', `/api/chat/messages/${messageId}`);
    }
    
    // Mark a message as read
    async markMessageAsRead(messageId) {
        return await this.makeRequest('PUT', `/api/chat/messages/${messageId}/read`);
    }
    
    // Get unread message count
    async getUnreadMessageCount() {
        return await this.makeRequest('GET', '/api/chat/messages/unread/count');
    }
} 