package com.prolaris.springboot.chatapp;

import tools.jackson.databind.ObjectMapper;
import com.prolaris.springboot.chatapp.rooms.RoomService;
import com.prolaris.springboot.chatapp.rooms.dto.CreateRoomRequest;
import com.prolaris.springboot.chatapp.rooms.dto.RoomResponse;
import com.prolaris.springboot.chatapp.rooms.exceptions.RoomAlreadyExistException;
import com.prolaris.springboot.chatapp.rooms.exceptions.RoomNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Room Controller Tests")
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoomService roomService;

    private RoomResponse sampleRoom;
    private UUID roomId;
    private UUID userId;

    @BeforeEach
    public void setup() {
        roomId = UUID.randomUUID();
        userId = UUID.randomUUID();
        sampleRoom = new RoomResponse(roomId, "room1", "Room One", false, userId, LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/rooms -> 401 without auth")
    void getRooms_401_without_auth() throws Exception {
        mockMvc.perform(get("/api/rooms")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/rooms -> 200 with auth")
    void getRooms_200_without_auth() throws Exception {
        when(roomService.listRooms()).thenReturn(List.of(sampleRoom));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(roomId.toString()))
                .andExpect(jsonPath("$.[0].name").value("room1"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/rooms -> create the new room")
    void create_new_room() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest("room1", "Room One", false);
        when(roomService.createRoom(any(), any())).thenReturn(sampleRoom);

        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("room1"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/rooms -> 400 error without the name")
    void create_new_room_without_name() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest("", "Room One", false);

        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/rooms -> 409 when the room name is exist")
    void create_new_room_with_existing_name() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest("room1", "Room One", false);
        when(roomService.createRoom(any(), any())).thenThrow(new RoomAlreadyExistException("Room already exist"));

        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/rooms/{id}/join -> OK join to the room")
    void join_join_room() throws Exception {
        when(roomService.joinRoom(any(), any())).thenReturn(sampleRoom);

        mockMvc.perform(post("/api/rooms/{id}/join", roomId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("room1"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/rooms/{id}/join -> 404 not found, when room dose not exist")
    void join_join_room_404() throws Exception {
        when(roomService.joinRoom(any(), any())).thenThrow(new RoomNotFoundException("Room not found"));

        mockMvc.perform(post("/api/rooms/{id}/join", userId.toString()))
                .andExpect(status().isNotFound());
    }
}
