package ru.andryss.antalk.server.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.antalk.server.entity.NotificationEntity;
import ru.andryss.antalk.server.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void save(List<NotificationEntity> entities) {
        notificationRepository.save(entities);
    }
}
