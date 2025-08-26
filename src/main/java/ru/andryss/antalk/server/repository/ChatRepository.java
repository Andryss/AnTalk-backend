package ru.andryss.antalk.server.repository;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.antalk.server.entity.ChatEntity;
import ru.andryss.antalk.server.entity.ChatType;
import ru.andryss.antalk.server.service.ObjectMapperWrapper;

/**
 * Репозиторий для работы с таблицей "chats"
 */
@Repository
@RequiredArgsConstructor
public class ChatRepository implements InitializingBean {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapperWrapper objectMapper;

    private RowMapper<ChatEntity> rowMapper;

    @Override
    public void afterPropertiesSet() {
        rowMapper = (rs, rowNum) -> {
            ChatEntity chat = new ChatEntity();
            chat.setId(rs.getLong("id"));
            chat.setType(ChatType.fromId(rs.getInt("type")));
            chat.setUserIds(objectMapper.readValue(rs.getString("user_ids"), new TypeReference<>() {}));
            chat.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            return chat;
        };
    }

    /**
     * Получить чат по идентификатору
     */
    public Optional<ChatEntity> findById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        List<ChatEntity> updates = jdbcTemplate.query("""
                select * from chats
                where id = :id
                """, params, rowMapper
        );

        if (updates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(updates.get(0));
    }
}
