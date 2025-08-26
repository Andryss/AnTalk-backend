package ru.andryss.antalk.server.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super(String.format("User with id %s not found", id));
    }
}
