package com.prolaris.springboot.chatapp.messages;

import com.prolaris.springboot.chatapp.auth.users.User;
import com.prolaris.springboot.chatapp.rooms.Room;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    private String content;

    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();
}
