package ru.andryss.antalk.server.repository;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.antalk.server.entity.NotificationEntity;
import ru.andryss.antalk.server.exception.NotificationNotFoundException;

/**
 * Репозиторий для работы с таблицей "notifications"
 */
@Repository
@RequiredArgsConstructor
public class NotificationRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<NotificationEntity> rowMapper = (rs, rowNum) -> {
        NotificationEntity notification = new NotificationEntity();
        notification.setId(rs.getLong("id"));
        notification.setUserId(rs.getLong("user_id"));
        notification.setUpdateId(rs.getLong("update_id"));
        notification.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        return notification;
    };

    /**
     * Сохранить сущности уведомлений
     */
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
                """, paramsList
        );
    }

    /**
     * Получить уведомление по идентификатору
     */
    public Optional<NotificationEntity> findById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        List<NotificationEntity> found = jdbcTemplate.query("""
                select * from notifications
                where id = :id
                """, params, rowMapper
        );

        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(found.get(0));
    }

    /**
     * Получить уведомление по идентификатору. Если уведомление не найдено - выбросить ошибку
     */
    public NotificationEntity findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new NotificationNotFoundException(id));
    }

    /**
     * Получить уведомления по заданному обновлению
     */
    public List<NotificationEntity> findByUpdateId(long updateId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("updateId", updateId);

        return jdbcTemplate.query("""
                select * from notifications
                where update_id = :updateId
                """, params, rowMapper);
    }

    /**
     * Найти уведомления по заданному пользователю.
     * Отфильтровать только с большим идентификатором и вернуть заданное количество
     */
    public List<NotificationEntity> findByUserIdAndIdGreaterThen(long userId, long id, long limit) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("idGreater", id)
                .addValue("limit", limit);

        return jdbcTemplate.query("""
                select * from notifications
                where user_id = :userId and id > :idGreater
                order by id
                limit :limit
                """, params, rowMapper);
    }
}
