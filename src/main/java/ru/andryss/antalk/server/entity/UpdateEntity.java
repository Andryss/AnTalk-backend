package ru.andryss.antalk.server.entity;

import java.time.Instant;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateEntity {
    private long id;
    private long prev;
    private UpdateType type;
    private Map<String, Object> data;
    private Instant createdAt;
}
