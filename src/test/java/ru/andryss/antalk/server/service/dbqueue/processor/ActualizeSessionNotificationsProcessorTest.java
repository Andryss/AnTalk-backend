package ru.andryss.antalk.server.service.dbqueue.processor;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.andryss.antalk.server.BaseDbTest;
import ru.andryss.antalk.server.entity.ChatType;
import ru.andryss.antalk.server.entity.SessionStatus;
import ru.andryss.antalk.server.entity.UpdateType;
import ru.andryss.antalk.server.generated.model.ChatDto;
import ru.andryss.antalk.server.generated.model.UpdateDto;
import ru.andryss.antalk.server.util.DbTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

class ActualizeSessionNotificationsProcessorTest extends BaseDbTest {

    @Autowired
    ActualizeSessionNotificationsProcessor processor;

    @MockitoBean
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    DbTestUtil dbTestUtil;

    @Test
    void processTest() {
        dbTestUtil.saveSession(5, 1, Map.of(), SessionStatus.UPDATING, 20);

        dbTestUtil.saveUpdate(1000, UpdateType.CHAT_CREATED, Map.of("chatId", 55));
        dbTestUtil.saveNotification(19, 1, 1000);

        dbTestUtil.saveUpdate(1001, UpdateType.CHAT_CREATED, Map.of("chatId", 55));
        dbTestUtil.saveNotification(20, 1, 1001);

        dbTestUtil.saveChat(60, ChatType.PRIVATE, List.of(-1L, -2L));
        dbTestUtil.saveUpdate(1002, UpdateType.CHAT_CREATED, Map.of("chatId", 60));
        dbTestUtil.saveNotification(21, 1, 1002);

        dbTestUtil.saveChat(61, ChatType.PRIVATE, List.of(5L));
        dbTestUtil.saveUpdate(1003, UpdateType.CHAT_CREATED, Map.of("chatId", 61));
        dbTestUtil.saveNotification(22, 1, 1003);

        processor.execute(
                new ActualizeSessionNotificationsPayload(5)
        );

        UpdateDto firstMessage = new UpdateDto()
                .id(1002L)
                .type(ru.andryss.antalk.server.generated.model.UpdateType.CHAT_CREATED)
                .chat(new ChatDto()
                        .id(60L)
                        .type(ru.andryss.antalk.server.generated.model.ChatType.PRIVATE));

        Mockito.verify(messagingTemplate).convertAndSendToUser(
                eq("1"), eq("/session/5/updates"), eq(firstMessage)
        );

        UpdateDto secondMessage = new UpdateDto()
                .id(1003L)
                .type(ru.andryss.antalk.server.generated.model.UpdateType.CHAT_CREATED)
                .chat(new ChatDto()
                        .id(61L)
                        .type(ru.andryss.antalk.server.generated.model.ChatType.PRIVATE));

        Mockito.verify(messagingTemplate).convertAndSendToUser(
                eq("1"), eq("/session/5/updates"), eq(secondMessage)
        );

        assertThat(
                dbTestUtil.findSessionById(5)
        ).extracting(
                "id",
                "userId",
                "status",
                "lastNotification"
        ).containsExactly(
                5L,
                1L,
                SessionStatus.ONLINE,
                22L
        );
    }

}