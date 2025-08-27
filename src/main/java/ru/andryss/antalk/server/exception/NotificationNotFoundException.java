package ru.andryss.antalk.server.exception;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(long id) {
        super(String.format("Notification with id %s not found", id));
    }
}
