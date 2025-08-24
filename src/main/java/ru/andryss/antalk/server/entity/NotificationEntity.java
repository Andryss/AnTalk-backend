package ru.andryss.antalk.server.entity;

import java.time.Instant;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationEntity {
    private long id;
    private long userId;
    private NotificationType type;
    private Map<String, Object> data;
    private Instant createdAt;
}
