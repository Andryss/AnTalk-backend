package ru.andryss.antalk.server.converter;

import org.springframework.stereotype.Component;
import ru.andryss.antalk.generated.model.ChatDto;
import ru.andryss.antalk.generated.model.MessageDto;
import ru.andryss.antalk.generated.model.UserDto;
import ru.andryss.antalk.server.entity.ChatEntity;
import ru.andryss.antalk.server.entity.ChatType;
import ru.andryss.antalk.server.entity.MessageEntity;
import ru.andryss.antalk.server.entity.UserEntity;

/**
 * Конвертер из классов моделей в классы dto
 */
@Component
public class EntityToDtoConverter {

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
                .name(user.getName());
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

}
