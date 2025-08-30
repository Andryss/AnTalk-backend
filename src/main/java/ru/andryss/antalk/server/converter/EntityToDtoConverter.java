package ru.andryss.antalk.server.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.andryss.antalk.server.entity.UpdateEntity;
import ru.andryss.antalk.server.entity.UpdateType;
import ru.andryss.antalk.server.generated.model.ChatDto;
import ru.andryss.antalk.server.generated.model.MessageDto;
import ru.andryss.antalk.server.generated.model.UpdateDto;
import ru.andryss.antalk.server.generated.model.UserDto;
import ru.andryss.antalk.server.entity.ChatEntity;
import ru.andryss.antalk.server.entity.ChatType;
import ru.andryss.antalk.server.entity.MessageEntity;
import ru.andryss.antalk.server.entity.UserEntity;
import ru.andryss.antalk.server.repository.ChatRepository;
import ru.andryss.antalk.server.repository.MessageRepository;
import ru.andryss.antalk.server.repository.UserRepository;

/**
 * Конвертер из классов моделей в классы dto
 */
@Component
@RequiredArgsConstructor
public class EntityToDtoConverter {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    /**
     * Конвертировать сущность чата в dto
     */
    public ChatDto convertChatToDto(ChatEntity chat) {
        return new ChatDto()
                .id(chat.getId())
                .type(ChatType.toApi(chat.getType()));
    }

    /**
     * Конвертировать сущность пользователя в dto
     */
    public UserDto convertUserToDto(UserEntity user) {
        return new UserDto()
                .id(user.getId())
                .name(user.getUsername());
    }

    /**
     * Конвертировать сущность сообщения в dto
     */
    public MessageDto convertMessageToDto(MessageEntity message, UserEntity user, ChatEntity chat) {
        return new MessageDto()
                .id(message.getId())
                .user(convertUserToDto(user))
                .chat(convertChatToDto(chat))
                .text(message.getText());
    }

    /**
     * Конвертировать сущность обновления в dto
     */
    public UpdateDto convertUpdateToDto(UpdateEntity update) {
        UpdateDto dto = new UpdateDto()
                .id(update.getId())
                .type(UpdateType.toApi(update.getType()));

        if (update.getType() == UpdateType.CHAT_CREATED) {
            long chatId = ((Number) update.getData().get("chatId")).longValue();
            dto = dto.chat(convertChatToDto(chatRepository.findByIdOrThrow(chatId)));
        }

        if (update.getType() == UpdateType.MESSAGE_SENT) {
            long messageId = ((Number) update.getData().get("messageId")).longValue();
            MessageEntity message = messageRepository.findByIdOrThrow(messageId);
            dto = dto.message(convertMessageToDto(
                    message,
                    userRepository.findByIdOrThrow(message.getSenderId()),
                    chatRepository.findByIdOrThrow(message.getChatId())
            ));
        }

        return dto;
    }
}
