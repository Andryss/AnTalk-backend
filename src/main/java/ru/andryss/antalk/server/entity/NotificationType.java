package ru.andryss.antalk.server.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    /**
     * Создан новый чат.
     */
    CHAT_CREATED(0),
    /**
     * Отправлено новое сообщение.
     */
    MESSAGE_SENT(1);

    private final int id;

    public static NotificationType fromId(int id) {
        for (NotificationType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown notification type " + id);
    }
}
