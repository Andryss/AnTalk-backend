package ru.andryss.antalk.server.entity;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatEntity {
    private long id;
    private ChatType type;
    private List<Long> userIds;
    private Instant createdAt;
}
