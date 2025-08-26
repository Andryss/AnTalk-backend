package ru.andryss.antalk.server.repository;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.antalk.server.entity.MessageEntity;
import ru.andryss.antalk.server.exception.MessageNotFoundException;

/**
 * Репозиторий для работы с таблицей "messages"
 */
@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<MessageEntity> rowMapper = (rs, rowNum) -> {
        MessageEntity message = new MessageEntity();
        message.setId(rs.getLong("id"));
        message.setChatId(rs.getLong("chat_id"));
        message.setSenderId(rs.getLong("sender_id"));
        message.setText(rs.getString("text"));
        message.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        return message;
    };

    /**
     * Получить сообщение по идентификатору
     */
    public Optional<MessageEntity> findById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        List<MessageEntity> found = jdbcTemplate.query("""
                select * from messages
                where id = :id
                """, params, rowMapper
        );

        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(found.get(0));
    }

    /**
     * Получить сообщение по идентификатору. Если сообщение не найдено - выбросить ошибку
     */
    public MessageEntity findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new MessageNotFoundException(id));
    }

    /**
     * Сохранить сообщение. Вернуть сохраненное сообщение
     */
    public MessageEntity save(MessageEntity message) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("senderId", message.getSenderId())
                .addValue("chatId", message.getChatId())
                .addValue("text", message.getText());

        List<MessageEntity> saved = jdbcTemplate.query("""
                insert into messages(sender_id, chat_id, text)
                values (:senderId, :chatId, :text)
                returning *
                """, params, rowMapper);

        return saved.get(0);
    }
}
