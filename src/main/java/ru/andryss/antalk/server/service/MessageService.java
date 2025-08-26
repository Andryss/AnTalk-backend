package ru.andryss.antalk.server.service;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.andryss.antalk.generated.model.MessageDto;
import ru.andryss.antalk.generated.model.SendMessageRequest;
import ru.andryss.antalk.server.converter.EntityToDtoConverter;
import ru.andryss.antalk.server.entity.ChatEntity;
import ru.andryss.antalk.server.entity.MessageEntity;
import ru.andryss.antalk.server.entity.UpdateEntity;
import ru.andryss.antalk.server.entity.UpdateType;
import ru.andryss.antalk.server.entity.UserEntity;
import ru.andryss.antalk.server.repository.ChatRepository;
import ru.andryss.antalk.server.repository.MessageRepository;
import ru.andryss.antalk.server.repository.UpdateRepository;
import ru.andryss.antalk.server.repository.UserRepository;
import ru.andryss.antalk.server.service.dbqueue.DbQueueService;
import ru.andryss.antalk.server.service.dbqueue.processor.CreateUpdateNotificationsPayload;
import ru.andryss.antalk.server.service.dbqueue.processor.CreateUpdateNotificationsProcessor;

/**
 * Сервис для работы с сообщениями
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UpdateRepository updateRepository;
    private final TransactionTemplate transactionTemplate;
    private final DbQueueService dbQueueService;
    private final EntityToDtoConverter converter;

    /**
     * Отправить новое сообщение. Также создаст задачу на уведомление пользователей
     */
    public MessageDto sendNew(SendMessageRequest request) {
        UserEntity user = userRepository.findByIdOrThrow(request.getSenderId());

        ChatEntity chat = chatRepository.findByIdOrThrow(request.getChatId());

        if (!chat.getUserIds().contains(user.getId())) {
            throw new IllegalArgumentException("User is not a participant"); // TODO: exception (chat not found?)
        }

        return transactionTemplate.execute(status -> {
            MessageEntity message = new MessageEntity();
            message.setSenderId(user.getId());
            message.setChatId(chat.getId());
            message.setText(request.getText());

            MessageEntity savedMessage = messageRepository.save(message);

            UpdateEntity update = new UpdateEntity();
            update.setType(UpdateType.MESSAGE_SENT);
            update.setData(Map.of("messageId", savedMessage.getId()));

            UpdateEntity savedUpdate = updateRepository.save(update);

            CreateUpdateNotificationsPayload payload = new CreateUpdateNotificationsPayload(savedUpdate.getId());

            dbQueueService.produceTask(CreateUpdateNotificationsProcessor.class, payload);

            return converter.convertMessageToDto(savedMessage, user, chat);
        });
    }
}
