package com.prolaris.springboot.chatapp.messages.mapper;

import com.prolaris.springboot.chatapp.messages.Message;
import com.prolaris.springboot.chatapp.messages.dto.MessageResponse;

public interface MessageMapper {
    MessageResponse toResponse(Message message);
}
