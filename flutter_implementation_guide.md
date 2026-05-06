# Flutter Implementation Guide for NearProp Chat

This guide provides instructions for implementing the WebSocket chat functionality in your Flutter application.

## Setup Dependencies

Add the following dependencies to your `pubspec.yaml`:

```yaml
dependencies:
  flutter:
    sdk: flutter
  stomp_dart_client: ^0.4.4
  web_socket_channel: ^2.4.0
  provider: ^6.0.5
  shared_preferences: ^2.2.0
  http: ^1.1.0
```

## Authentication Service

Create an authentication service to handle JWT tokens:

```dart
// lib/services/auth_service.dart
import 'package:shared_preferences/shared_preferences.dart';

class AuthService {
  static const String _tokenKey = 'jwt_token';
  
  // Store the JWT token
  Future<void> setToken(String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_tokenKey, token);
  }
  
  // Get the stored JWT token
  Future<String?> getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_tokenKey);
  }
  
  // Check if user is logged in
  Future<bool> isLoggedIn() async {
    final token = await getToken();
    return token != null && token.isNotEmpty;
  }
  
  // Logout
  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_tokenKey);
  }
}
```

## WebSocket Service

Implement a WebSocket service that handles connections and message processing:

```dart
// lib/services/websocket_service.dart
import 'dart:async';
import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:stomp_dart_client/stomp_dart_client.dart';
import '../models/chat_message.dart';
import 'auth_service.dart';

class WebSocketService with ChangeNotifier {
  static const String baseUrl = 'ws://your-api-base-url/api/ws';
  
  StompClient? _stompClient;
  final AuthService _authService = AuthService();
  bool _isConnected = false;
  
  // Connection status
  bool get isConnected => _isConnected;
  
  // Stream controllers for various message types
  final _messageController = StreamController<ChatMessage>.broadcast();
  final _typingController = StreamController<TypingIndicator>.broadcast();
  final _readReceiptController = StreamController<ReadReceipt>.broadcast();
  
  // Stream getters
  Stream<ChatMessage> get messageStream => _messageController.stream;
  Stream<TypingIndicator> get typingStream => _typingController.stream;
  Stream<ReadReceipt> get readReceiptStream => _readReceiptController.stream;
  
  // Connect to WebSocket
  Future<void> connect() async {
    if (_stompClient != null) {
      await disconnect();
    }
    
    try {
      final token = await _authService.getToken();
      if (token == null) {
        throw Exception('No authentication token found');
      }
      
      _stompClient = StompClient(
        config: StompConfig.SockJS(
          url: '$baseUrl?token=$token',
          onConnect: _onConnect,
          onWebSocketError: (error) => _onError(error.toString()),
          stompConnectHeaders: {'Authorization': 'Bearer $token'},
          webSocketConnectHeaders: {'Authorization': 'Bearer $token'},
        ),
      );
      
      _stompClient!.activate();
    } catch (e) {
      _onError('Connection error: $e');
    }
  }
  
  // Handle successful connection
  void _onConnect(StompFrame frame) {
    _isConnected = true;
    notifyListeners();
    
    // Subscribe to read receipts for the current user
    _stompClient!.subscribe(
      destination: '/user/queue/read-receipts',
      callback: (frame) {
        final data = jsonDecode(frame.body!);
        _readReceiptController.add(ReadReceipt.fromJson(data));
      },
    );
    
    print('WebSocket connected successfully');
  }
  
  // Subscribe to a chat room
  void subscribeToChatRoom(int chatRoomId) {
    if (!_isConnected || _stompClient == null) {
      throw Exception('WebSocket not connected');
    }
    
    // Subscribe to chat messages
    _stompClient!.subscribe(
      destination: '/topic/chat/$chatRoomId',
      callback: (frame) {
        final data = jsonDecode(frame.body!);
        _messageController.add(ChatMessage.fromJson(data));
      },
    );
    
    // Subscribe to typing indicators
    _stompClient!.subscribe(
      destination: '/topic/chat/$chatRoomId/typing',
      callback: (frame) {
        final data = jsonDecode(frame.body!);
        _typingController.add(TypingIndicator.fromJson(data));
      },
    );
    
    print('Subscribed to chat room: $chatRoomId');
  }
  
  // Send a chat message
  void sendMessage(int chatRoomId, String content) {
    if (!_isConnected || _stompClient == null) {
      throw Exception('WebSocket not connected');
    }
    
    _stompClient!.send(
      destination: '/app/chat/$chatRoomId/send',
      body: jsonEncode({'content': content}),
    );
  }
  
  // Send typing indicator
  void sendTypingIndicator(int chatRoomId, bool isTyping) {
    if (!_isConnected || _stompClient == null) {
      throw Exception('WebSocket not connected');
    }
    
    _stompClient!.send(
      destination: '/app/chat/$chatRoomId/typing',
      body: jsonEncode(isTyping),
    );
  }
  
  // Mark message as read
  void markMessageAsRead(int chatRoomId, int messageId) {
    if (!_isConnected || _stompClient == null) {
      throw Exception('WebSocket not connected');
    }
    
    _stompClient!.send(
      destination: '/app/chat/$chatRoomId/read/$messageId',
      body: jsonEncode({'messageId': messageId}),
    );
  }
  
  // Handle errors
  void _onError(String message) {
    print('WebSocket error: $message');
    _isConnected = false;
    notifyListeners();
  }
  
  // Disconnect from WebSocket
  Future<void> disconnect() async {
    _stompClient?.deactivate();
    _isConnected = false;
    notifyListeners();
    print('WebSocket disconnected');
  }
  
  @override
  void dispose() {
    disconnect();
    _messageController.close();
    _typingController.close();
    _readReceiptController.close();
    super.dispose();
  }
}
```

## Data Models

Create the necessary data models for chat:

```dart
// lib/models/chat_message.dart
class ChatMessage {
  final int id;
  final int chatRoomId;
  final String content;
  final DateTime createdAt;
  final String status;
  final bool isRead;
  final User sender;
  
  ChatMessage({
    required this.id,
    required this.chatRoomId,
    required this.content,
    required this.createdAt,
    required this.status,
    required this.isRead,
    required this.sender,
  });
  
  factory ChatMessage.fromJson(Map<String, dynamic> json) {
    return ChatMessage(
      id: json['id'],
      chatRoomId: json['chatRoomId'],
      content: json['content'],
      createdAt: DateTime.parse(json['createdAt']),
      status: json['status'] ?? 'SENT',
      isRead: json['readAt'] != null,
      sender: User.fromJson(json['sender']),
    );
  }
}

class User {
  final int id;
  final String name;
  final List<String> roles;
  
  User({
    required this.id,
    required this.name,
    required this.roles,
  });
  
  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      name: json['name'],
      roles: List<String>.from(json['roles'] ?? []),
    );
  }
  
  bool get isSeller => roles.contains('SELLER');
}

class TypingIndicator {
  final int userId;
  final String username;
  final List<String> roles;
  final bool isTyping;
  
  TypingIndicator({
    required this.userId,
    required this.username,
    required this.roles,
    required this.isTyping,
  });
  
  factory TypingIndicator.fromJson(Map<String, dynamic> json) {
    return TypingIndicator(
      userId: json['userId'],
      username: json['username'],
      roles: List<String>.from(json['roles'] ?? []),
      isTyping: json['isTyping'],
    );
  }
}

class ReadReceipt {
  final int messageId;
  final DateTime readAt;
  final int readByUserId;
  
  ReadReceipt({
    required this.messageId,
    required this.readAt,
    required this.readByUserId,
  });
  
  factory ReadReceipt.fromJson(Map<String, dynamic> json) {
    return ReadReceipt(
      messageId: json['messageId'],
      readAt: DateTime.parse(json['readAt']),
      readByUserId: json['readByUserId'],
    );
  }
}
```

## Chat Provider

Create a provider to manage chat state:

```dart
// lib/providers/chat_provider.dart
import 'dart:async';
import 'package:flutter/foundation.dart';
import '../models/chat_message.dart';
import '../services/websocket_service.dart';
import '../services/chat_service.dart';

class ChatProvider with ChangeNotifier {
  final WebSocketService _websocketService;
  final ChatService _chatService;
  
  int? _currentChatRoomId;
  List<ChatMessage> _messages = [];
  bool _isLoading = false;
  String? _typingUser;
  
  StreamSubscription? _messageSubscription;
  StreamSubscription? _typingSubscription;
  StreamSubscription? _readReceiptSubscription;
  
  ChatProvider({
    required WebSocketService websocketService,
    required ChatService chatService,
  }) : 
    _websocketService = websocketService,
    _chatService = chatService {
    _subscribeToStreams();
  }
  
  // Getters
  List<ChatMessage> get messages => _messages;
  bool get isLoading => _isLoading;
  String? get typingUser => _typingUser;
  int? get currentChatRoomId => _currentChatRoomId;
  
  // Subscribe to WebSocket streams
  void _subscribeToStreams() {
    _messageSubscription = _websocketService.messageStream.listen(_handleNewMessage);
    _typingSubscription = _websocketService.typingStream.listen(_handleTypingIndicator);
    _readReceiptSubscription = _websocketService.readReceiptStream.listen(_handleReadReceipt);
  }
  
  // Load chat room
  Future<void> loadChatRoom(int chatRoomId) async {
    _isLoading = true;
    _currentChatRoomId = chatRoomId;
    notifyListeners();
    
    try {
      // Load messages from API
      final messages = await _chatService.getChatMessages(chatRoomId);
      _messages = messages;
      
      // Subscribe to WebSocket topics for this chat room
      _websocketService.subscribeToChatRoom(chatRoomId);
    } catch (e) {
      print('Error loading chat room: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
  
  // Send a message
  Future<void> sendMessage(String content) async {
    if (_currentChatRoomId == null) return;
    
    try {
      // Add optimistic message
      final tempMessage = ChatMessage(
        id: -1, // Temporary ID
        chatRoomId: _currentChatRoomId!,
        content: content,
        createdAt: DateTime.now(),
        status: 'SENDING',
        isRead: false,
        sender: await _chatService.getCurrentUser(),
      );
      
      _messages.add(tempMessage);
      notifyListeners();
      
      // Send via WebSocket
      _websocketService.sendMessage(_currentChatRoomId!, content);
      
      // If WebSocket fails, send via HTTP as fallback
      if (!_websocketService.isConnected) {
        await _chatService.sendMessage(_currentChatRoomId!, content);
      }
    } catch (e) {
      print('Error sending message: $e');
      // Update status to error
      final index = _messages.indexWhere((m) => 
        m.status == 'SENDING' && m.content == content);
      
      if (index != -1) {
        // Create new message with error status
        final errorMessage = ChatMessage(
          id: _messages[index].id,
          chatRoomId: _messages[index].chatRoomId,
          content: _messages[index].content,
          createdAt: _messages[index].createdAt,
          status: 'ERROR',
          isRead: _messages[index].isRead,
          sender: _messages[index].sender,
        );
        
        _messages[index] = errorMessage;
        notifyListeners();
      }
    }
  }
  
  // Send typing indicator
  void sendTypingIndicator(bool isTyping) {
    if (_currentChatRoomId == null) return;
    _websocketService.sendTypingIndicator(_currentChatRoomId!, isTyping);
  }
  
  // Mark message as read
  void markMessageAsRead(int messageId) {
    if (_currentChatRoomId == null) return;
    _websocketService.markMessageAsRead(_currentChatRoomId!, messageId);
    
    // Update local message
    final index = _messages.indexWhere((m) => m.id == messageId);
    if (index != -1) {
      // Create a new message with isRead set to true
      final updatedMessage = ChatMessage(
        id: _messages[index].id,
        chatRoomId: _messages[index].chatRoomId,
        content: _messages[index].content,
        createdAt: _messages[index].createdAt,
        status: _messages[index].status,
        isRead: true,
        sender: _messages[index].sender,
      );
      
      _messages[index] = updatedMessage;
      notifyListeners();
    }
  }
  
  // Handle incoming message
  void _handleNewMessage(ChatMessage message) {
    if (message.chatRoomId != _currentChatRoomId) return;
    
    // Check if we already have this message (for duplicate prevention)
    final existingIndex = _messages.indexWhere((m) => 
      m.id == message.id || 
      (m.content == message.content && 
       m.sender.id == message.sender.id &&
       m.createdAt.difference(message.createdAt).inSeconds.abs() < 5)
    );
    
    if (existingIndex != -1) {
      // Update existing message
      _messages[existingIndex] = message;
    } else {
      // Add new message
      _messages.add(message);
    }
    
    notifyListeners();
  }
  
  // Handle typing indicator
  void _handleTypingIndicator(TypingIndicator indicator) {
    if (indicator.isTyping) {
      _typingUser = indicator.username;
    } else if (_typingUser == indicator.username) {
      _typingUser = null;
    }
    
    notifyListeners();
  }
  
  // Handle read receipt
  void _handleReadReceipt(ReadReceipt receipt) {
    final index = _messages.indexWhere((m) => m.id == receipt.messageId);
    if (index != -1) {
      // Create a new message with isRead set to true
      final updatedMessage = ChatMessage(
        id: _messages[index].id,
        chatRoomId: _messages[index].chatRoomId,
        content: _messages[index].content,
        createdAt: _messages[index].createdAt,
        status: _messages[index].status,
        isRead: true,
        sender: _messages[index].sender,
      );
      
      _messages[index] = updatedMessage;
      notifyListeners();
    }
  }
  
  @override
  void dispose() {
    _messageSubscription?.cancel();
    _typingSubscription?.cancel();
    _readReceiptSubscription?.cancel();
    super.dispose();
  }
}
```

## HTTP Chat Service

Create a service to handle HTTP API calls as fallback for WebSocket:

```dart
// lib/services/chat_service.dart
import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/chat_message.dart';
import 'auth_service.dart';

class ChatService {
  static const String baseUrl = 'https://your-api-base-url/api';
  final AuthService _authService = AuthService();
  
  // Get chat messages from API
  Future<List<ChatMessage>> getChatMessages(int chatRoomId, {int page = 0, int size = 20}) async {
    final token = await _authService.getToken();
    final response = await http.get(
      Uri.parse('$baseUrl/chat/rooms/$chatRoomId/messages?page=$page&size=$size'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
    
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return (data['content'] as List)
        .map((json) => ChatMessage.fromJson(json))
        .toList();
    } else {
      throw Exception('Failed to load chat messages: ${response.statusCode}');
    }
  }
  
  // Send message via HTTP API (fallback)
  Future<ChatMessage> sendMessage(int chatRoomId, String content) async {
    final token = await _authService.getToken();
    final response = await http.post(
      Uri.parse('$baseUrl/chat/rooms/$chatRoomId/messages'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
      body: jsonEncode({'content': content}),
    );
    
    if (response.statusCode == 201) {
      return ChatMessage.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to send message: ${response.statusCode}');
    }
  }
  
  // Mark message as read via HTTP API (fallback)
  Future<void> markMessageAsRead(int messageId) async {
    final token = await _authService.getToken();
    final response = await http.put(
      Uri.parse('$baseUrl/chat/messages/$messageId/read'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
    
    if (response.statusCode != 200) {
      throw Exception('Failed to mark message as read: ${response.statusCode}');
    }
  }
  
  // Get current user information
  Future<User> getCurrentUser() async {
    final token = await _authService.getToken();
    final response = await http.get(
      Uri.parse('$baseUrl/users/me'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
    
    if (response.statusCode == 200) {
      return User.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to get current user: ${response.statusCode}');
    }
  }
}
```

## Chat Screen Implementation

Create the chat screen UI:

```dart
// lib/screens/chat_screen.dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/chat_message.dart';
import '../providers/chat_provider.dart';
import '../services/websocket_service.dart';

class ChatScreen extends StatefulWidget {
  final int chatRoomId;
  final String title;
  
  const ChatScreen({
    Key? key,
    required this.chatRoomId,
    required this.title,
  }) : super(key: key);
  
  @override
  _ChatScreenState createState() => _ChatScreenState();
}

class _ChatScreenState extends State<ChatScreen> {
  final TextEditingController _messageController = TextEditingController();
  final ScrollController _scrollController = ScrollController();
  bool _isTyping = false;
  Timer? _typingTimer;
  
  @override
  void initState() {
    super.initState();
    // Connect to WebSocket if not already connected
    final websocketService = Provider.of<WebSocketService>(context, listen: false);
    if (!websocketService.isConnected) {
      websocketService.connect();
    }
    
    // Load chat room
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<ChatProvider>(context, listen: false).loadChatRoom(widget.chatRoomId);
    });
  }
  
  @override
  void dispose() {
    _messageController.dispose();
    _scrollController.dispose();
    _typingTimer?.cancel();
    super.dispose();
  }
  
  // Send typing indicator
  void _handleTyping(String text) {
    final chatProvider = Provider.of<ChatProvider>(context, listen: false);
    
    if (text.isNotEmpty && !_isTyping) {
      _isTyping = true;
      chatProvider.sendTypingIndicator(true);
    }
    
    // Reset typing timer
    _typingTimer?.cancel();
    _typingTimer = Timer(const Duration(seconds: 2), () {
      if (_isTyping) {
        _isTyping = false;
        chatProvider.sendTypingIndicator(false);
      }
    });
  }
  
  // Send message
  void _sendMessage() {
    final text = _messageController.text.trim();
    if (text.isEmpty) return;
    
    final chatProvider = Provider.of<ChatProvider>(context, listen: false);
    chatProvider.sendMessage(text);
    
    // Clear input and typing indicator
    _messageController.clear();
    _isTyping = false;
    chatProvider.sendTypingIndicator(false);
    _typingTimer?.cancel();
    
    // Scroll to bottom
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Consumer<ChatProvider>(
        builder: (context, chatProvider, child) {
          if (chatProvider.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }
          
          return Column(
            children: [
              // Typing indicator
              if (chatProvider.typingUser != null)
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  alignment: Alignment.centerLeft,
                  child: Text(
                    '${chatProvider.typingUser} is typing...',
                    style: TextStyle(
                      color: Colors.grey[600],
                      fontStyle: FontStyle.italic,
                    ),
                  ),
                ),
              
              // Messages list
              Expanded(
                child: ListView.builder(
                  controller: _scrollController,
                  padding: const EdgeInsets.all(16),
                  itemCount: chatProvider.messages.length,
                  itemBuilder: (context, index) {
                    final message = chatProvider.messages[index];
                    return _buildMessageBubble(message);
                  },
                ),
              ),
              
              // Input area
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8),
                decoration: BoxDecoration(
                  color: Colors.white,
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black12,
                      blurRadius: 5,
                      offset: const Offset(0, -1),
                    ),
                  ],
                ),
                child: Row(
                  children: [
                    // Text input
                    Expanded(
                      child: TextField(
                        controller: _messageController,
                        decoration: const InputDecoration(
                          hintText: 'Type a message...',
                          border: InputBorder.none,
                          contentPadding: EdgeInsets.all(16),
                        ),
                        onChanged: _handleTyping,
                        onSubmitted: (_) => _sendMessage(),
                      ),
                    ),
                    
                    // Send button
                    IconButton(
                      icon: const Icon(Icons.send),
                      color: Theme.of(context).primaryColor,
                      onPressed: _sendMessage,
                    ),
                  ],
                ),
              ),
            ],
          );
        },
      ),
    );
  }
  
  Widget _buildMessageBubble(ChatMessage message) {
    final chatProvider = Provider.of<ChatProvider>(context, listen: false);
    final isMine = !message.sender.isSeller; // Adjust based on your user role check
    
    return Align(
      alignment: isMine ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        margin: const EdgeInsets.symmetric(vertical: 4),
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: isMine 
            ? Theme.of(context).primaryColor.withOpacity(0.8)
            : (message.sender.isSeller 
                ? Colors.blue.withOpacity(0.7) 
                : Colors.grey[300]),
          borderRadius: BorderRadius.circular(16),
        ),
        constraints: BoxConstraints(
          maxWidth: MediaQuery.of(context).size.width * 0.75,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Sender name for received messages
            if (!isMine)
              Padding(
                padding: const EdgeInsets.only(bottom: 4),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text(
                      message.sender.name,
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        color: isMine ? Colors.white : Colors.black87,
                      ),
                    ),
                    if (message.sender.isSeller)
                      Container(
                        margin: const EdgeInsets.only(left: 4),
                        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                        decoration: BoxDecoration(
                          color: Colors.blue[800],
                          borderRadius: BorderRadius.circular(10),
                        ),
                        child: const Text(
                          'SELLER',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 10,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                  ],
                ),
              ),
            
            // Message content
            Text(
              message.content,
              style: TextStyle(
                color: isMine ? Colors.white : Colors.black87,
              ),
            ),
            
            // Message metadata
            Row(
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                Text(
                  _formatTime(message.createdAt),
                  style: TextStyle(
                    fontSize: 10,
                    color: isMine ? Colors.white70 : Colors.black54,
                  ),
                ),
                const SizedBox(width: 4),
                Text(
                  message.status,
                  style: TextStyle(
                    fontSize: 10,
                    color: isMine ? Colors.white70 : Colors.black54,
                  ),
                ),
                const SizedBox(width: 4),
                if (message.isRead)
                  Icon(
                    Icons.done_all,
                    size: 12,
                    color: isMine ? Colors.white70 : Colors.black54,
                  ),
              ],
            ),
            
            // Read button for received messages
            if (!isMine && !message.isRead)
              TextButton(
                onPressed: () => chatProvider.markMessageAsRead(message.id),
                child: const Text('Mark as Read'),
                style: TextButton.styleFrom(
                  minimumSize: Size.zero,
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  tapTargetSize: MaterialTapTargetSize.shrinkWrap,
                ),
              ),
          ],
        ),
      ),
    );
  }
  
  String _formatTime(DateTime dateTime) {
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final messageDate = DateTime(dateTime.year, dateTime.month, dateTime.day);
    
    if (messageDate == today) {
      return '${dateTime.hour.toString().padLeft(2, '0')}:${dateTime.minute.toString().padLeft(2, '0')}';
    } else {
      return '${dateTime.day}/${dateTime.month} ${dateTime.hour.toString().padLeft(2, '0')}:${dateTime.minute.toString().padLeft(2, '0')}';
    }
  }
}
```

## Setting Up Providers

Register the providers in your app:

```dart
// lib/main.dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'services/auth_service.dart';
import 'services/websocket_service.dart';
import 'services/chat_service.dart';
import 'providers/chat_provider.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        // Services
        Provider(create: (_) => AuthService()),
        ChangeNotifierProvider(create: (_) => WebSocketService()),
        Provider(create: (_) => ChatService()),
        
        // Providers that depend on services
        ChangeNotifierProxyProvider2<WebSocketService, ChatService, ChatProvider>(
          create: (context) => ChatProvider(
            websocketService: context.read<WebSocketService>(),
            chatService: context.read<ChatService>(),
          ),
          update: (context, websocketService, chatService, previous) => 
            previous ?? ChatProvider(
              websocketService: websocketService,
              chatService: chatService,
            ),
        ),
      ],
      child: MaterialApp(
        title: 'NearProp Chat',
        theme: ThemeData(
          primarySwatch: Colors.green,
          visualDensity: VisualDensity.adaptivePlatformDensity,
        ),
        home: const LoginScreen(), // Create your login screen
      ),
    );
  }
}
```

## Key Implementation Notes

1. **WebSocket Connection**: The app establishes a WebSocket connection using the JWT token for authentication.

2. **Error Handling**: The implementation includes fallback mechanisms to send messages via HTTP API if WebSocket fails.

3. **Message Status Updates**: Messages display different statuses (SENDING, SENT, ERROR, READ) and update in real-time.

4. **Role-Based UI**: The chat interface distinguishes between buyer and seller messages.

5. **Read Receipts**: Users can mark messages as read, and read status is shown to senders.

6. **Typing Indicators**: Real-time typing indicators show when the other party is typing.

7. **Optimistic Updates**: Messages appear immediately in the UI before server confirmation.

## Testing the Implementation

1. Run the app and log in with a user account
2. Navigate to a property listing and start a chat with the property owner
3. Send messages and verify they appear in both interfaces
4. Test typing indicators by typing in the message field
5. Mark messages as read and verify the read status updates
6. Test offline functionality by disabling network connectivity

## Troubleshooting

1. **Connection Issues**: If WebSocket connections fail, check your server URL and ensure the token is valid.

2. **CORS Errors**: Make sure your server allows WebSocket connections from your app's domain.

3. **Message Duplicates**: If messages appear multiple times, check the duplicate detection logic in the chat provider.

4. **Authentication Problems**: Verify that your JWT token is correctly passed in the WebSocket connection URL. 