package ru.andryss.antalk.server.util;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.andryss.antalk.server.entity.ChatEntity;
import ru.andryss.antalk.server.entity.ChatType;
import ru.andryss.antalk.server.entity.MessageEntity;
import ru.andryss.antalk.server.entity.UpdateEntity;
import ru.andryss.antalk.server.repository.ChatRepository;
import ru.andryss.antalk.server.repository.MessageRepository;
import ru.andryss.antalk.server.repository.UpdateRepository;
import ru.andryss.antalk.server.repository.UserRepository;
import ru.andryss.antalk.server.service.ObjectMapperWrapper;

@Component
public class DbTestUtil {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UpdateRepository updateRepository;

    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    ObjectMapperWrapper objectMapper;

    public void saveUser(long id, String username, String passwordHash) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("username", username)
                .addValue("passwordHash", passwordHash);

        jdbcTemplate.update("""
                insert into users(id, username, password_hash)
                values (:id, :username, :passwordHash)
                """, params);
    }

    public ChatEntity findChatById(long id) {
        return chatRepository.findByIdOrThrow(id);
    }

    public void saveChat(long id, ChatType type, List<Long> userIds) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("type", type.getId())
                .addValue("userIds", objectMapper.writeValueAsString(userIds));

        jdbcTemplate.update("""
                insert into chats(id, type, user_ids)
                values (:id, :type, :userIds::jsonb)
                """, params);
    }

    public MessageEntity findMessageById(long id) {
        return messageRepository.findByIdOrThrow(id);
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
