package ru.andryss.antalk.server.config.requestid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.TaskDecorator;

/**
 * Декоратор для Runnable-задач.
 */
public class PropogateRequestIdTaskDecorator implements RequestIdAware, TaskDecorator {
    @Override
    @SuppressWarnings("NullableProblems")
    public Runnable decorate(Runnable runnable) {
        // Capture the current request id
        String requestId = fetchRequestId();

        return () -> {
            try {
                if (!StringUtils.isBlank(requestId)) {
                    assignRequestId(requestId);
                }
                runnable.run();
            } finally {
                clearRequestId();
            }
        };
    }
}
