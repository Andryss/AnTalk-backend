package ru.andryss.antalk.server.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.antalk.server.entity.SessionEntity;
import ru.andryss.antalk.server.entity.SessionStatus;
import ru.andryss.antalk.server.exception.SessionNotFoundException;
import ru.andryss.antalk.server.service.ObjectMapperWrapper;

/**
 * Репозиторий для работы с таблицей "sessions"
 */
@Repository
@RequiredArgsConstructor
public class SessionRepository implements InitializingBean {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapperWrapper objectMapper;

    private RowMapper<SessionEntity> rowMapper;

    @Override
    public void afterPropertiesSet() {
        rowMapper = (rs, rowNum) -> {
            SessionEntity session = new SessionEntity();
            session.setId(rs.getLong("id"));
            session.setUserId(rs.getLong("user_id"));
            session.setMeta(objectMapper.readMap(rs.getString("meta")));
            session.setStatus(SessionStatus.fromId(rs.getInt("status")));
            session.setLastNotification(rs.getLong("last_notification"));
            session.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            return session;
        };
    }

    /**
     * Найти все сессии с заданным статусом и пользователем из заданного списка
     */
    public List<SessionEntity> findByStatusAndUserIdIn(SessionStatus status, List<Long> userIds) {
        if (userIds.isEmpty()) {
            return List.of();
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", status.getId())
                .addValue("userIds", userIds);

        return jdbcTemplate.query("""
                select * from sessions
                where status = :status and user_id in (:userIds)
                """, params, rowMapper);
    }

    /**
     * Обновить последнее полученное уведомление для заданных сессий
     */
    public void updateLastNotifications(Map<Long, Long> sessionToLastNotification) {
        MapSqlParameterSource[] paramsList = sessionToLastNotification.entrySet().stream()
                .map(entry -> new MapSqlParameterSource()
                        .addValue("id", entry.getKey())
                        .addValue("lastNotification", entry.getValue()))
                .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate("""
                update sessions set last_notification = :lastNotification
                where id = :id
                """, paramsList);
    }

    /**
     * Получить сессию по идентификатору
     */
    public Optional<SessionEntity> findById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        List<SessionEntity> found = jdbcTemplate.query("""
                select * from sessions
                where id = :id
                """, params, rowMapper
        );

        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(found.get(0));
    }

    /**
     * Получить сессию по идентификатору. Если сессия не найдена - выбросить ошибку
     */
    public SessionEntity findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new SessionNotFoundException(id));
    }

    /**
     * Обновить статус заданной сессии
     */
    public void updateStatus(long id, SessionStatus status) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("newStatus", status.getId());

        jdbcTemplate.update("""
                update sessions set status = :newStatus
                where id = :id
                """, params);
    }
}
