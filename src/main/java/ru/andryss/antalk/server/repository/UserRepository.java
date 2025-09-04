package ru.andryss.antalk.server.repository;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.antalk.server.entity.UserEntity;
import ru.andryss.antalk.server.exception.Errors;

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
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
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
        return findById(id).orElseThrow(() -> Errors.userNotFound(id));
    }

    /**
     * Получить пользователя по имени пользователя
     */
    public Optional<UserEntity> findByUsername(String username) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", username);

        List<UserEntity> updates = jdbcTemplate.query("""
                select * from users
                where username = :username
                """, params, rowMapper
        );

        if (updates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(updates.get(0));
    }

    /**
     * Сохранить пользователя. Вернуть сохраненного пользователя
     */
    public UserEntity save(UserEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", entity.getUsername())
                .addValue("passwordHash", entity.getPasswordHash());

        List<UserEntity> saved = jdbcTemplate.query("""
                insert into users(username, password_hash)
                values (:username, :passwordHash)
                returning *
                """, params, rowMapper);

        return saved.get(0);
    }
}
