package ru.andryss.antalk.server.service.dbqueue.processor;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.andryss.antalk.server.service.dbqueue.DbQueueProcessor;
import ru.andryss.antalk.server.service.dbqueue.DbQueueSettings;
import ru.yoomoney.tech.dbqueue.api.TaskExecutionResult;

@Slf4j
@Component
@DbQueueSettings(DummyPayload.QUEUE_NAME)
public class DummyProcessor implements DbQueueProcessor<DummyPayload> {

    @Nonnull
    @Override
    public TaskExecutionResult execute(DummyPayload payload) {
        log.info("Processing payload {}", payload);
        return TaskExecutionResult.finish();
    }
}
