package ru.andryss.antalk.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.antalk.server.generated.api.ChatsApi;
import ru.andryss.antalk.server.generated.model.ChatDto;
import ru.andryss.antalk.server.generated.model.CreateChatRequest;
import ru.andryss.antalk.server.service.ChatService;

@RestController
@RequiredArgsConstructor
public class ChatsApiController implements ChatsApi {

    private final ChatService chatService;

    @Override
    public ChatDto createChat(CreateChatRequest request) {
        return chatService.createNew(request);
    }
}
