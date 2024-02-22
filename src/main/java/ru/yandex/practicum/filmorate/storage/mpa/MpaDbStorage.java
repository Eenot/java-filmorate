package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.SmthNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpas() {
        String sqlQuery = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlQuery, this::makeMpa);
    }

    @Override
    public Mpa getMpaById(int id) {
        String sqlQuery = "SELECT * FROM mpa WHERE mpa_id = ?";
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject(sqlQuery, this::makeMpa, id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Тип рейтинга с id {} не существует!", id);
            throw new SmthNotFoundException("Тип рейтинга с id " + id + " не существует!");
        }
        return mpa;
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
}
