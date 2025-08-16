package ru.andryss.antalk.server.requestid;

import java.util.UUID;

import org.slf4j.MDC;

/**
 * Класс, описывающий общую работу с идентификатором запроса
 */
public abstract class AbstractRequestIdAssign {

    private static final String REQUEST_ID_VARIABLE = "requestId";

    /**
     * Присвоить текущему обработчику идентификатор запроса.
     * Должен быть очищен после обработки запроса при помощи {@link #clearRequestId()}
     */
    protected void assignRequestId() {
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_VARIABLE, requestId);
    }

    /**
     * Очищение у обработчика идентификатор запроса
     */
    protected void clearRequestId() {
        MDC.remove(REQUEST_ID_VARIABLE);
    }

}
