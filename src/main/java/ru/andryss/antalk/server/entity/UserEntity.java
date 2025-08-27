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
    private String username;
    private String passwordHash;
    private Instant createdAt;
}
