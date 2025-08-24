package ru.andryss.antalk.server.entity;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageEntity {
    private long id;
    private long chatId;
    private long senderId;
    private String text;
    private Instant createdAt;
}
