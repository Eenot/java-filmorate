package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int filmId);

    List<Film> getAllFilms();

    void addLike(int id, int userId);

    void removeLike(int id, int userId);
}
