package com.prolaris.springboot.chatapp.auth.dto;

import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank(message = "Username cannot be blank.") @NotEmpty(message = "Username cannot be empty.") @NotNull(message = "Username cannot be null.")
        String username,
        @NotBlank(message = "Password cannot be blank.") @NotEmpty(message = "Password cannot be empty.") @NotNull(message = "Password cannot be null.")
        @Size(min = 6, max = 36, message = "Password must be between 6 and 36 characters long.")
        String password
) {
}
