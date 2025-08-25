package ru.andryss.antalk.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.antalk.server.entity.UpdateEntity;
import ru.andryss.antalk.server.exception.UpdateNotFoundException;
import ru.andryss.antalk.server.repository.UpdateRepository;

@Service
@RequiredArgsConstructor
public class UpdateService {

    private final UpdateRepository updateRepository;

    public UpdateEntity findByIdOrThrow(long id) {
        return updateRepository.findById(id)
                .orElseThrow(() -> new UpdateNotFoundException(id));
    }
}
