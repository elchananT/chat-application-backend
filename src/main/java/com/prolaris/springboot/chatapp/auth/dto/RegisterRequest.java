package com.prolaris.springboot.chatapp.auth.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "Username cannot be blank.") @NotEmpty(message = "Username cannot be empty.") @NotNull(message = "Username cannot be null.")
        String username,
        @NotBlank(message = "Email cannot be blank.") @NotEmpty(message = "Email cannot be empty.") @NotNull(message = "Email cannot be null.")
        @Email(message = "Email must be valid.")
        String email,
        @NotBlank(message = "Password cannot be blank.") @NotEmpty(message = "Password cannot be empty.") @NotNull(message = "Password cannot be null.")
        @Size(min = 6, max = 36, message = "Password must be between 6 and 36 characters long.")
        String password
) {
}
