package ru.andryss.antalk.server.service.dbqueue.processor;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import ru.andryss.antalk.server.generated.model.MessageDto;
import ru.andryss.antalk.server.generated.model.UpdateDto;
import ru.andryss.antalk.server.generated.model.UserDto;
import ru.andryss.antalk.server.util.DbTestUtil;

import static org.mockito.ArgumentMatchers.eq;

class SendUpdateNotificationsProcessorTest extends BaseDbTest {

    @Autowired
    SendUpdateNotificationsProcessor processor;

    @MockitoBean
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    DbTestUtil dbTestUtil;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(messagingTemplate);
    }

    @AfterEach
    void verifyMocks() {
        Mockito.verifyNoMoreInteractions(messagingTemplate);
    }

    @Test
    void sendCreatedChatUpdateNotifications() {
        dbTestUtil.saveUser(3, "user", "hash");

        dbTestUtil.saveSession(100, 3, Map.of(), SessionStatus.DISCONNECTED, 2);
        dbTestUtil.saveSession(101, 3, Map.of(), SessionStatus.UPDATING, 2);
        dbTestUtil.saveSession(102, 3, Map.of(), SessionStatus.ONLINE, 2);
        dbTestUtil.saveSession(103, 3, Map.of(), SessionStatus.ONLINE, 4);
        dbTestUtil.saveSession(104, 3, Map.of(), SessionStatus.ONLINE, 5);
        dbTestUtil.saveSession(105, 3, Map.of(), SessionStatus.ONLINE, 6);

        dbTestUtil.saveChat(9090, ChatType.PRIVATE, List.of(4L, 5L));
        dbTestUtil.saveUpdate(33, UpdateType.CHAT_CREATED, Map.of("chatId", 9090L));

        dbTestUtil.saveNotification(5, 3, 33);

        processor.execute(
                new CreateUpdateNotificationsPayload(33)
        );

        UpdateDto expectingMessage = new UpdateDto()
                .id(33L)
                .type(ru.andryss.antalk.server.generated.model.UpdateType.CHAT_CREATED)
                .chat(new ChatDto()
                        .id(9090L)
                        .type(ru.andryss.antalk.server.generated.model.ChatType.PRIVATE));

        Mockito.verify(messagingTemplate).convertAndSendToUser(
                eq("3"), eq("/session/102/updates"), eq(expectingMessage)
        );
        Mockito.verify(messagingTemplate).convertAndSendToUser(
                eq("3"), eq("/session/103/updates"), eq(expectingMessage)
        );
    }

    @Test
    void sendMessageSentUpdateNotifications() {
        dbTestUtil.saveUser(1, "super-user", "super-hash");

        dbTestUtil.saveSession(110, 1, Map.of(), SessionStatus.DISCONNECTED, 200);
        dbTestUtil.saveSession(111, 1, Map.of(), SessionStatus.UPDATING, 200);
        dbTestUtil.saveSession(112, 1, Map.of(), SessionStatus.ONLINE, 200);
        dbTestUtil.saveSession(113, 1, Map.of(), SessionStatus.ONLINE, 600);

        dbTestUtil.saveChat(9090, ChatType.PRIVATE, List.of(4L, 5L));
        dbTestUtil.saveMessage(3030, 9090, 1, "text");
        dbTestUtil.saveUpdate(33, UpdateType.MESSAGE_SENT, Map.of("messageId", 3030L));

        dbTestUtil.saveNotification(500, 1, 33);

        processor.execute(
                new CreateUpdateNotificationsPayload(33)
        );

        UpdateDto expectingMessage = new UpdateDto()
                .id(33L)
                .type(ru.andryss.antalk.server.generated.model.UpdateType.MESSAGE_SENT)
                .message(new MessageDto()
                        .id(3030L)
                        .user(new UserDto()
                                .id(1L)
                                .name("super-user"))
                        .chat(new ChatDto()
                                .id(9090L)
                                .type(ru.andryss.antalk.server.generated.model.ChatType.PRIVATE))
                        .text("text"));

        Mockito.verify(messagingTemplate).convertAndSendToUser(
                eq("1"), eq("/session/112/updates"), eq(expectingMessage)
        );
    }

}