package ru.andryss.antalk.server.repository;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.antalk.server.entity.MessageEntity;

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
}
