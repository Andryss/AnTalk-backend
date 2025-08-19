package ru.andryss.antalk.server.service.dbqueue;

import jakarta.annotation.Nonnull;
import ru.yoomoney.tech.dbqueue.api.TaskExecutionResult;

/**
 * Интерфейс описывающий процессор db-queue очереди
 * @param <P> класс данных процессора
 */
public interface DbQueueProcessor<P extends QueuePayload> {
    /**
     * Выполнить задачу из очереди
     */
    @Nonnull
    TaskExecutionResult execute(P payload);
}
