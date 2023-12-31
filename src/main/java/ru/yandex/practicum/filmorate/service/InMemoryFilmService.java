package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class InMemoryFilmService implements FilmService {
    private static int filmId = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895,12,28);

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        film.setId(++filmId);
        films.put(filmId, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id {} не существует",film.getId());
            throw new ValidationException("Фильм с id " + film.getId() + " не существует");
        }
        validateFilm(film);
        films.put(film.getId(),film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
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
