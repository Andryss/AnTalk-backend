package ru.andryss.antalk.server.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(long id) {
        super(String.format("Session with id %s not found", id));
    }
}
