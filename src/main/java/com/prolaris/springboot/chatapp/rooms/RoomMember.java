package com.prolaris.springboot.chatapp.rooms;

import com.prolaris.springboot.chatapp.auth.users.User;
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
@Table(name = "room_members", uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "user_id"}))
public class RoomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    private LocalDateTime joinAt =  LocalDateTime.now();
}
