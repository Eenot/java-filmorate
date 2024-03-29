package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.exception.SmthNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = new GenreDbStorage(jdbcTemplate);
    }


    @Override
    public Film createFilm(Film film) {
        int filmId = addFilmToDb(film);
        film.setId(filmId);
        Set<Genre> genres = film.getGenres();
        if (!genres.isEmpty()) {
            insertFilmGenres(film.getId(), genres);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmId(film.getId());
        String sqlQuery = "UPDATE films SET " +
                "name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE film_id=?";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        String sqlQuery2 = "DELETE FROM film_genre WHERE film_id=?";
        jdbcTemplate.update(sqlQuery2, film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Genre> genres = film.getGenres();
            insertFilmGenres(film.getId(), genres);
        }
        return film;
    }

    @Override
    public Film getFilmById(int filmId) {
        checkFilmId(filmId);
        String sqlQuery = "SELECT f.*, mpa.mpa_name FROM films AS f JOIN mpa ON f.mpa_id=mpa.mpa_id WHERE f.film_id=?";
        return jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT f.*, mpa.mpa_name FROM films AS f JOIN mpa ON f.mpa_id=mpa.mpa_id";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public void addLike(int userId, int filmId) {
        checkFilmId(filmId);
        String sqlQuery = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, userId, filmId);
        } catch (DuplicateKeyException e) {
            log.info("Нельзя оценить один и тот же фильм дважды!");
            throw new DuplicateDataException("Нельзя оценить один и тот же фильм дважды!");
        }
    }

    @Override
    public void removeLike(int userId, int filmId) {
        checkFilmId(filmId);
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        if (jdbcTemplate.update(sqlQuery,userId,filmId) == 0) {
            log.info("Пользователь с id {} не ставил оценку фильму с id {}", userId, filmId);
            throw new SmthNotFoundException("Пользователь с id " + userId + " не ставил оценку фильму с id " + filmId);
        }
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                .build();

        String sqlQuery = "SELECT user_id FROM likes WHERE film_id=?";
        film.getLikes().addAll(jdbcTemplate.query(
                        sqlQuery,
                        (rs1, rowNum1) -> rs1.getInt("user_id"),
                        film.getId()
                )
        );
        film.getGenres().addAll(genreDbStorage.getGenresForFilm(film.getId()));
        return film;
    }

    private int addFilmToDb(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        return simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
    }

    private boolean dbContainsFilm(int filmId) {
        String sqlQuery = "SELECT f.*, mpa.mpa_name FROM films AS f JOIN mpa ON f.mpa_id=mpa.mpa_id WHERE f.film_id=?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, filmId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private void insertFilmGenres(int filmId, Set<Genre> genres) {
        String sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?,?)";
        jdbcTemplate.batchUpdate(
                sqlQuery,
                genres,
                genres.size(),
                (PreparedStatement ps, Genre genre) -> {
                    ps.setInt(1, filmId);
                    ps.setInt(2, genre.getId());
                });
    }

    private void checkFilmId(int filmId) {
        if (!dbContainsFilm(filmId)) {
            log.error("Фильм с id {} не существует!", filmId);
            throw new SmthNotFoundException("Фильм с id " + filmId + " не существует!");
        }
    }
}
