package com.prolaris.springboot.chatapp.messages;

import com.prolaris.springboot.chatapp.auth.users.User;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionManager {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, User> sessionUsers = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> roomSessions = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session, User user, UUID roomId) {
        sessions.put(session.getId(), session);
        sessionUsers.put(session.getId(), user);
        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session.getId());
    }

    public void removeSession(WebSocketSession session) {
        String sessionId = session.getId();
        User user = sessionUsers.get(sessionId);
        sessions.remove(sessionId);
        roomSessions.values().forEach(set -> set.remove(sessionId));
    }

    public User getUserFromSession(WebSocketSession session) {
        return sessionUsers.get(session.getId());
    }

    public void broadcastToRoom(UUID roomId, String payload) {
        Set<String> ids = roomSessions.getOrDefault(roomId, Set.of());
        ids.forEach(sessionId -> {
            WebSocketSession session = sessions.get(sessionId);
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(payload));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
