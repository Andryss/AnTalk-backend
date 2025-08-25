package ru.andryss.antalk.server.exception;

public class UpdateNotFoundException extends RuntimeException {
    public UpdateNotFoundException(long id) {
        super(String.format("Update with id %s not found", id));
    }
}
