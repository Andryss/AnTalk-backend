package ru.andryss.antalk.server.config.dbqueue;

import lombok.Getter;
import ru.andryss.antalk.server.service.dbqueue.DbQueueProcessor;
import ru.andryss.antalk.server.service.dbqueue.QueuePayload;
import ru.yoomoney.tech.dbqueue.api.EnqueueParams;
import ru.yoomoney.tech.dbqueue.api.EnqueueResult;
import ru.yoomoney.tech.dbqueue.api.QueueProducer;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;
import ru.yoomoney.tech.dbqueue.api.impl.ShardingQueueProducer;
import ru.yoomoney.tech.dbqueue.api.impl.SingleQueueShardRouter;
import ru.yoomoney.tech.dbqueue.config.QueueShard;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;

/**
 * Базовый класс, описывающий производителя событий в очередь db-queue
 */
public class QueueProducerBase<P extends QueuePayload> implements QueueProducer<P> {

    @Getter
    private final QueueConfig queueConfig;
    @Getter
    private final TaskPayloadTransformer<P> payloadTransformer;
    @Getter
    private final Class<? extends DbQueueProcessor<?>> processorClass;
    private final ShardingQueueProducer<P, ?> queueProducer;

    @SuppressWarnings("unchecked")
    public QueueProducerBase(
            QueueConfig queueConfig,
            TaskPayloadTransformer<P> payloadTransformer,
            DbQueueProcessor<P> processor,
            QueueShard<?> queueShard
    ) {
        this.queueConfig = queueConfig;
        this.payloadTransformer = payloadTransformer;
        this.processorClass = (Class<? extends DbQueueProcessor<?>>) processor.getClass();
        this.queueProducer = new ShardingQueueProducer<>(
                queueConfig,
                payloadTransformer,
                new SingleQueueShardRouter<>(queueShard)
        );
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public EnqueueResult enqueue(EnqueueParams<P> enqueueParams) {
        return queueProducer.enqueue(enqueueParams);
    }
}
