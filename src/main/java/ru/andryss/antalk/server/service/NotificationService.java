package ru.andryss.antalk.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.antalk.server.repository.NotificationRepository;

/**
 * Сервис для работы с уведомлениями
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
}
