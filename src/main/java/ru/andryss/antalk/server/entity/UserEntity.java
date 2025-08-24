package ru.andryss.antalk.server.entity;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    private long id;
    private String name;
    private Instant createdAt;
}
