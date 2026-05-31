package com.prolaris.springboot.chatapp;

import com.prolaris.springboot.chatapp.messages.MessageService;
import com.prolaris.springboot.chatapp.messages.dto.MessageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Message Controller Tests")
public class MassageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    @WithMockUser
    @DisplayName("GET /api/rooms/{id}/messages -> 200 ok and the list")
    void getHistory_success() throws Exception {
        UUID roomId = UUID.randomUUID();
        MessageResponse messageResponse = new MessageResponse(
                UUID.randomUUID(), roomId, UUID.randomUUID(), "username", "Hello!", LocalDateTime.now()
        );

        when(messageService.getHistory(any(), any())).thenReturn(List.of(messageResponse));

        mockMvc.perform(get("/api/rooms/{id}/messages", roomId)
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello!"))
                .andExpect(jsonPath("$[0].roomId").value(roomId.toString()))
                .andExpect(jsonPath("$[0].username").value("username"));
    }

    @Test
    @DisplayName("GET /api/rooms/{id}/messages -> 401 without auth")
    void getHistory_not_authorized() throws Exception {
        mockMvc.perform(get("/api/rooms/{id}/messages", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/rooms/{id}/messages -> 200 ok with empty room")
    void getHistory_empty_room() throws Exception {
        when( messageService.getHistory(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/rooms/{id}/messages", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }


}
