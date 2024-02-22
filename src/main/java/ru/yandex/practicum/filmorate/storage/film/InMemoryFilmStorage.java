package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.SmthNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private static int idCounter = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895,12,28);

    @Override
    public Film createFilm(Film film) {
        if (films.containsKey(film.getId())) {
            log.error("Фильм с id {} уже существует!", film.getId());
            throw new ValidationException("Фильм с id " + film.getId() + " уже существует!");
        }
        idCounter++;
        validateFilm(film);
        film.setId(idCounter);
        films.put(idCounter,film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmId(film.getId());
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(int filmId) {
        checkFilmId(filmId);
        return this.films.get(filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(int id, int userId) {
    }

    @Override
    public void removeLike(int id, int userId) {
    }

    private void checkFilmId(int filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Фильм с id {} не существует!", filmId);
            throw new SmthNotFoundException("Фильм с id " + filmId + " не существует!");
        }
    }


    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не должно быть пустым");
            throw new ValidationException("Название фильма не должно быть пустым");
        }
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.error("Максимальная длина описания: {}", MAX_DESCRIPTION_LENGTH);
            throw new ValidationException("Максимальная длина описания: " + MAX_DESCRIPTION_LENGTH);
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Дата релиза должна быть - не раньше {}", MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза должна быть - не раньше " + MIN_RELEASE_DATE.toString());
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}

