package ru.andryss.antalk.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.antalk.generated.api.MessagesApi;
import ru.andryss.antalk.generated.model.MessageDto;
import ru.andryss.antalk.generated.model.SendMessageRequest;
import ru.andryss.antalk.server.service.MessageService;

@RestController
@RequiredArgsConstructor
public class MessagesApiController implements MessagesApi {

    private final MessageService messageService;

    @Override
    public MessageDto sendMessage(SendMessageRequest request) {
        return messageService.sendNew(request);
    }
}
