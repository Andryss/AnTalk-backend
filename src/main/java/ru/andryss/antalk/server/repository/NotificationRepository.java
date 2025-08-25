package ru.andryss.antalk.server.repository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.antalk.server.entity.NotificationEntity;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void save(List<NotificationEntity> entities) {
        MapSqlParameterSource[] paramsList = entities.stream()
                .map(entity -> new MapSqlParameterSource()
                        .addValue("userId", entity.getUserId())
                        .addValue("updateId", entity.getUpdateId())
                )
                .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate("""
                insert into notifications(user_id, update_id)
                values (:userId, :updateId)
                returning *
                """, paramsList
        );
    }
}
