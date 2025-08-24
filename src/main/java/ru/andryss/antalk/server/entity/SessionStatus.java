package ru.andryss.antalk.server.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SessionStatus {
    /**
     * Сессия не активна (соединение устройства оборвано).
     */
    DISCONNECTED(0),
    /**
     * Сессия обновляется (соединение с устройством установлено, происходит актуализация состояния).
     */
    UPDATING(1),
    /**
     * Сессия онлайн (соединение установлено, состояние актуально).
     */
    ONLINE(2);

    private final int id;

    public static SessionStatus fromId(int id) {
        for (SessionStatus status : values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown session status " + id);
    }
}
