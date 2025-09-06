package ru.andryss.antalk.server.controller;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JwtRequestFilterTest extends BaseApiTest {

    @ParameterizedTest
    @MethodSource("openEndpointsRequests")
    void requestOpenEndpointsWithoutTokenTest(RequestBuilder request) throws Exception {
        dbTestUtil.saveUser(100L, "testuser", "$2a$10$Kio4vtZrIOozp1RPWK95xeKYGeklbQ7z8N9L5hvQviOfbx02ANqM2");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("authenticatedEndpointsRequests")
    void requestAuthenticatedEndpointsWithoutTokenTest(RequestBuilder request) throws Exception {
        mockMvc.perform(request)
                .andExpectAll(
                        status().isBadRequest(),
                        content().json("""
                                {
                                    "code": 401,
                                    "message": "user.unauthorized",
                                    "humanMessage": "Пользователь не авторизован"
                                }
                                """)
                );
    }

    @ParameterizedTest
    @MethodSource("invalidTokens")
    void requestWithInvalidTokenTest() throws Exception {
        mockMvc.perform(post("/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalid-token")
                        .content("{ }"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().json("""
                                {
                                    "code": 401,
                                    "message": "user.unauthorized",
                                    "humanMessage": "Пользователь не авторизован"
                                }
                                """)
                );
    }

    static Stream<RequestBuilder> openEndpointsRequests() {
        return Stream.of(
                get("/ping"),
                post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "password": "some-pass"
                                }
                                """)
        );
    }

    static Stream<RequestBuilder> authenticatedEndpointsRequests() {
        return Stream.of(
                post("/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ }"),
                post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ }")
        );
    }

    static Stream<String> invalidTokens() {
        return Stream.of(
                "",
                "InvalidFormat token",
                "Bearer invalid-token"
        );
    }
}
