package ru.andryss.antalk.server.service.dbqueue;

import lombok.Getter;
import lombok.Setter;
import ru.andryss.antalk.server.requestid.RequestIdAware;

/**
 * Описание базовых данных для задачи из очереди
 */
@Getter
@Setter
public class QueuePayload implements RequestIdAware {
    private String requestId;

    public QueuePayload() {
        this.requestId = fetchRequestId();
    }
}
