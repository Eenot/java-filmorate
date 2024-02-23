package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
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
        log.info("Количество фильмов: {}", filmService.getFilmStorage().getAllFilms().size());
        return filmService.getFilmStorage().getAllFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validate(film);
        log.info("Получен POST-запрос: {}", film);
        Film response = filmService.getFilmStorage().createFilm(film);
        log.info("Добавлен фильм: {}", film.toString());
        return response;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validate(film);
        log.info("Получен PUT-запрос: {}",film);
        Film response = filmService.getFilmStorage().updateFilm(film);
        log.info("Обновлена информация о фильме: {}", response.getName());
        return response;
    }

    @GetMapping("{id}")
    public Film getFilmById(@PathVariable("id") int filmId) {
        log.info("Получен GET-запрос: фильм с id \"{}\"", filmId);
        Film response = filmService.getFilmStorage().getFilmById(filmId);
        log.info("Фильм с id \"{}\" : \"{}\"", filmId, response.getName());
        return response;
    }

    @PutMapping("{id}/like/{userId}")
    public Film addLike(@PathVariable("id") int filmId,
                             @PathVariable("userId") int userId) {
        log.info("Получен PUT-запрос: пользователь с id \"{}\" оценил фильм с id \"{}\"", userId, filmId);
        Film response = filmService.addLike(filmId, userId);
        log.info("Обновлён список оценок фильма с id \"{}\".", filmId);
        return response;
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film removeLike(@PathVariable("id") int filmId,
                                @PathVariable("userId") int userId) {
        log.info("Получен DELETE-запрос: пользователь с id \"{}\" убрал оценку фильма с id \"{}\"", userId, filmId);
        log.info("Обновлён список оценок фильма с id \"{}\".", filmId);
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен GET-запрос: топ-{} фильмов по популярности.", count);
        List<Film> response = filmService.getPopularFilms(count);
        log.info("Самые популярные фильмы: {}", response);
        return response;
    }

    private void validate(Film film) {
        String msg;
        if (film.getName() == null || film.getName().isBlank()) {
            msg = "Имя фильма не может быть пустым.";
            log.error(msg);
            throw new ValidationException(msg);
        }
        if (film.getDescription().length() > 200) {
            msg = "Описание не может быть длиной более 200 символов!.";
            log.error(msg);
            throw new ValidationException(msg);
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            msg = "Дата выхода фильма не может быть раньше чем 28.12.1895.";
            log.error(msg);
            throw new ValidationException(msg);
        }
        if (film.getDuration() <= 0) {
            msg = "Продолжительность фильма должна быть положительной";
            log.error(msg);
            throw new ValidationException(msg);
        }
    }
}
