package ru.andryss.antalk.server.repository;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.antalk.server.entity.UserEntity;
import ru.andryss.antalk.server.exception.UserNotFoundException;

/**
 * Репозиторий для работы с таблицей "chats"
 */
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<UserEntity> rowMapper = (rs, rowNum) -> {
        UserEntity user = new UserEntity();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        return user;
    };

    /**
     * Получить пользователя по идентификатору
     */
    public Optional<UserEntity> findById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        List<UserEntity> updates = jdbcTemplate.query("""
                select * from users
                where id = :id
                """, params, rowMapper
        );

        if (updates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(updates.get(0));
    }

    /**
     * Получить пользователя по идентификатору. Если пользователь не найден - выбросить ошибку
     */
    public UserEntity findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
