package com.prolaris.springboot.chatapp.rooms.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RoomResponse(
        UUID id,
        String name,
        String description,
        boolean isPrivate,
        UUID createdById,
        LocalDateTime cratedAt
) {
}
