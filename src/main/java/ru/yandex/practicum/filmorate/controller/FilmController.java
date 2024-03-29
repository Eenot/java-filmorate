package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Количество фильмов: {}", filmService.getAllFilms().size());
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Получен POST-запрос: {}", film);
        Film response = filmService.createFilm(film);
        log.info("Добавлен фильм: {}", film.toString());
        return response;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        filmService.validateFilm(film);
        log.info("Получен PUT-запрос: {}",film);
        Film response = filmService.updateFilm(film);
        log.info("Обновлена информация о фильме: {}", response.getName());
        return response;
    }

    @GetMapping("{id}")
    public Film getFilmById(@PathVariable("id") int filmId) {
        log.info("Получен GET-запрос: фильм с id \"{}\"", filmId);
        Film response = filmService.getFilmById(filmId);
        log.info("Фильм с id \"{}\" : \"{}\"", filmId, response.getName());
        return response;
    }

    @PutMapping("{id}/like/{userId}")
    public Film addLike(@PathVariable("id") int id,
                             @PathVariable("userId") int userId) {
        log.info("Получен PUT-запрос: пользователь с id \"{}\" оценил фильм с id \"{}\"", userId, id);
        Film response = filmService.addLike(id, userId);
        log.info("Обновлён список оценок фильма с id \"{}\".", id);
        return response;
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film removeLike(@PathVariable("id") int id,
                                @PathVariable("userId") int userId) {
        log.info("Получен DELETE-запрос: пользователь с id \"{}\" убрал оценку фильма с id \"{}\"", userId, id);
        log.info("Обновлён список оценок фильма с id \"{}\".", id);
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен GET-запрос: топ-{} фильмов по популярности.", count);
        List<Film> response = filmService.getPopularFilms(count);
        log.info("Самые популярные фильмы: {}", response);
        return response;
    }
}
