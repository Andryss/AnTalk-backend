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
import ru.andryss.antalk.server.entity.UpdateEntity;
import ru.andryss.antalk.server.entity.UpdateType;
import ru.andryss.antalk.server.service.ObjectMapperWrapper;

/**
 * Репозиторий для работы с таблицей "updates"
 */
@Repository
@RequiredArgsConstructor
public class UpdateRepository implements InitializingBean {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapperWrapper objectMapper;

    private RowMapper<UpdateEntity> rowMapper;

    @Override
    public void afterPropertiesSet() {
        rowMapper = (rs, rowNum) -> {
            UpdateEntity update = new UpdateEntity();
            update.setId(rs.getLong("id"));
            update.setType(UpdateType.fromId(rs.getInt("type")));
            update.setData(objectMapper.readValue(rs.getString("data"), new TypeReference<>() {}));
            update.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            return update;
        };
    }

    /**
     * Получить обновление по идентификатору
     */
    public Optional<UpdateEntity> findById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        List<UpdateEntity> updates = jdbcTemplate.query("""
                select * from updates
                where id = :id
                """, params, rowMapper
        );

        if (updates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(updates.get(0));
    }
}
