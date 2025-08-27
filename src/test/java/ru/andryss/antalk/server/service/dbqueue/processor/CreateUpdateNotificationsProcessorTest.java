package ru.andryss.antalk.server.service.dbqueue.processor;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.andryss.antalk.server.BaseDbTest;
import ru.andryss.antalk.server.entity.ChatType;
import ru.andryss.antalk.server.entity.UpdateType;
import ru.andryss.antalk.server.util.DbTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUpdateNotificationsProcessorTest extends BaseDbTest {

    @Autowired
    CreateUpdateNotificationsProcessor processor;

    @Autowired
    DbTestUtil dbTestUtil;

    @Test
    void processChatCreatedEvent() {
        dbTestUtil.saveChat(333, ChatType.PRIVATE, List.of(2L, 3L, 5L));
        dbTestUtil.saveUpdate(12, UpdateType.CHAT_CREATED, Map.of("chatId", 333L));

        processor.execute(
                new CreateUpdateNotificationsPayload(12)
        );

        assertThat(
                dbTestUtil.findNotificationById(1)
        ).extracting(
                "id",
                "userId",
                "updateId"
        ).containsExactly(
                1L,
                2L,
                12L
        );

        assertThat(
                dbTestUtil.findNotificationById(2)
        ).extracting(
                "id",
                "userId",
                "updateId"
        ).containsExactly(
                2L,
                3L,
                12L
        );

        assertThat(
                dbTestUtil.findNotificationById(3)
        ).extracting(
                "id",
                "userId",
                "updateId"
        ).containsExactly(
                3L,
                5L,
                12L
        );
    }

    @Test
    void processMessageSentEvent() {
        dbTestUtil.saveChat(5, ChatType.PRIVATE, List.of(7L, 10L, 15L));
        dbTestUtil.saveMessage(999, 5, 33, "Some message");
        dbTestUtil.saveUpdate(13, UpdateType.MESSAGE_SENT, Map.of("messageId", 999L));

        processor.execute(
                new CreateUpdateNotificationsPayload(13)
        );

        assertThat(
                dbTestUtil.findNotificationById(1)
        ).extracting(
                "id",
                "userId",
                "updateId"
        ).containsExactly(
                1L,
                7L,
                13L
        );

        assertThat(
                dbTestUtil.findNotificationById(2)
        ).extracting(
                "id",
                "userId",
                "updateId"
        ).containsExactly(
                2L,
                10L,
                13L
        );

        assertThat(
                dbTestUtil.findNotificationById(3)
        ).extracting(
                "id",
                "userId",
                "updateId"
        ).containsExactly(
                3L,
                15L,
                13L
        );
    }

}