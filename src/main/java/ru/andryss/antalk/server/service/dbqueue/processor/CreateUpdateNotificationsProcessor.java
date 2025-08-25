package ru.andryss.antalk.server.service.dbqueue.processor;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.andryss.antalk.server.entity.ChatEntity;
import ru.andryss.antalk.server.entity.MessageEntity;
import ru.andryss.antalk.server.entity.NotificationEntity;
import ru.andryss.antalk.server.entity.UpdateEntity;
import ru.andryss.antalk.server.service.ChatService;
import ru.andryss.antalk.server.service.MessageService;
import ru.andryss.antalk.server.service.NotificationService;
import ru.andryss.antalk.server.service.UpdateService;
import ru.andryss.antalk.server.service.dbqueue.DbQueueProcessor;
import ru.andryss.antalk.server.service.dbqueue.DbQueueSettings;
import ru.yoomoney.tech.dbqueue.api.TaskExecutionResult;

@Component
@RequiredArgsConstructor
@DbQueueSettings(CreateUpdateNotificationsPayload.QUEUE_NAME)
public class CreateUpdateNotificationsProcessor implements DbQueueProcessor<CreateUpdateNotificationsPayload> {

    private final UpdateService updateService;
    private final ChatService chatService;
    private final NotificationService notificationService;
    private final MessageService messageService;

    @Override
    public TaskExecutionResult execute(CreateUpdateNotificationsPayload payload) {
        long updateId = payload.getUpdateId();

        UpdateEntity update = updateService.findByIdOrThrow(updateId);

        switch (update.getType()) {
            case CHAT_CREATED -> handleChatCreatedUpdate(update);
            case MESSAGE_SENT -> handleMessageSentUpdate(update);
            default -> throw new IllegalArgumentException("Unknown update type " + updateId);
        }

        return TaskExecutionResult.finish();
    }

    private void handleChatCreatedUpdate(UpdateEntity update) {
        long chatId = (long) update.getData().get("chatId");

        ChatEntity chat = chatService.findByIdOrThrow(chatId);

        saveNotifications(update.getId(), chat);
    }

    private void handleMessageSentUpdate(UpdateEntity update) {
        long messageId = (long) update.getData().get("messageId");

        MessageEntity message = messageService.findByIdOrThrow(messageId);

        ChatEntity chat = chatService.findByIdOrThrow(message.getChatId());

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

        notificationService.save(notifications);
    }
}
