package com.prolaris.springboot.chatapp.messages;

import com.prolaris.springboot.chatapp.auth.users.User;
import com.prolaris.springboot.chatapp.auth.users.UserRepository;
import com.prolaris.springboot.chatapp.messages.dto.MessageResponse;
import com.prolaris.springboot.chatapp.messages.mapper.MessageMapper;
import com.prolaris.springboot.chatapp.rooms.Room;
import com.prolaris.springboot.chatapp.rooms.RoomRepository;
import com.prolaris.springboot.chatapp.rooms.exceptions.RoomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Transactional
    public Message save(UUID roomId, UUID userId, String content) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found" + roomId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found" + userId));

        Message message = Message.builder()
                .room(room)
                .user(user)
                .content(content)
                .build();

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getHistory(UUID roomId, Pageable pageable) {
        return messageRepository.findByRoomIdOrderBySentAtDesc(roomId, pageable)
                .stream().map(messageMapper::toResponse).toList();
    }
}
