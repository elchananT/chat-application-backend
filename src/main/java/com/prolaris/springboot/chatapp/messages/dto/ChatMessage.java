package com.prolaris.springboot.chatapp.messages.dto;

import java.util.UUID;

public record ChatMessage(
        Type type,
        UUID roomId,
        String content
) {
    public enum Type { SEND, JOIN, LEAVE, ERROR }
}
