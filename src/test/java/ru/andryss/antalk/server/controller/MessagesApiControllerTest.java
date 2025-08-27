package ru.andryss.antalk.server.controller;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.andryss.antalk.server.entity.ChatType;
import ru.andryss.antalk.server.entity.UpdateType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MessagesApiControllerTest extends BaseApiTest {

    @Test
    void sendNewMessageTest() throws Exception {
        dbTestUtil.saveUser(111, "user", "pass-hash");
        dbTestUtil.saveChat(777, ChatType.PRIVATE, List.of(111L));

        mockMvc.perform(
                post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "senderId": 111,
                                    "chatId": 777,
                                    "text": "Some message text"
                                }
                                """)
        ).andExpectAll(
                status().isOk(),
                content().json("""
                        {
                            "id": 1,
                            "user": {
                                "id": 111,
                                "name": "user"
                            },
                            "chat": {
                                "id": 777,
                                "type": "PRIVATE"
                            },
                            "text": "Some message text"
                        }
                        """)
        );

        assertThat(
                dbTestUtil.findMessageById(1)
        ).extracting(
                "id",
                "senderId",
                "chatId",
                "text"
        ).containsExactly(
                1L,
                111L,
                777L,
                "Some message text"
        );

        assertThat(
                dbTestUtil.findUpdateById(1)
        ).extracting(
                "id",
                "type",
                "data"
        ).containsExactly(
                1L,
                UpdateType.MESSAGE_SENT,
                Map.of("messageId", 1)
        );

        assertThat(dbTestUtil.findTasksByQueue("CREATE_UPDATE_NOTIFICATIONS"))
                .hasSize(1)
                .element(0)
                .satisfies(payload -> {
                    assertThat(payload).extractingByKey("requestId").asString().isNotBlank();
                    assertThat(payload).extractingByKey("updateId").isEqualTo(1);
                });
    }

}