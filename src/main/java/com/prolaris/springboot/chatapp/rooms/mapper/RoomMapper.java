package com.prolaris.springboot.chatapp.rooms.mapper;

import com.prolaris.springboot.chatapp.rooms.Room;
import com.prolaris.springboot.chatapp.rooms.dto.RoomResponse;

public interface RoomMapper {
    RoomResponse toResponse(Room room);
}
