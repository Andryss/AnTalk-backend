package ru.andryss.antalk.server.controller;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class PingHandler {
    @MessageMapping("/ping")
    @SendTo("/pong")
    public String ping(Message<byte[]> message) {
        return new String(message.getPayload());
    }
}
