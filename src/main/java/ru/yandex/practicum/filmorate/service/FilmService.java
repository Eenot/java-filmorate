package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private static int filmId = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film createFilm(Film film) {
        return this.filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return this.filmStorage.updateFilm(film);
    }

    public Film getFilmById(int filmId) {
        return this.filmStorage.getFilmById(filmId);
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(this.filmStorage.getAllFilms().values());
    }

    public Set<Long> getAllLikes(int filmId) {
        return this.filmStorage.getFilmById(filmId).getLikes();
    }

    public Set<Long> addLike(int filmId, int userId) {
        checkFilmAndUserIds(filmId, userId);
        filmStorage.getFilmById(filmId).addLike(userId);
        return filmStorage.getFilmById(filmId).getLikes();
    }

    public Set<Long> removeLike(int filmId, int userId) {
        checkFilmAndUserIds(filmId,userId);
        filmStorage.getFilmById(filmId).removeLike(userId);
        return filmStorage.getFilmById(filmId).getLikes();
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().values().stream()
                .sorted((film1,film2) -> compare(film1, film2))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film film1, Film film2) {
        return Integer.compare(film2.getLikes().size(), film1.getLikes().size());
    }

    private void checkFilmAndUserIds(int filmId, int userId) {
        if (!filmStorage.getAllFilms().containsKey(filmId)) {
            log.error("Фильм с id {} не существует!", filmId);
            throw new FilmNotFoundException(String.format("Фильм с id \"%s\" не существует!", filmId));
        }

        if (!userStorage.getAllUsers().containsKey(userId)) {
            log.error("Пользователь с id {} не существует!", userId);
            throw new UserNotFoundException(String.format("Пользователь с id \"%s\" не существует!", userId));
        }
    }

}
