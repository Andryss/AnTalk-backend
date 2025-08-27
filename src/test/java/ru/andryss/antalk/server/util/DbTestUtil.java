package ru.andryss.antalk.server.util;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.andryss.antalk.server.entity.ChatEntity;
import ru.andryss.antalk.server.entity.UpdateEntity;
import ru.andryss.antalk.server.repository.ChatRepository;
import ru.andryss.antalk.server.repository.UpdateRepository;
import ru.andryss.antalk.server.service.ObjectMapperWrapper;

@Component
public class DbTestUtil {

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    UpdateRepository updateRepository;

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    ObjectMapperWrapper objectMapper;

    public ChatEntity findChatById(long id) {
        return chatRepository.findByIdOrThrow(id);
    }

    public UpdateEntity findUpdateById(long id) {
        return updateRepository.findByIdOrThrow(id);
    }

    public List<Map<String, Object>> findTasksByQueue(String queue) {
        List<String> rawPayloads = jdbcTemplate.queryForList("""
                select payload from queue_tasks
                where queue_name = :queue
                """, Map.of("queue", queue), String.class);

        return rawPayloads.stream()
                .map(payload -> objectMapper.readMap(payload))
                .toList();
    }
}
