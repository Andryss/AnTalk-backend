package ru.andryss.antalk.server.config;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockBeansConfig {

    @Bean
    public Clock mockClock() {
        ZoneOffset zone = ZoneOffset.UTC;
        Instant instant = LocalDateTime.of(2025, 6, 3, 10, 15).toInstant(zone);
        return Clock.fixed(instant, zone);
    }

}
