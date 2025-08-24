package ru.andryss.antalk.server.entity;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationEntity {
    private long id;
    private long userId;
    private long updateId;
    private Instant createdAt;
}
