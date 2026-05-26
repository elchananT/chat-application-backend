package com.prolaris.springboot.chatapp.rooms;

import com.prolaris.springboot.chatapp.auth.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {
    boolean existsByRoomAndUser(Room room, User user);
}
