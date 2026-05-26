package com.prolaris.springboot.chatapp.rooms.exceptions;

public class RoomAlreadyExistException extends RuntimeException {
    public RoomAlreadyExistException(String message) {
        super(message);
    }
}
