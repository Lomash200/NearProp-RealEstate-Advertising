document.addEventListener('DOMContentLoaded', () => {
    // Initialize app controller
    const app = new ChatApp();
    app.init();
});

class ChatApp {
    constructor() {
        // Services
        this.userWebSocketService = null;
        this.sellerWebSocketService = null;
        this.adminWebSocketService = null;
        
        this.userChatUI = null;
        this.sellerChatUI = null;
        this.adminChatUI = null;
        
        this.userApiService = null;
        this.sellerApiService = null;
        this.adminApiService = null;
        
        // State
        this.currentChatRoomId = null;
        this.serverBaseUrl = null;
        this.wsBaseUrl = null;
    }
    
    init() {
        // Initialize DOM references
        this.initDomReferences();
        
        // Setup event listeners
        this.setupEventListeners();
        
        // Load saved tokens from localStorage if available
        this.loadSavedTokens();
        
        // Initialize tabs
        this.initTabs();
    }
    
    initDomReferences() {
        // Connection panel
        this.chatRoomIdInput = document.getElementById('chatRoomId');
        this.serverUrlInput = document.getElementById('serverUrl');
        this.userTokenInput = document.getElementById('userToken');
        this.sellerTokenInput = document.getElementById('sellerToken');
        this.adminTokenInput = document.getElementById('adminToken');
        this.propertyIdInput = document.getElementById('propertyId');
        this.connectBtn = document.getElementById('connectBtn');
        this.connectBuyerSellerBtn = document.getElementById('connectBuyerSellerBtn');
        this.disconnectBtn = document.getElementById('disconnectBtn');
        this.createRoomBtn = document.getElementById('createRoomBtn');
        
        // Status badges
        this.userStatus = document.getElementById('userStatus');
        this.sellerStatus = document.getElementById('sellerStatus');
        this.adminStatus = document.getElementById('adminStatus');
        
        // Debug log
        this.debugLog = document.getElementById('debugLog');
        this.clearDebugBtn = document.getElementById('clearDebugBtn');
        
        // User chat panel
        this.userMessages = document.getElementById('userMessages');
        this.userInput = document.getElementById('userInput');
        this.userSendBtn = document.getElementById('userSendBtn');
        this.userTypingBtn = document.getElementById('userTypingBtn');
        this.userTypingStopBtn = document.getElementById('userTypingStopBtn');
        this.userTypingIndicator = document.getElementById('userTyping');
        
        // Seller chat panel
        this.sellerMessages = document.getElementById('sellerMessages');
        this.sellerInput = document.getElementById('sellerInput');
        this.sellerSendBtn = document.getElementById('sellerSendBtn');
        this.sellerTypingBtn = document.getElementById('sellerTypingBtn');
        this.sellerTypingStopBtn = document.getElementById('sellerTypingStopBtn');
        this.sellerTypingIndicator = document.getElementById('sellerTyping');
        
        // Admin chat panel
        this.adminMessages = document.getElementById('adminMessages');
        this.adminInput = document.getElementById('adminInput');
        this.adminSendBtn = document.getElementById('adminSendBtn');
        this.adminTypingBtn = document.getElementById('adminTypingBtn');
        this.adminTypingStopBtn = document.getElementById('adminTypingStopBtn');
        this.adminTypingIndicator = document.getElementById('adminTyping');
        
        // API testing panel
        this.apiMethodSelect = document.getElementById('apiMethodSelect');
        this.apiEndpointInput = document.getElementById('apiEndpointInput');
        this.apiParamsInput = document.getElementById('apiParamsInput');
        this.apiBodyInput = document.getElementById('apiBodyInput');
        this.apiRoleSelect = document.getElementById('apiRoleSelect');
        this.apiSendBtn = document.getElementById('apiSendBtn');
        this.apiResponseArea = document.getElementById('apiResponseArea');
    }
    
    setupEventListeners() {
        // Connection buttons
        this.connectBtn.addEventListener('click', () => this.connectAll());
        this.disconnectBtn.addEventListener('click', () => this.disconnectAll());
        this.createRoomBtn.addEventListener('click', () => this.createChatRoom());
        
        // Debug log clear button
        
        this.clearDebugBtn.addEventListener('click', () => {
            this.debugLog.innerHTML = '';
            this.debug('Debug log cleared');
        });
        
        // User chat buttons
        this.userSendBtn.addEventListener('click', () => this.sendMessage('user'));
        this.userTypingBtn.addEventListener('click', () => this.sendTypingIndicator('user', true));
        this.userTypingStopBtn.addEventListener('click', () => this.sendTypingIndicator('user', false));
        this.userInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage('user');
            }
        });
        
        // Seller chat buttons
        this.sellerSendBtn.addEventListener('click', () => this.sendMessage('seller'));
        this.sellerTypingBtn.addEventListener('click', () => this.sendTypingIndicator('seller', true));
        this.sellerTypingStopBtn.addEventListener('click', () => this.sendTypingIndicator('seller', false));
        this.sellerInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage('seller');
            }
        });
        
        // Admin chat buttons
        this.adminSendBtn.addEventListener('click', () => this.sendMessage('admin'));
        this.adminTypingBtn.addEventListener('click', () => this.sendTypingIndicator('admin', true));
        this.adminTypingStopBtn.addEventListener('click', () => this.sendTypingIndicator('admin', false));
        this.adminInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage('admin');
            }
        });
        
        // API testing buttons
        this.apiSendBtn.addEventListener('click', () => this.sendApiRequest());
        
        // Save tokens when changed
        this.userTokenInput.addEventListener('change', () => this.saveTokensToLocalStorage());
        this.sellerTokenInput.addEventListener('change', () => this.saveTokensToLocalStorage());
        this.adminTokenInput.addEventListener('change', () => this.saveTokensToLocalStorage());
        this.serverUrlInput.addEventListener('change', () => this.saveTokensToLocalStorage());
    }
    
    loadSavedTokens() {
        try {
            const savedTokens = localStorage.getItem('nearprop_chat_tokens');
            if (savedTokens) {
                const tokens = JSON.parse(savedTokens);
                if (tokens.userToken) this.userTokenInput.value = tokens.userToken;
                if (tokens.sellerToken) this.sellerTokenInput.value = tokens.sellerToken;
                if (tokens.adminToken) this.adminTokenInput.value = tokens.adminToken;
                if (tokens.serverUrl) this.serverUrlInput.value = tokens.serverUrl;
                if (tokens.chatRoomId) this.chatRoomIdInput.value = tokens.chatRoomId;
                console.log('[DEBUG] Loaded tokens from localStorage:', tokens);
            }
        } catch (err) {
            console.error('Error loading saved tokens:', err);
        }
    }
    
    saveTokensToLocalStorage() {
        try {
            const tokens = {
                userToken: this.userTokenInput.value,
                sellerToken: this.sellerTokenInput.value,
                adminToken: this.adminTokenInput.value,
                serverUrl: this.serverUrlInput.value,
                chatRoomId: this.chatRoomIdInput.value
            };
            localStorage.setItem('nearprop_chat_tokens', JSON.stringify(tokens));
        } catch (err) {
            console.error('Error saving tokens:', err);
        }
    }
    
    initTabs() {
        const tabs = document.querySelectorAll('.tab');
        const tabContents = document.querySelectorAll('.tab-content');
        
        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                // Remove active class from all tabs and contents
                tabs.forEach(t => t.classList.remove('active'));
                tabContents.forEach(c => c.classList.remove('active'));
                
                // Add active class to clicked tab and corresponding content
                tab.classList.add('active');
                const target = tab.getAttribute('data-tab');
                document.getElementById(target).classList.add('active');
            });
        });
        
        // Activate first tab by default
        if (tabs.length > 0) {
            tabs[0].click();
        }
    }
    
    connectAll() {
        const chatRoomId = this.chatRoomIdInput.value.trim();
        const serverUrl = this.serverUrlInput.value.trim();
        const userToken = this.userTokenInput.value.trim();
        const sellerToken = this.sellerTokenInput.value.trim();
        const adminToken = this.adminTokenInput.value.trim();
        
        console.log('[DEBUG] connectAll() tokens:', { userToken, sellerToken, adminToken });
        
        if (!chatRoomId || !serverUrl) {
            alert('Please provide chat room ID and server URL');
            return;
        }
        
        this.currentChatRoomId = chatRoomId;
        this.serverBaseUrl = serverUrl.replace('ws:', 'http:').split('/api/ws')[0];
        this.wsBaseUrl = serverUrl;
        
        this.saveTokensToLocalStorage();
        
        // Connect all roles that have tokens
        if (userToken) {
            this.connectUser(userToken);
        }
        
        if (sellerToken) {
            this.connectSeller(sellerToken);
        }
        
        if (adminToken) {
            this.connectAdmin(adminToken);
        }
        
        // Update UI
        this.connectBtn.disabled = true;
        this.disconnectBtn.disabled = false;
        
        // Initialize API services
        this.initApiServices();
    }
    
    initApiServices() {
        if (this.userTokenInput.value) {
            console.log('[DEBUG] Initializing userApiService with token:', this.userTokenInput.value);
            this.userApiService = new ApiService(this.serverBaseUrl, this.userTokenInput.value);
        }
        
        if (this.sellerTokenInput.value) {
            console.log('[DEBUG] Initializing sellerApiService with token:', this.sellerTokenInput.value);
            this.sellerApiService = new ApiService(this.serverBaseUrl, this.sellerTokenInput.value);
        }
        
        if (this.adminTokenInput.value) {
            console.log('[DEBUG] Initializing adminApiService with token:', this.adminTokenInput.value);
            this.adminApiService = new ApiService(this.serverBaseUrl, this.adminTokenInput.value);
        }
    }
    
    disconnectAll() {
        // Disconnect all services
        if (this.userWebSocketService) {
            this.userWebSocketService.disconnect();
            this.userWebSocketService = null;
        }
        
        if (this.sellerWebSocketService) {
            this.sellerWebSocketService.disconnect();
            this.sellerWebSocketService = null;
        }
        
        if (this.adminWebSocketService) {
            this.adminWebSocketService.disconnect();
            this.adminWebSocketService = null;
        }
        
        // Update connection status
        this.updateStatus('user', 'Disconnected');
        this.updateStatus('seller', 'Disconnected');
        this.updateStatus('admin', 'Disconnected');
        
        // Disable UI
        this.setUIEnabled('user', false);
        this.setUIEnabled('seller', false);
        this.setUIEnabled('admin', false);
        
        // Re-enable connect button
        this.connectBtn.disabled = false;
        this.disconnectBtn.disabled = true;
    }
    
    async createChatRoom() {
        const propertyId = this.propertyIdInput.value.trim();
        if (!propertyId) {
            alert('Please enter a property ID');
            return;
        }
        
        // Use user token by default, or any available token
        const token = this.userTokenInput.value || this.sellerTokenInput.value || this.adminTokenInput.value;
        if (!token) {
            alert('Please enter at least one token to create a chat room');
            return;
        }
        
        const serverUrl = this.serverUrlInput.value.trim();
        if (!serverUrl) {
            alert('Please enter the server URL');
            return;
        }
        
        const baseUrl = serverUrl.replace('ws:', 'http:').split('/api/ws')[0];
        const apiService = new ApiService(baseUrl, token);
        
        try {
            this.debug(`Creating chat room for property ID: ${propertyId}`);
            const response = await apiService.createChatRoom(propertyId);
            
            if (response.ok) {
                this.debug(`Chat room created: ${JSON.stringify(response.data)}`);
                // Set the created room ID in the input
                if (response.data && response.data.id) {
                    this.chatRoomIdInput.value = response.data.id;
                }
                alert(`Chat room created! ID: ${response.data.id}`);
            } else {
                this.debug(`Error creating chat room: ${response.status} ${response.statusText}`);
                alert(`Error creating chat room: ${response.status} ${response.statusText}`);
            }
        } catch (error) {
            this.debug(`Exception creating chat room: ${error.message}`);
            alert(`Error: ${error.message}`);
        }
    }
    
    connectUser(token) {
        console.log('[DEBUG] connectUser() with token:', token);
        this.debug(`[user] Connecting to ${this.wsBaseUrl} with chat room ${this.currentChatRoomId}`);
        
        // Create WebSocket service
        this.userWebSocketService = new WebSocketService('user', this.wsBaseUrl, token, this.currentChatRoomId);
        
        // Setup callbacks
        this.userWebSocketService.onDebug = (message) => this.debug(message);
        this.userWebSocketService.onStatusChange = (status) => this.updateStatus('user', status);
        this.userWebSocketService.onMessage = (message) => this.broadcastMessage(message);
        this.userWebSocketService.onTyping = (typingData) => this.broadcastTypingNotification(typingData);
        
        // Create UI service
        this.userChatUI = new ChatUIService('user', {
            input: this.userInput,
            sendBtn: this.userSendBtn,
            typingBtn: this.userTypingBtn, 
            typingStopBtn: this.userTypingStopBtn,
            messagesContainer: this.userMessages,
            typingIndicator: this.userTypingIndicator,
            statusBadge: this.userStatus
        });
        
        // Set callback for marking messages as read
        this.userChatUI.setCallbacks({
            onMarkRead: (messageId) => {
                if (this.userWebSocketService) {
                    this.userWebSocketService.markMessageAsRead(messageId);
                }
            }
        });
        
        // Connect
        this.userWebSocketService.connect();
        
        // Enable UI
        this.setUIEnabled('user', true);
    }
    
    connectSeller(token) {
        console.log('[DEBUG] connectSeller() with token:', token);
        this.debug(`[seller] Connecting to ${this.wsBaseUrl} with chat room ${this.currentChatRoomId}`);
        
        // Create WebSocket service
        this.sellerWebSocketService = new WebSocketService('seller', this.wsBaseUrl, token, this.currentChatRoomId);
        
        // Setup callbacks
        this.sellerWebSocketService.onDebug = (message) => this.debug(message);
        this.sellerWebSocketService.onStatusChange = (status) => this.updateStatus('seller', status);
        this.sellerWebSocketService.onMessage = (message) => this.broadcastMessage(message);
        this.sellerWebSocketService.onTyping = (typingData) => this.broadcastTypingNotification(typingData);
        
        // Create UI service
        this.sellerChatUI = new ChatUIService('seller', {
            input: this.sellerInput,
            sendBtn: this.sellerSendBtn,
            typingBtn: this.sellerTypingBtn, 
            typingStopBtn: this.sellerTypingStopBtn,
            messagesContainer: this.sellerMessages,
            typingIndicator: this.sellerTypingIndicator,
            statusBadge: this.sellerStatus
        });
        
        // Set callback for marking messages as read
        this.sellerChatUI.setCallbacks({
            onMarkRead: (messageId) => {
                if (this.sellerWebSocketService) {
                    this.sellerWebSocketService.markMessageAsRead(messageId);
                }
            }
        });
        
        // Connect
        this.sellerWebSocketService.connect();
        
        // Enable UI
        this.setUIEnabled('seller', true);
    }
    
    connectAdmin(token) {
        console.log('[DEBUG] connectAdmin() with token:', token);
        this.debug(`[admin] Connecting to ${this.wsBaseUrl} with chat room ${this.currentChatRoomId}`);
        
        // Create WebSocket service
        this.adminWebSocketService = new WebSocketService('admin', this.wsBaseUrl, token, this.currentChatRoomId);
        
        // Setup callbacks
        this.adminWebSocketService.onDebug = (message) => this.debug(message);
        this.adminWebSocketService.onStatusChange = (status) => this.updateStatus('admin', status);
        this.adminWebSocketService.onMessage = (message) => this.broadcastMessage(message);
        this.adminWebSocketService.onTyping = (typingData) => this.broadcastTypingNotification(typingData);
        
        // Create UI service
        this.adminChatUI = new ChatUIService('admin', {
            input: this.adminInput,
            sendBtn: this.adminSendBtn,
            typingBtn: this.adminTypingBtn, 
            typingStopBtn: this.adminTypingStopBtn,
            messagesContainer: this.adminMessages,
            typingIndicator: this.adminTypingIndicator,
            statusBadge: this.adminStatus
        });
        
        // Set callback for marking messages as read
        this.adminChatUI.setCallbacks({
            onMarkRead: (messageId) => {
                if (this.adminWebSocketService) {
                    this.adminWebSocketService.markMessageAsRead(messageId);
                }
            }
        });
        
        // Connect
        this.adminWebSocketService.connect();
        
        // Enable UI
        this.setUIEnabled('admin', true);
    }
    
    updateStatus(role, status) {
        switch (role) {
            case 'user':
                if (this.userChatUI) {
                    this.userChatUI.updateStatus(status);
                }
                break;
            case 'seller':
                if (this.sellerChatUI) {
                    this.sellerChatUI.updateStatus(status);
                }
                break;
            case 'admin':
                if (this.adminChatUI) {
                    this.adminChatUI.updateStatus(status);
                }
                break;
        }
    }
    
    setUIEnabled(role, enabled) {
        switch (role) {
            case 'user':
                if (this.userChatUI) {
                    this.userChatUI.setUIEnabled(enabled);
                } else {
                    this.userInput.disabled = !enabled;
                    this.userSendBtn.disabled = !enabled;
                    this.userTypingBtn.disabled = !enabled;
                    this.userTypingStopBtn.disabled = !enabled;
                }
                break;
            case 'seller':
                if (this.sellerChatUI) {
                    this.sellerChatUI.setUIEnabled(enabled);
                } else {
                    this.sellerInput.disabled = !enabled;
                    this.sellerSendBtn.disabled = !enabled;
                    this.sellerTypingBtn.disabled = !enabled;
                    this.sellerTypingStopBtn.disabled = !enabled;
                }
                break;
            case 'admin':
                if (this.adminChatUI) {
                    this.adminChatUI.setUIEnabled(enabled);
                } else {
                    this.adminInput.disabled = !enabled;
                    this.adminSendBtn.disabled = !enabled;
                    this.adminTypingBtn.disabled = !enabled;
                    this.adminTypingStopBtn.disabled = !enabled;
                }
                break;
        }
    }
    
    sendMessage(role) {
        let content, service, ui;
        
        switch (role) {
            case 'user':
                content = this.userChatUI ? this.userChatUI.getInputAndClear() : this.userInput.value.trim();
                service = this.userWebSocketService;
                ui = this.userChatUI;
                if (!this.userChatUI) this.userInput.value = '';
                break;
            case 'seller':
                content = this.sellerChatUI ? this.sellerChatUI.getInputAndClear() : this.sellerInput.value.trim();
                service = this.sellerWebSocketService;
                ui = this.sellerChatUI;
                if (!this.sellerChatUI) this.sellerInput.value = '';
                break;
            case 'admin':
                content = this.adminChatUI ? this.adminChatUI.getInputAndClear() : this.adminInput.value.trim();
                service = this.adminWebSocketService;
                ui = this.adminChatUI;
                if (!this.adminChatUI) this.adminInput.value = '';
                break;
        }
        
        if (!content || !service) return;
        
        // Send message
        service.sendMessage(content, {
            onSuccess: (message) => {
                this.debug(`[${role}] Message sent: ${message.content}`);
                // Display in all UIs for immediate feedback if not already handled by WebSocket
                this.broadcastMessage(message);
            },
            onError: (errorMessage) => {
                this.debug(`[${role}] Error sending message: ${errorMessage.status}`);
                // Display error in sender's UI
                if (ui) {
                    ui.displayMessage(errorMessage, true);
                }
            }
        });
    }
    
    sendTypingIndicator(role, isTyping) {
        let service, ui;
        
        switch (role) {
            case 'user':
                service = this.userWebSocketService;
                ui = this.userChatUI;
                break;
            case 'seller':
                service = this.sellerWebSocketService;
                ui = this.sellerChatUI;
                break;
            case 'admin':
                service = this.adminWebSocketService;
                ui = this.adminChatUI;
                break;
        }
        
        if (!service) return;
        
        // Update local UI
        if (ui) {
            ui.showLocalTypingIndicator(isTyping);
        }
        
        // Send typing indicator
        service.sendTypingIndicator(isTyping);
    }
    
    broadcastMessage(message) {
        // Display message in all UIs
        if (this.userChatUI) {
            this.userChatUI.displayMessage(message);
        }
        
        if (this.sellerChatUI) {
            this.sellerChatUI.displayMessage(message);
        }
        
        if (this.adminChatUI) {
            this.adminChatUI.displayMessage(message);
        }
    }
    
    broadcastTypingNotification(typingData) {
        // Update typing indicators in all UIs
        if (this.userChatUI) {
            this.userChatUI.handleTypingNotification(typingData);
        }
        
        if (this.sellerChatUI) {
            this.sellerChatUI.handleTypingNotification(typingData);
        }
        
        if (this.adminChatUI) {
            this.adminChatUI.handleTypingNotification(typingData);
        }
    }
    
    debug(message) {
        if (!this.debugLog) return;
        
        const now = new Date();
        const timestamp = now.toLocaleTimeString();
        const logEntry = document.createElement('div');
        logEntry.textContent = `[${timestamp}] ${message}`;
        this.debugLog.appendChild(logEntry);
        this.debugLog.scrollTop = this.debugLog.scrollHeight;
        console.log(`[DEBUG] ${message}`);
    }
    
    async sendApiRequest() {
        const method = this.apiMethodSelect.value;
        const endpoint = this.apiEndpointInput.value;
        const role = this.apiRoleSelect.value;
        
        if (!endpoint) {
            alert('Please enter an endpoint');
            return;
        }
        
        let params = null;
        let body = null;
        
        try {
            if (this.apiParamsInput.value.trim()) {
                params = JSON.parse(this.apiParamsInput.value);
            }
        } catch (e) {
            alert(`Invalid JSON in params: ${e.message}`);
            return;
        }
        
        try {
            if (this.apiBodyInput.value.trim()) {
                body = JSON.parse(this.apiBodyInput.value);
            }
        } catch (e) {
            alert(`Invalid JSON in body: ${e.message}`);
            return;
        }
        
        let apiService;
        switch (role) {
            case 'user':
                apiService = this.userApiService;
                break;
            case 'seller':
                apiService = this.sellerApiService;
                break;
            case 'admin':
                apiService = this.adminApiService;
                break;
        }
        
        if (!apiService) {
            alert(`No connection for ${role} role. Connect first.`);
            return;
        }
        
        try {
            this.debug(`API Request: ${method} ${endpoint} as ${role}`);
            this.apiResponseArea.textContent = 'Loading...';
            
            const response = await apiService.makeRequest(method, endpoint, body, params);
            
            // Display response
            this.apiResponseArea.textContent = JSON.stringify(response, null, 2);
            
            this.debug(`API Response: ${response.status} ${response.statusText}`);
        } catch (error) {
            this.debug(`API Error: ${error.message}`);
            this.apiResponseArea.textContent = `Error: ${error.message}`;
        }
    }
} 