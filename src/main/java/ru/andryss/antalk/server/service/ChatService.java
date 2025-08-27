package ru.andryss.antalk.server.service;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.andryss.antalk.server.generated.model.ChatDto;
import ru.andryss.antalk.server.generated.model.CreateChatRequest;
import ru.andryss.antalk.server.converter.EntityToDtoConverter;
import ru.andryss.antalk.server.entity.ChatEntity;
import ru.andryss.antalk.server.entity.ChatType;
import ru.andryss.antalk.server.entity.UpdateEntity;
import ru.andryss.antalk.server.entity.UpdateType;
import ru.andryss.antalk.server.repository.ChatRepository;
import ru.andryss.antalk.server.repository.UpdateRepository;
import ru.andryss.antalk.server.service.dbqueue.DbQueueService;
import ru.andryss.antalk.server.service.dbqueue.processor.CreateUpdateNotificationsPayload;
import ru.andryss.antalk.server.service.dbqueue.processor.CreateUpdateNotificationsProcessor;

/**
 * Сервис для работы с чатами
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    public static final int PRIVATE_CHAT_USER_COUNT = 2;

    private final ChatRepository chatRepository;
    private final UpdateRepository updateRepository;
    private final DbQueueService dbQueueService;
    private final TransactionTemplate transactionTemplate;
    private final EntityToDtoConverter converter;

    /**
     * Создать новый чат. Также создаст задачу на уведомление пользователей
     */
    public ChatDto createNew(CreateChatRequest request) {
        ChatType chatType = ChatType.fromApi(request.getType());

        if (chatType == ChatType.PRIVATE) {
            List<Long> userIds = request.getUserIds();

            if (userIds.size() != PRIVATE_CHAT_USER_COUNT) {
                throw new IllegalArgumentException("Wrong users count for private chat"); // TODO: exception
            }
        }

        return transactionTemplate.execute(status -> {
            ChatEntity chat = new ChatEntity();
            chat.setType(chatType);
            chat.setUserIds(request.getUserIds());

            ChatEntity savedChat = chatRepository.save(chat);

            UpdateEntity update = new UpdateEntity();
            update.setType(UpdateType.CHAT_CREATED);
            update.setData(Map.of("chatId", savedChat.getId()));

            UpdateEntity savedUpdate = updateRepository.save(update);

            CreateUpdateNotificationsPayload payload = new CreateUpdateNotificationsPayload(savedUpdate.getId());

            dbQueueService.produceTask(CreateUpdateNotificationsProcessor.class, payload);

            return converter.convertChatToDto(savedChat);
        });
    }

}
