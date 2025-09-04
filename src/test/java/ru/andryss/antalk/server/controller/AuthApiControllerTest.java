package ru.andryss.antalk.server.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.andryss.antalk.server.entity.SessionStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiControllerTest extends BaseApiTest {

    @Test
    void signInSuccessTest() throws Exception {
        dbTestUtil.saveUser(44, "user", "$2a$10$Kio4vtZrIOozp1RPWK95xeKYGeklbQ7z8N9L5hvQviOfbx02ANqM2");

        mockMvc.perform(
                post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "user",
                                    "password": "some-pass"
                                }
                                """)
        ).andExpectAll(
                status().isOk(),
                content().json("""
                        {
                            "session": {
                                "id": 1
                            }
                        }
                        """),
                jsonPath("$.session.token").isString(),
                jsonPath("$.session.token").isNotEmpty()
        );

        assertThat(
                dbTestUtil.findSessionById(1)
        ).extracting(
                "id",
                "userId",
                "status",
                "lastNotification"
        ).containsExactly(
                1L,
                44L,
                SessionStatus.DISCONNECTED,
                -1L
        );
    }

}