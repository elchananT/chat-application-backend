package com.prolaris.springboot.chatapp.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolaris.springboot.chatapp.auth.security.jwt.JwtService;
import com.prolaris.springboot.chatapp.auth.users.User;
import com.prolaris.springboot.chatapp.auth.users.UserRepository;
import com.prolaris.springboot.chatapp.messages.dto.ChatMessage;
import com.prolaris.springboot.chatapp.messages.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final ConnectionManager connectionManager;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            String query = getQuery(session);
            String token = extractParam(query, "token");
            UUID roomId = UUID.fromString(extractParam(query, "roomId"));
            String username = jwtService.getUsernameFromToken(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (!jwtService.isValidToken(token, user)) {
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }

            connectionManager.addSession(session, user,  roomId);

        } catch (Exception e) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        User user = connectionManager.getUserFromSession(session);

        if (user == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        if (chatMessage.type().equals(ChatMessage.Type.SEND)) {
            Message saved = messageService.save(
                    chatMessage.roomId(), user.getId(), chatMessage.content()
            );

            ChatMessageResponse response = new ChatMessageResponse(
                    saved.getId(),
                    chatMessage.roomId(),
                    user.getId(),
                    user.getUsername(),
                    saved.getContent(),
                    saved.getSentAt()
            );

            connectionManager.broadcastToRoom(chatMessage.roomId(), objectMapper.writeValueAsString(response));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        connectionManager.removeSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        connectionManager.removeSession(session);
    }

    private String getQuery(WebSocketSession session) {
        URI uri = session.getUri();
        return uri != null && uri.getQuery() != null ? uri.getQuery() : "";
    }

    private String extractParam(String query, String param) {
        for (String pair : query.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2 && keyValue[0].equals(param)) {
                return keyValue[1];
            }
        }
        return "";
    }

}
