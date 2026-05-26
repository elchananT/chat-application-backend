package com.prolaris.springboot.chatapp.rooms.mapper;

import com.prolaris.springboot.chatapp.rooms.Room;
import com.prolaris.springboot.chatapp.rooms.dto.RoomResponse;
import org.springframework.stereotype.Service;

@Service
public class RoomMapperImpl implements RoomMapper {
    @Override
    public RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getDescription(),
                room.isPrivate(),
                room.getCreatedBy().getId(),
                room.getCreatedAt()
        );
    }
}
