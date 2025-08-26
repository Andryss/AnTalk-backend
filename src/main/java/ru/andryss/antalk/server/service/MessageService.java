package ru.andryss.antalk.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.antalk.server.entity.MessageEntity;
import ru.andryss.antalk.server.exception.MessageNotFoundException;
import ru.andryss.antalk.server.repository.MessageRepository;

/**
 * Сервис для работы с сообщениями
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    /**
     * Получить сообщение по идентификатору. Если сообщение не найдено - выбросить ошибку
     */
    public MessageEntity findByIdOrThrow(long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException(id));
    }
}
