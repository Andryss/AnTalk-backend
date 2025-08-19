package ru.andryss.antalk.server.service.dbqueue;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import ru.andryss.antalk.server.config.dbqueue.QueueProducerBase;
import ru.yoomoney.tech.dbqueue.api.EnqueueParams;

/**
 * Сервис для работы с db-queue
 */
@Service
public class DbQueueService {

    private final Map<Class<?>, QueueProducerBase<?>> producers;

    public DbQueueService(List<? extends QueueProducerBase<?>> producers) {
        this.producers = producers.stream()
                .collect(Collectors.toMap(QueueProducerBase::getProcessorClass, Function.identity()));
    }

    /**
     * Отправить задачу в очередь для обработки
     */
    @SuppressWarnings("unchecked")
    public <P extends QueuePayload> void produceTask(Class<? extends DbQueueProcessor<P>> processorClass, P payload) {
        EnqueueParams<P> params = EnqueueParams.create(payload);
        QueueProducerBase<P> producer = (QueueProducerBase<P>) producers.get(processorClass);
        producer.enqueue(params);
    }
}
