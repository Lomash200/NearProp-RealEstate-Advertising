let stompClient = null;
let token = '';
let chatRoomId = '';
let userId = null;

const log = (msg) => {
  const area = document.getElementById('logArea');
  area.textContent += `[${new Date().toLocaleTimeString()}] ${msg}\n`;
  area.scrollTop = area.scrollHeight;
  console.log(msg);
};

const showChat = (show) => {
  document.querySelector('.chat-section').style.display = show ? '' : 'none';
  document.querySelector('.login-panel').style.display = show ? 'none' : '';
};

document.getElementById('connectBtn').onclick = async () => {
  token = document.getElementById('tokenInput').value.trim();
  chatRoomId = document.getElementById('chatRoomIdInput').value.trim();
  if (!token || !chatRoomId) {
    log('Token and Chat Room ID required.');
    return;
  }
  log('Connecting to WebSocket...');
  stompClient = new StompJs.Client({
    brokerURL: `ws://localhost:8080/api/ws?token=${token}`,
    connectHeaders: {},
    debug: (str) => log('[STOMP] ' + str),
    reconnectDelay: 5000,
    onConnect: async () => {
      log('WebSocket connected. Subscribing to chat topic...');
      stompClient.subscribe(`/topic/chat/${chatRoomId}`, (msg) => {
        const data = JSON.parse(msg.body);
        log('Received message: ' + msg.body);
        addMessage(data, data.sender && data.sender.id == userId ? 'sent' : 'received');
      });
      // Fetch userId from /v1/users/profile
      try {
        const res = await fetch('http://localhost:8080/v1/users/profile', { headers: { Authorization: `Bearer ${token}` } });
        const profile = await res.json();
        userId = profile.data.id;
        log('Fetched user profile. User ID: ' + userId);
      } catch (e) {
        log('Failed to fetch user profile: ' + e);
      }
      // Fetch chat history
      fetchMessages();
      showChat(true);
    },
    onStompError: (frame) => log('STOMP error: ' + frame.headers['message']),
    onWebSocketError: (evt) => log('WebSocket error: ' + evt),
    onDisconnect: () => { log('WebSocket disconnected.'); showChat(false); },
  });
  stompClient.activate();
};

document.getElementById('disconnectBtn').onclick = () => {
  if (stompClient) stompClient.deactivate();
  showChat(false);
  log('Disconnected.');
};

document.getElementById('sendBtn').onclick = () => {
  const content = document.getElementById('messageInput').value.trim();
  if (!content) return;
  const msg = { content };
  stompClient.publish({
    destination: `/app/chat/${chatRoomId}/send`,
    body: JSON.stringify(msg),
    headers: { Authorization: `Bearer ${token}` },
  });
  log('Sent message: ' + content);
  document.getElementById('messageInput').value = '';
};

function addMessage(data, type) {
  const messages = document.getElementById('messages');
  const div = document.createElement('div');
  div.className = 'message ' + type;
  const bubble = document.createElement('div');
  bubble.className = 'bubble';
  bubble.textContent = (data.sender ? data.sender.name + ': ' : '') + data.content;
  div.appendChild(bubble);
  messages.appendChild(div);
  messages.scrollTop = messages.scrollHeight;
}

async function fetchMessages() {
  try {
    const res = await fetch(`http://localhost:8080/chat/rooms/${chatRoomId}/messages?page=0&size=50`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    const result = await res.json();
    if (result && result.content) {
      document.getElementById('messages').innerHTML = '';
      result.content.forEach(msg => addMessage(msg, msg.sender && msg.sender.id == userId ? 'sent' : 'received'));
      log('Loaded chat history.');
    }
  } catch (e) {
    log('Failed to fetch messages: ' + e);
  }
} 