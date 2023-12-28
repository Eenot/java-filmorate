package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmManager;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmManager manager;

    public FilmController(@Autowired FilmManager manager) {
        this.manager = manager;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Количество фильмов: {}", manager.getFilms().size());
        return manager.getFilms();
    }

    @PostMapping
    public Film addFilm(@NotNull @RequestBody Film film) {
        log.info("Получен POST-запрос: {}", film);
        Film response = manager.addFilm(film);
        log.info("Добавлен фильм: {}", film.toString());
        return response;
    }

    @PutMapping
    public Film updateFilm(@NotNull @RequestBody Film film) {
        log.info("Получен PUT-запрос: {}",film);
        Film response = manager.updateFilm(film);
        log.info("Обновлена информация о фильма: {}",film.toString());
        return response;
    }
}
