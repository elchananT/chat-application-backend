package com.prolaris.springboot.chatapp.messages.mapper;

import com.prolaris.springboot.chatapp.messages.Message;
import com.prolaris.springboot.chatapp.messages.dto.MessageResponse;
import org.springframework.stereotype.Component;

@Component
public class MessageMapperImpl implements MessageMapper {
    @Override
    public MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getRoom().getId(),
                message.getUser().getId(),
                message.getUser().getUsername(),
                message.getContent(),
                message.getSentAt());
    }
}
