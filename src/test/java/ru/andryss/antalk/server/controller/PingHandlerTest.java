package ru.andryss.antalk.server.controller;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PingHandlerTest extends BaseAuthTest {

    @LocalServerPort
    int port;

    @Test
    @SuppressWarnings("NullableProblems")
    void pingTest() throws Exception {
        // sign in as some user
        AuthData authData = registerUserAndSignIn(15, "user", "pass");

        // init web socket client
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new StringMessageConverter());

        // add authorization header
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", formatAuthorization(authData));

        // connect to app
        CompletableFuture<StompSession> connectFuture = stompClient.connectAsync(
                "ws://localhost:" + port + "/ws",
                headers,
                new StompSessionHandlerAdapter() { }
        );

        // wait for connection
        StompSession session = connectFuture.get(2, TimeUnit.SECONDS);
        assertTrue(session.isConnected());

        // subscribe to ping
        CompletableFuture<String> resultFuture = new CompletableFuture<>();

        session.subscribe(
                "/pong",
                new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }
                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        resultFuture.complete((String) payload);
                    }
                }
        );

        // send echo message
        session.send("/app/ping", "echo");

        // wait for response
        String response = resultFuture.get(2, TimeUnit.SECONDS);
        assertEquals("echo", response);

        // close connection
        session.disconnect();
        stompClient.stop();
    }
}