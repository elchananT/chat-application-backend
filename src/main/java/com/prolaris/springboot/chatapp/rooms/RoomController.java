package com.prolaris.springboot.chatapp.rooms;


import com.prolaris.springboot.chatapp.auth.users.User;
import com.prolaris.springboot.chatapp.rooms.dto.CreateRoomRequest;
import com.prolaris.springboot.chatapp.rooms.dto.RoomResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponse>> listRooms(){
        return ResponseEntity.ok(roomService.listRooms());
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal User user
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(request, user));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<RoomResponse> joinRoom(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(roomService.joinRoom(id, user));
    }
}
