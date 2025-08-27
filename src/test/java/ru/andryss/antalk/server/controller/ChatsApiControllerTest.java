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

class ChatsApiControllerTest extends BaseApiTest {

    @Test
    void createNewPrivateChatTest() throws Exception {
        mockMvc.perform(
                post("/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "type": "PRIVATE",
                                    "userIds": [111, 222]
                                }
                                """)
        ).andExpectAll(
                status().isOk(),
                content().json("""
                        {
                            "id": 1,
                            "type": "PRIVATE"
                        }
                        """)
        );

        assertThat(
                dbTestUtil.findChatById(1)
        ).extracting(
                "id",
                "type",
                "userIds"
        ).containsExactly(
                1L,
                ChatType.PRIVATE,
                List.of(111L, 222L)
        );

        assertThat(
                dbTestUtil.findUpdateById(1)
        ).extracting(
                "id",
                "type",
                "data"
        ).containsExactly(
                1L,
                UpdateType.CHAT_CREATED,
                Map.of("chatId", 1)
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