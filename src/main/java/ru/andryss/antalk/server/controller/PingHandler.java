package ru.andryss.antalk.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class PingHandler {
    @MessageMapping("/ping")
    @SendTo("/pong")
    public String ping(Message<byte[]> message) {
        log.info("Message ping request handling");
        return new String(message.getPayload());
    }
}
