package com.prolaris.springboot.chatapp;

import tools.jackson.databind.ObjectMapper;
import com.prolaris.springboot.chatapp.auth.users.User;
import com.prolaris.springboot.chatapp.messages.ChatWebSocketHandler;
import com.prolaris.springboot.chatapp.messages.ConnectionManager;
import com.prolaris.springboot.chatapp.messages.Message;
import com.prolaris.springboot.chatapp.messages.MessageService;
import com.prolaris.springboot.chatapp.messages.dto.ChatMessage;
import com.prolaris.springboot.chatapp.rooms.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Chat WebSocket Handler Tests")
public class ChatWebSocketHandlerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private ConnectionManager connectionManager;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private ChatWebSocketHandler chatWebSocketHandler;

    private User testUser;
    private Room testRoom;
    private UUID roomId;
    private UUID userId;
    private UUID chatId;

    @BeforeEach
    public void setUp() {
        chatId = UUID.randomUUID();
        userId = UUID.randomUUID();
        roomId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .username("username")
                .email("email@mail.com")
                .password("encodedPassword")
                .build();

        testRoom = Room.builder()
                .id(roomId)
                .name("name")
                .description("description")
                .createdBy(testUser)
                .build();
    }

    @Test
    @DisplayName("Test the handleTextMessage method -> save the message and broadcast it to the room")
    public void testHandleTextMessage() throws Exception {
        String payload = """
                {
                    "type":"SEND",
                    "roomId": %s,
                    "content": "Hello World!"
                }
                """.formatted(roomId);

        ChatMessage chatMessage = new ChatMessage(ChatMessage.Type.SEND, roomId, "Hello World!");

        when(objectMapper.readValue(payload, ChatMessage.class)).thenReturn(chatMessage);
        when(connectionManager.getUserFromSession(session)).thenReturn(testUser);
        when(messageService.save(roomId, userId, "Hello World!")).thenReturn(
                Message.builder().id(chatId).room(testRoom).user(testUser).build()
        );
        when(objectMapper.writeValueAsString(any())).thenReturn("""
                {
                    "content": "Hello World!"
                }
                """);

        chatWebSocketHandler.handleTextMessage(session, new TextMessage(payload));

        verify(messageService, times(1)).save(roomId, userId, "Hello World!");
        verify(connectionManager, times(1)).broadcastToRoom(eq(roomId), any());
    }

    @Test
    @DisplayName("Test the handleTextMessage method without user -> dose nothing")
    public void testHandleTextMessageWithoutUser() throws Exception {
        when(connectionManager.getUserFromSession(session)).thenReturn(null);

        chatWebSocketHandler.handleTextMessage(session, new TextMessage("{}"));

        verify(messageService, never()).save(any(), any(), any());
        verify(session, times(1)).close(any());
    }

    @Test
    @DisplayName("Test the afterConnectionClose method -> remove the session")
    public void testAfterConnectionClose() throws Exception {
        chatWebSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);
        verify(connectionManager, times(1)).removeSession(session);
    }

    @Test
    @DisplayName("Test the handleTransportError method -> remove the session from the ConnectionManager")
    void testHandleTransportError() throws Exception {
        chatWebSocketHandler.handleTransportError(session, new RuntimeException("network error"));
        verify(connectionManager, times(1)).removeSession(session);
    }

}
