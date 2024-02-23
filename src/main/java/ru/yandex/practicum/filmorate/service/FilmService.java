package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.SmthNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    public Film addLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        filmStorage.addLike(userId, filmId);

        return filmStorage.getFilmById(filmId);
    }

    public Film removeLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        if (!filmStorage.getFilmById(filmId).getLikes().contains(userId)) {
            log.info("Пользователь с id {} не ставил лайк фильму с id {}", userId, filmId);
            throw new SmthNotFoundException("Пользователь с id " + userId + " не ставил лайк фильму с id " + filmId);
        }
        filmStorage.removeLike(userId, filmId);

        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
