package com.prolaris.springboot.chatapp.excption;

import java.util.Map;

public record ErrorResponse(int status, String message, Map<String, String> errors) {
}
