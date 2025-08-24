package ru.andryss.antalk.server.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UpdateType {
    /**
     * Создан новый чат.
     */
    CHAT_CREATED(0),
    /**
     * Отправлено новое сообщение.
     */
    MESSAGE_SENT(1);

    private final int id;

    public static UpdateType fromId(int id) {
        for (UpdateType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown update type " + id);
    }
}
