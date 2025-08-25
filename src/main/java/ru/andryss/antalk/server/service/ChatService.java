package ru.andryss.antalk.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.antalk.server.entity.ChatEntity;
import ru.andryss.antalk.server.exception.ChatNotFoundException;
import ru.andryss.antalk.server.repository.ChatRepository;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatEntity findByIdOrThrow(long id) {
        return chatRepository.findById(id)
                .orElseThrow(() -> new ChatNotFoundException(id));
    }
}
