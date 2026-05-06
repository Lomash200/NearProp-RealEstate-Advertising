class ChatUIService {
    constructor(role, domElements) {
        this.role = role;
        this.domElements = domElements;
        this.messages = new Map(); // Map to store messages by ID or tempId
        this.typingTimer = null;
    }
    
    // Enable or disable UI elements
    setUIEnabled(enabled) {
        const { input, sendBtn, typingBtn, typingStopBtn } = this.domElements;
        
        input.disabled = !enabled;
        sendBtn.disabled = !enabled;
        typingBtn.disabled = !enabled;
        typingStopBtn.disabled = !enabled;
    }
    
    // Update connection status badge
    updateStatus(status) {
        const { statusBadge } = this.domElements;
        if (!statusBadge) return;
        
        statusBadge.textContent = `${this.role.charAt(0).toUpperCase() + this.role.slice(1)}: ${status}`;
        
        // Update badge class
        statusBadge.className = 'badge';
        if (status === 'Connected' || status === 'Connected*') {
            statusBadge.classList.add('badge-success');
        } else if (status === 'Error') {
            statusBadge.classList.add('badge-danger');
        } else {
            statusBadge.classList.add('badge-warning');
        }
    }
    
    // Add or update a message in the chat
    displayMessage(message, isLocalEcho = false) {
        try {
            const { messagesContainer } = this.domElements;
            if (!messagesContainer) return;
            
            // Check if this is a status update for an existing message
            if (message.tempId) {
                const existingMsg = messagesContainer.querySelector(`[data-temp-id="${message.tempId}"]`);
                if (existingMsg) {
                    // Update the status of the existing message
                    const statusElement = existingMsg.querySelector('.message-status');
                    if (statusElement) {
                        statusElement.textContent = message.status || 'SENT';
                    }
                    
                    // If message now has a real ID, add it
                    if (message.id) {
                        existingMsg.setAttribute('data-message-id', message.id);
                        
                        // Store in our messages map
                        this.messages.set(message.id, message);
                    }
                    
                    return;
                }
            }
            
            // Check if this message already exists by id (to avoid duplicates)
            if (!isLocalEcho && message.id) {
                const existingMsg = messagesContainer.querySelector(`[data-message-id="${message.id}"]`);
                if (existingMsg) {
                    return;
                }
                
                // Store in our messages map
                this.messages.set(message.id, message);
            }
            
            // Check if this is essentially the same message content sent recently
            const recentMessages = Array.from(messagesContainer.querySelectorAll('.message'));
            const lastFewMessages = recentMessages.slice(-3); // Check last 3 messages
            
            for (const recentMsg of lastFewMessages) {
                const contentEl = recentMsg.querySelector('.message-content');
                if (contentEl && contentEl.textContent === message.content) {
                    const timeEl = recentMsg.querySelector('.message-time');
                    if (timeEl) {
                        const msgTime = new Date(timeEl.dataset.timestamp || 0);
                        const newMsgTime = new Date(message.createdAt);
                        const timeDiff = Math.abs(newMsgTime - msgTime);
                        
                        // If same content was sent within 2 seconds, likely a duplicate
                        if (timeDiff < 2000) {
                            return;
                        }
                    }
                }
            }
            
            // Create message element
            const messageDiv = document.createElement('div');
            messageDiv.classList.add('message');
            
            // Add message ID if available
            if (message.id) {
                messageDiv.setAttribute('data-message-id', message.id);
            }
            
            // Add temp ID for status updates
            if (message.tempId) {
                messageDiv.setAttribute('data-temp-id', message.tempId);
            }
            
            // Determine message styling based on sender role
            if (isLocalEcho || this.isMine(message)) {
                messageDiv.classList.add('sent');
                
                if (message.sender && message.sender.roles) {
                    if (message.sender.roles.includes('ADMIN')) {
                        messageDiv.classList.add('admin-message');
                    } else if (message.sender.roles.includes('SELLER')) {
                        messageDiv.classList.add('seller-message');
                    } else {
                        messageDiv.classList.add('user-message');
                    }
                }
            } else {
                messageDiv.classList.add('received');
                
                if (message.sender && message.sender.roles) {
                    if (message.sender.roles.includes('ADMIN')) {
                        messageDiv.classList.add('admin-message');
                    } else if (message.sender.roles.includes('SELLER')) {
                        messageDiv.classList.add('seller-message');
                    } else {
                        messageDiv.classList.add('user-message');
                    }
                }
            }
            
            // Build message content
            let content = '';
            
            // Show sender name for all messages
            const senderName = message.sender?.name || (
                message.sender?.roles?.includes('ADMIN') ? 'Admin' :
                message.sender?.roles?.includes('SELLER') ? 'Property Owner' : 'Buyer'
            );
            
            // Display sender name with role
            const roleBadge = message.sender?.roles ? 
                `<small style="color: #777;">(${message.sender.roles.join(', ')})</small>` : '';
            content += `<div class="message-sender">${senderName} ${roleBadge}</div>`;
            
            content += `<div class="message-content">${message.content}</div>`;
            
            // Add message metadata
            content += `<div class="message-meta">
                <span class="message-time" data-timestamp="${message.createdAt}">${new Date(message.createdAt).toLocaleTimeString()}</span> - 
                <span class="message-status">${message.status || 'SENT'}</span>
                ${message.edited ? ' (edited)' : ''}
                ${message.read ? ' (read)' : ''}
            </div>`;
            
            // Add message actions for received messages
            if (messageDiv.classList.contains('received') && !isLocalEcho && message.id) {
                content += `<div class="message-actions">
                    <button class="message-action-btn mark-read-btn" data-message-id="${message.id}">Mark as Read</button>
                </div>`;
            }
            
            messageDiv.innerHTML = content;
            
            // Add event listener for the read button
            const readBtn = messageDiv.querySelector('.mark-read-btn');
            if (readBtn) {
                readBtn.addEventListener('click', () => {
                    const msgId = readBtn.getAttribute('data-message-id');
                    if (msgId && this.onMarkRead) {
                        this.onMarkRead(msgId);
                        
                        // Update UI
                        const statusElement = messageDiv.querySelector('.message-status');
                        if (statusElement) {
                            statusElement.textContent = 'READ';
                        }
                        
                        // Disable the button after use
                        readBtn.disabled = true;
                        readBtn.textContent = 'Read';
                    }
                });
            }
            
            // Add to container
            messagesContainer.appendChild(messageDiv);
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        } catch (error) {
            console.error(`[${this.role}] Error displaying message:`, error);
        }
    }
    
    // Check if a message is from the current role
    isMine(message) {
        if (!message.sender || !message.sender.roles) {
            return false;
        }
        
        switch (this.role.toLowerCase()) {
            case 'admin':
                return message.sender.roles.includes('ADMIN');
            case 'seller':
                return message.sender.roles.includes('SELLER') && !message.sender.roles.includes('ADMIN');
            case 'user':
            default:
                return !message.sender.roles.includes('SELLER') && !message.sender.roles.includes('ADMIN');
        }
    }
    
    // Update typing indicator
    handleTypingNotification(typingData) {
        const { typingIndicator } = this.domElements;
        if (!typingIndicator) return;
        
        // Clear any existing timer
        if (this.typingTimer) {
            clearTimeout(this.typingTimer);
            this.typingTimer = null;
        }
        
        // Only show typing from other roles (not our own)
        if (this.isOtherRole(typingData) && typingData.isTyping) {
            const username = typingData.username || 'Someone';
            typingIndicator.textContent = `${username} is typing...`;
            
            // Auto-clear after 3 seconds
            this.typingTimer = setTimeout(() => {
                if (typingIndicator.textContent.includes('is typing')) {
                    typingIndicator.textContent = '';
                }
                this.typingTimer = null;
            }, 3000);
        } else if (!typingData.isTyping) {
            typingIndicator.textContent = '';
        }
    }
    
    // Check if typing notification is from another role
    isOtherRole(typingData) {
        if (!typingData.roles) {
            return true; // Assume it's from another role if no roles provided
        }
        
        switch (this.role.toLowerCase()) {
            case 'admin':
                return !typingData.roles.includes('ADMIN');
            case 'seller':
                return !typingData.roles.includes('SELLER');
            case 'user':
            default:
                return typingData.roles.includes('SELLER') || typingData.roles.includes('ADMIN');
        }
    }
    
    // Update local typing indicator when user starts typing
    showLocalTypingIndicator(isTyping) {
        const { typingIndicator } = this.domElements;
        if (!typingIndicator) return;
        
        if (isTyping) {
            typingIndicator.textContent = "You are typing...";
            
            // Auto-clear after 3 seconds
            if (this.typingTimer) {
                clearTimeout(this.typingTimer);
            }
            
            this.typingTimer = setTimeout(() => {
                if (typingIndicator.textContent === "You are typing...") {
                    typingIndicator.textContent = "";
                }
                this.typingTimer = null;
            }, 3000);
        } else {
            typingIndicator.textContent = "";
            
            if (this.typingTimer) {
                clearTimeout(this.typingTimer);
                this.typingTimer = null;
            }
        }
    }
    
    // Clear typing indicator
    clearTypingIndicator() {
        const { typingIndicator } = this.domElements;
        if (typingIndicator) {
            typingIndicator.textContent = '';
        }
        
        if (this.typingTimer) {
            clearTimeout(this.typingTimer);
            this.typingTimer = null;
        }
    }
    
    // Get input value and clear
    getInputAndClear() {
        const { input } = this.domElements;
        if (!input) return '';
        
        const value = input.value.trim();
        input.value = '';
        return value;
    }
    
    // Set callbacks
    setCallbacks(callbacks = {}) {
        this.onMarkRead = callbacks.onMarkRead || null;
    }

    static showNotification(message) {
        const notificationArea = document.getElementById('notificationArea');
        if (!notificationArea) return;

        const notification = document.createElement('div');
        notification.className = 'notification';
        notification.textContent = message;

        notificationArea.appendChild(notification);

        // Automatically remove the notification element after the animation ends
        setTimeout(() => {
            notification.remove();
        }, 5000); // Matches CSS animation
    }
} 