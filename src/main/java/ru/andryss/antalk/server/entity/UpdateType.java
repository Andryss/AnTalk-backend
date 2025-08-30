package ru.andryss.antalk.server.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Тип обновления
 */
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

    /**
     * Получить тип обновления по идентификатору
     */
    public static UpdateType fromId(int id) {
        for (UpdateType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown update type " + id);
    }

    /**
     * Конвертировать тип обновления в класс API
     */
    public static ru.andryss.antalk.server.generated.model.UpdateType toApi(UpdateType update) {
        return switch (update) {
            case CHAT_CREATED -> ru.andryss.antalk.server.generated.model.UpdateType.CHAT_CREATED;
            case MESSAGE_SENT -> ru.andryss.antalk.server.generated.model.UpdateType.MESSAGE_SENT;
        };
    }
}
