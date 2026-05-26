package com.prolaris.springboot.chatapp.rooms;

import com.prolaris.springboot.chatapp.auth.users.User;
import com.prolaris.springboot.chatapp.rooms.dto.CreateRoomRequest;
import com.prolaris.springboot.chatapp.rooms.dto.RoomResponse;
import com.prolaris.springboot.chatapp.rooms.exceptions.RoomAlreadyExistException;
import com.prolaris.springboot.chatapp.rooms.exceptions.RoomNotFoundException;
import com.prolaris.springboot.chatapp.rooms.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomMapper roomMapper;

    @Transactional(readOnly = true)
    public List<RoomResponse> listRooms() {
        return roomRepository.findByIsPrivateFalse()
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request, User creator) {
        if (roomRepository.existsByName(request.name())) {
            throw new RoomAlreadyExistException("Room '"  + request.name() + "' already exist");
        }

        Room room = Room.builder()
                .name(request.name())
                .description(request.description())
                .isPrivate(request.isPrivate())
                .createdBy(creator)
                .build();

        Room saved = roomRepository.save(room);

        roomMemberRepository.save(RoomMember.builder().room(saved).user(creator).build());

        return roomMapper.toResponse(saved);
    }

    @Transactional
    public RoomResponse joinRoom(UUID roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        if (!roomMemberRepository.existsByRoomAndUser(room, user)) {
            roomMemberRepository.save(RoomMember.builder().room(room).user(user).build());
        }

        return roomMapper.toResponse(room);
    }
}
