package ru.andryss.antalk.server.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatType {
    /**
     * Приватный чат 1-1.
     */
    PRIVATE(0);

    private final int id;

    public static ChatType fromId(int id) {
        for (ChatType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown chat type " + id);
    }

    public static ChatType fromApi(ru.andryss.antalk.generated.model.ChatType type) {
        return switch (type) {
            case PRIVATE -> PRIVATE;
        };
    }

    public static ru.andryss.antalk.generated.model.ChatType toApi(ChatType type) {
        return switch (type) {
            case PRIVATE -> ru.andryss.antalk.generated.model.ChatType.PRIVATE;
        };
    }
}
