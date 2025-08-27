package ru.andryss.antalk.server.config.requestid;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import ru.andryss.antalk.server.service.ObjectMapperWrapper;
import ru.yoomoney.tech.dbqueue.api.TaskExecutionResult;
import ru.yoomoney.tech.dbqueue.api.TaskRecord;
import ru.yoomoney.tech.dbqueue.config.QueueShardId;
import ru.yoomoney.tech.dbqueue.config.TaskLifecycleListener;
import ru.yoomoney.tech.dbqueue.settings.QueueLocation;

@RequiredArgsConstructor
@SuppressWarnings("NullableProblems")
public class DbQueueTaskRequestIdAssignListener implements RequestIdAware, TaskLifecycleListener {

    private final ObjectMapperWrapper objectMapper;

    @Override
    public void picked(
            QueueShardId shardId,
            QueueLocation location,
            TaskRecord taskRecord,
            long pickTaskTime
    ) {
        // do nothing
    }

    @Override
    public void started(
            QueueShardId shardId,
            QueueLocation location,
            TaskRecord taskRecord
    ) {
        String payload = taskRecord.getPayload();
        JsonNode node = objectMapper.readTree(payload);
        String requestId = node.get(REQUEST_ID_VARIABLE).asText();
        assignRequestId(requestId);
    }

    @Override
    public void executed(
            QueueShardId shardId,
            QueueLocation location,
            TaskRecord taskRecord,
            TaskExecutionResult executionResult,
            long processTaskTime
    ) {
        // do nothing
    }

    @Override
    public void finished(
            QueueShardId shardId,
            QueueLocation location,
            TaskRecord taskRecord
    ) {
        clearRequestId();
    }

    @Override
    public void crashed(
            QueueShardId shardId,
            QueueLocation location,
            TaskRecord taskRecord,
            Exception exc
    ) {
        // do nothing
    }
}
