package ru.andryss.antalk.server.exception;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(long id) {
        super(String.format("Message with id %s not found", id));
    }
}
