package com.prolaris.springboot.chatapp.rooms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRoomRequest(
        @NotNull(message = "Room name is required.") @NotEmpty(message = "Room name is required.")
        @NotBlank(message = "Room name is required.") @Size(max = 255, message = "Name must be under/equal to 255 characters.")
        String name,

        @NotNull(message = "Room name is required.") @NotEmpty(message = "Room name is required.")
        @NotBlank(message = "Room name is required.") @Size(max = 500, message = "Description must be under/equal to 500 characters.")
        String description,

        boolean isPrivate
) {
}
