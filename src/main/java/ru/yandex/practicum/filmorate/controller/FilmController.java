package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

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
    public Film addFilm(@NotNull @RequestBody Film film) {
        log.info("Получен POST-запрос: {}", film);
        Film response = filmService.createFilm(film);
        log.info("Добавлен фильм: {}", film.toString());
        return response;
    }

    @PutMapping
    public Film updateFilm(@NotNull @RequestBody Film film) {
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
    public Set<Long> addLike(@PathVariable("id") int filmId,
                             @PathVariable("userId") int userId) {
        log.info("Получен PUT-запрос: пользователь с id \"{}\" оценил фильм с id \"{}\"", userId, filmId);
        Set<Long> response = filmService.addLike(filmId, userId);
        log.info("Обновлён список оценок фильма с id \"{}\". Фильм оценили: {}", filmId, response);
        return response;
    }

    @DeleteMapping("{id}/like/{userId}")
    public Set<Long> removeLike(@PathVariable("id") int filmId,
                                @PathVariable("userId") int userId) {
        log.info("Получен DELETE-запрос: пользователь с id \"{}\" убрал оценку фильма с id \"{}\"", userId, filmId);
        filmService.removeLike(filmId, userId);
        Set<Long> response = filmService.getAllLikes(filmId);
        log.info("Обновлён список оценок фильма с id \"{}\". Фильм оценили: {}", filmId, response);
        return response;
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) int count) {
        log.info("Получен GET-запрос: топ-{} фильмов по популярности.", count);
        List<Film> response = filmService.getPopularFilms(count);
        log.info("Самые популярные фильмы: {}", response);
        return response;
    }
}
