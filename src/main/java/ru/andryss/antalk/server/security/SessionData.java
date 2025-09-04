package ru.andryss.antalk.server.security;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SessionData {
    private long userId;
    private long sessionId;
    private List<String> privileges;
}
