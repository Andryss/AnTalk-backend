package ru.andryss.antalk.server.service.dbqueue.processor;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import ru.andryss.antalk.server.converter.EntityToDtoConverter;
import ru.andryss.antalk.server.entity.NotificationEntity;
import ru.andryss.antalk.server.entity.SessionEntity;
import ru.andryss.antalk.server.entity.SessionStatus;
import ru.andryss.antalk.server.generated.model.UpdateDto;
import ru.andryss.antalk.server.repository.NotificationRepository;
import ru.andryss.antalk.server.repository.SessionRepository;
import ru.andryss.antalk.server.repository.UpdateRepository;
import ru.andryss.antalk.server.service.dbqueue.DbQueueProcessor;
import ru.andryss.antalk.server.service.dbqueue.DbQueueSettings;
import ru.yoomoney.tech.dbqueue.api.TaskExecutionResult;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
@DbQueueSettings(SendUpdateNotificationsPayload.QUEUE_NAME)
public class SendUpdateNotificationsProcessor implements DbQueueProcessor<CreateUpdateNotificationsPayload> {

    private final UpdateRepository updateRepository;
    private final NotificationRepository notificationRepository;
    private final SessionRepository sessionRepository;
    private final TransactionTemplate transactionTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final EntityToDtoConverter converter;

    @Override
    public TaskExecutionResult execute(CreateUpdateNotificationsPayload payload) {
        long updateId = payload.getUpdateId();

        // Found notifications to send
        List<NotificationEntity> notifications = notificationRepository.findByUpdateId(updateId);

        List<Long> userIds = notifications.stream()
                .map(NotificationEntity::getUserId)
                .toList();

        // Found sessions to send notifications to
        List<SessionEntity> onlineSessions = sessionRepository.findByStatusAndUserIdIn(SessionStatus.ONLINE, userIds);

        Map<Long, Long> userToNotification = notifications.stream()
                .collect(toMap(NotificationEntity::getUserId, NotificationEntity::getId));

        Map<Long, Long> sessionToNotification = onlineSessions.stream()
                .filter(session -> session.getLastNotification() < userToNotification.get(session.getUserId()))
                .collect(toMap(SessionEntity::getId, session -> userToNotification.get(session.getUserId())));

        // Update sessions and send update
        UpdateDto message = converter.convertUpdateToDto(updateRepository.findByIdOrThrow(updateId));

        transactionTemplate.executeWithoutResult(status -> {
            sessionRepository.updateLastNotifications(sessionToNotification);

            for (SessionEntity session : onlineSessions) {
                if (!sessionToNotification.containsKey(session.getId())) {
                    continue; // skip sessions that don't need to be updated
                }
                messagingTemplate.convertAndSendToUser(
                        String.valueOf(session.getUserId()),
                        String.format("/session/%s/updates", session.getId()),
                        message
                );
            }
        });

        return TaskExecutionResult.finish();
    }
}
