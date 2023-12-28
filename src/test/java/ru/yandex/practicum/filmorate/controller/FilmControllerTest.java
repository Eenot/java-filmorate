package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.InMemoryFilmManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private static Film film;
    private static FilmController controller;

    @BeforeEach
    public void beforeEach() {
        controller = new FilmController(new InMemoryFilmManager());
        film = Film.builder()
                .name("Test film")
                .description("Test film description")
                .releaseDate(LocalDate.of(2007,7,7))
                .duration(100)
                .build();
    }

    @Test
    public void shouldAddFilm() {
        controller.addFilm(film);
        List<Film> expectedFilms = List.of(film);
        List<Film> savedFilms = controller.getFilms();
        assertEquals(expectedFilms,savedFilms);
        assertEquals(1,controller.getFilms().size());
    }

    @Test
    public void shouldNotAddFilmWhenNameIsEmpty() {
        film.setName("");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
        String expectedMessage = "Название фильма не должно быть пустым";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddFilmWhenReleaseDateIsIncorrect() {
        film.setReleaseDate(LocalDate.of(1800,5,5));
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
        String expectedMessage = "Дата релиза должна быть - не раньше 1895-12-28";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddFilmWhenDescriptionLengthIsMoreThan200() {
        char[] chars = new char[205];
        Arrays.fill(chars,'o');
        film.setDescription(new String(chars));
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
        String expectedMessage = "Максимальная длина описания: 200";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddFilmWhenDurationIsLessThanZero() {
        film.setDuration(-5);
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
        String expectedMessage = "Продолжительность фильма должна быть положительной";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldUpdateFilm() {
        controller.addFilm(film);

        Film updatedFilm = film;
        updatedFilm.setName("New test film");
        updatedFilm.setDescription("Updated test film");
        updatedFilm.setReleaseDate(LocalDate.of(2000,5,9));
        updatedFilm.setDuration(55);
        controller.updateFilm(updatedFilm);
        assertEquals(updatedFilm.toString(),controller.getFilms().get(0).toString());
    }

    @Test
    public void shouldNotUpdateFilmWhenIdIsIncorrect() {
        controller.addFilm(film);

        Film updatedFilm = film;
        updatedFilm.setId(555);
        updatedFilm.setName("New test film");
        updatedFilm.setDescription("Updated test film");
        updatedFilm.setReleaseDate(LocalDate.of(2000,5,9));
        updatedFilm.setDuration(55);

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.updateFilm(updatedFilm));
        String expectedMessage = "Фильм с id " + updatedFilm.getId() + " не существует";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}
