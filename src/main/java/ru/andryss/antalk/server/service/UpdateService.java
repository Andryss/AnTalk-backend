package ru.andryss.antalk.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.antalk.server.repository.UpdateRepository;

/**
 * Сервис для работы с обновлениями
 */
@Service
@RequiredArgsConstructor
public class UpdateService {

    private final UpdateRepository updateRepository;
}
