package ru.andryss.antalk.server.service.dbqueue.processor;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.andryss.antalk.server.entity.ChatEntity;
import ru.andryss.antalk.server.entity.MessageEntity;
import ru.andryss.antalk.server.entity.NotificationEntity;
import ru.andryss.antalk.server.entity.UpdateEntity;
import ru.andryss.antalk.server.repository.ChatRepository;
import ru.andryss.antalk.server.repository.MessageRepository;
import ru.andryss.antalk.server.repository.NotificationRepository;
import ru.andryss.antalk.server.repository.UpdateRepository;
import ru.andryss.antalk.server.service.dbqueue.DbQueueProcessor;
import ru.andryss.antalk.server.service.dbqueue.DbQueueSettings;
import ru.yoomoney.tech.dbqueue.api.TaskExecutionResult;

@Component
@RequiredArgsConstructor
@DbQueueSettings(CreateUpdateNotificationsPayload.QUEUE_NAME)
public class CreateUpdateNotificationsProcessor implements DbQueueProcessor<CreateUpdateNotificationsPayload> {

    private final UpdateRepository updateRepository;
    private final ChatRepository chatRepository;
    private final NotificationRepository notificationRepository;
    private final MessageRepository messageRepository;

    @Override
    public TaskExecutionResult execute(CreateUpdateNotificationsPayload payload) {
        long updateId = payload.getUpdateId();

        UpdateEntity update = updateRepository.findByIdOrThrow(updateId);

        switch (update.getType()) {
            case CHAT_CREATED -> handleChatCreatedUpdate(update);
            case MESSAGE_SENT -> handleMessageSentUpdate(update);
            default -> throw new IllegalArgumentException("Unknown update type " + updateId);
        }

        return TaskExecutionResult.finish();
    }

    private void handleChatCreatedUpdate(UpdateEntity update) {
        long chatId = (long) update.getData().get("chatId");

        ChatEntity chat = chatRepository.findByIdOrThrow(chatId);

        saveNotifications(update.getId(), chat);
    }

    private void handleMessageSentUpdate(UpdateEntity update) {
        long messageId = (long) update.getData().get("messageId");

        MessageEntity message = messageRepository.findByIdOrThrow(messageId);

        ChatEntity chat = chatRepository.findByIdOrThrow(message.getChatId());

        saveNotifications(update.getId(), chat);
    }

    private void saveNotifications(long updateId, ChatEntity chat) {
        List<NotificationEntity> notifications = chat.getUserIds().stream()
                .map(userId -> {
                    NotificationEntity notification = new NotificationEntity();
                    notification.setUserId(userId);
                    notification.setUpdateId(updateId);
                    return notification;
                })
                .toList();

        notificationRepository.save(notifications);
    }
}
