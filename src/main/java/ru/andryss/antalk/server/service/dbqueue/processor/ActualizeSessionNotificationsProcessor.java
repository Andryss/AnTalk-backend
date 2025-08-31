package ru.andryss.antalk.server.service.dbqueue.processor;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ru.andryss.antalk.server.converter.EntityToDtoConverter;
import ru.andryss.antalk.server.entity.NotificationEntity;
import ru.andryss.antalk.server.entity.SessionEntity;
import ru.andryss.antalk.server.entity.SessionStatus;
import ru.andryss.antalk.server.entity.UpdateEntity;
import ru.andryss.antalk.server.generated.model.UpdateDto;
import ru.andryss.antalk.server.repository.NotificationRepository;
import ru.andryss.antalk.server.repository.SessionRepository;
import ru.andryss.antalk.server.repository.UpdateRepository;
import ru.andryss.antalk.server.service.dbqueue.DbQueueProcessor;
import ru.andryss.antalk.server.service.dbqueue.DbQueueSettings;
import ru.yoomoney.tech.dbqueue.api.TaskExecutionResult;

@Component
@RequiredArgsConstructor
@DbQueueSettings(ActualizeSessionNotificationsPayload.QUEUE_NAME)
public class ActualizeSessionNotificationsProcessor implements DbQueueProcessor<ActualizeSessionNotificationsPayload> {

    private static final long BATCH_SIZE = 50;

    private final SessionRepository sessionRepository;
    private final NotificationRepository notificationRepository;
    private final UpdateRepository updateRepository;
    private final EntityToDtoConverter converter;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public TaskExecutionResult execute(ActualizeSessionNotificationsPayload payload) {
        long sessionId = payload.getSessionId();

        boolean needToContinue;
        do {
            needToContinue = sendNextNotificationBatch(sessionId);
        } while (needToContinue);

        return TaskExecutionResult.finish();
    }

    /**
     * Отправляет очередной батч сообщений в обновляющуюся сессию
     * @return true - метод нужно вызвать повторно для отправки следующего батча, false - уведомления актуализированы
     */
    private boolean sendNextNotificationBatch(long sessionId) {
        SessionEntity session = sessionRepository.findByIdOrThrow(sessionId);

        // Check whether sessions status changed (e.g. session disconnected)
        if (session.getStatus() != SessionStatus.UPDATING) {
            return false;
        }

        // Fetch next batch of notifications for session
        long lastNotification = session.getLastNotification();

        List<NotificationEntity> notifications = notificationRepository.findByUserIdAndIdGreaterThen(
                session.getUserId(), lastNotification, BATCH_SIZE
        );

        // Send notifications
        for (NotificationEntity notification : notifications) {
            UpdateEntity update = updateRepository.findByIdOrThrow(notification.getUpdateId());

            UpdateDto message = converter.convertUpdateToDto(update);

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(session.getUserId()),
                    String.format("/session/%s/updates", session.getId()),
                    message
            ); // TODO: extract to messaging service
        }

        // Update last sent notification and session status if needed
        NotificationEntity lastSentNotification = notifications.get(notifications.size() - 1);
        sessionRepository.updateLastNotifications(Map.of(sessionId, lastSentNotification.getId()));

        if (notifications.size() < BATCH_SIZE) {
            sessionRepository.updateStatus(sessionId, SessionStatus.ONLINE);
            return false;
        }

        return true;
    }
}
