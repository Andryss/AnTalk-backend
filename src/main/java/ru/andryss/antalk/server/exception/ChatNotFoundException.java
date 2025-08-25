package ru.andryss.antalk.server.exception;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(long id) {
        super(String.format("Chat with id %s not found", id));
    }
}
