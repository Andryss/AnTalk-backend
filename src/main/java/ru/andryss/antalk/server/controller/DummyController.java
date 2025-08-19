package ru.andryss.antalk.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.antalk.server.service.dbqueue.DbQueueService;
import ru.andryss.antalk.server.service.dbqueue.processor.DummyPayload;
import ru.andryss.antalk.server.service.dbqueue.processor.DummyProcessor;

@RestController
@RequiredArgsConstructor
public class DummyController {

    private final DbQueueService dbQueueService;

    /**
     * Тестовая ручка, записывающая задачу в db-queue очередь
     */
    @GetMapping("/dummy")
    public void dummy(@RequestParam String data) {
        dbQueueService.produceTask(DummyProcessor.class, new DummyPayload(data));
    }
}
