package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private static FilmController filmController;
    private static Film film1, film2, film3, film4, film5, film6;
    private static User user1, user2, user3, user4, user5;

    @BeforeAll
    public static void beforeAll() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        UserController userController = new UserController(new UserService(userStorage));
        filmController = new FilmController(new FilmService(filmStorage, userStorage));

        film1 = Film.builder().name("Джентльмены").description("Фильм Гая Ричи").releaseDate(LocalDate.of(2020, 1, 24)).duration(113).build();
        film2 = Film.builder().name("Большой Куш").description("Фильм Гая Ричи").releaseDate(LocalDate.of(2000, 8, 23)).duration(104).build();
        film3 = Film.builder().name("Великий Гэтсби").description("Фильм Лурмана База").releaseDate(LocalDate.of(2013, 5, 1)).duration(143).build();
        film4 = Film.builder().name("Вечное сияние чистого разума").description("Фильм Мишеля Гондри").releaseDate(LocalDate.of(2004, 3, 9)).duration(108).build();
        film5 = Film.builder().name("Драйв").description("Фильм Николаса Виндинга Рефна").releaseDate(LocalDate.of(2011, 11, 3)).duration(100).build();

        user1 = User.builder().id(1).email("kirill@email.ru").login("kirillNew").name("Кирилл").birthday(LocalDate.of(1990, 1, 10)).build();
        user2 = User.builder().id(2).email("denis@email.ru").login("denisNew").name("Денис").birthday(LocalDate.of(1991, 2, 11)).build();
        user3 = User.builder().id(3).email("viktor@email.ru").login("viktorNew").name("Виктор").birthday(LocalDate.of(1992, 3, 12)).build();
        user4 = User.builder().id(4).email("stas@email.ru").login("stasNew").name("Стас").birthday(LocalDate.of(1993, 4, 13)).build();
        user5 = User.builder().id(5).email("igor@email.ru").login("igorNew").name("Игорь").birthday(LocalDate.of(1994, 5, 14)).build();

        filmController.addFilm(film1);
        filmController.addFilm(film2);
        filmController.addFilm(film3);
        filmController.addFilm(film4);
        filmController.addFilm(film5);

        userController.createUser(user1);
        userController.createUser(user2);
        userController.createUser(user3);
        userController.createUser(user4);
        userController.createUser(user5);
    }

    @BeforeEach
    public void beforeEach() {
        film6 = Film.builder().name("Test").description("Test").releaseDate(LocalDate.of(2001, 1, 1)).duration(100).build();
    }

    @Test
    public void shouldNotAddFilmWhenNameIsEmpty() {
        film6.setName("");
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film6));
        String expectedMessage = "Название фильма не должно быть пустым";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddFilmWhenReleaseDateIsIncorrect() {
        film6.setReleaseDate(LocalDate.of(1800,5,5));
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film6));
        String expectedMessage = "Дата релиза должна быть - не раньше 1895-12-28";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddFilmWhenDescriptionLengthIsMoreThan200() {
        char[] chars = new char[205];
        Arrays.fill(chars,'o');
        film6.setDescription(new String(chars));
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film6));
        String expectedMessage = "Максимальная длина описания: 200";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddFilmWhenDurationIsLessThanZero() {
        film6.setDuration(-5);
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.addFilm(film6));
        String expectedMessage = "Продолжительность фильма должна быть положительной";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldUpdateFilm() {
        Film updatedFilm = film1;
        updatedFilm.setDescription("Жанр: криминал/боевик");
        updatedFilm.setId(1);
        filmController.updateFilm(updatedFilm);
        assertEquals(updatedFilm, filmController.getFilmById(updatedFilm.getId()));
        filmController.updateFilm(film1);
    }

    @Test
    public void shouldNotUpdateFilmWhenIdIsIncorrect() {
        Film updatedFilm = Film.builder().id(555).build();
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> filmController.updateFilm(updatedFilm));
        String expectedMessage = "Фильм с id " + updatedFilm.getId() + " не существует!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotGetFilmWhenIdIsIncorrect() {
        int filmId = 555;
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> filmController.getFilmById(filmId));
        String expectedMessage = "Фильм с id " + filmId + " не существует!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldAddLikeFromUserWhenIdIsCorrect() {
        final int film1Id = film1.getId();
        final int film2Id = film2.getId();
        final int user1Id = user1.getId();
        final int user2Id = user2.getId();
        final int user3Id = user3.getId();
        final int user4Id = user4.getId();
        final int user5Id = user5.getId();

        filmController.addLike(film1Id, user1Id);
        filmController.addLike(film1Id, user2Id);
        filmController.addLike(film1Id, user3Id);
        filmController.addLike(film2Id, user4Id);
        filmController.addLike(film2Id, user5Id);

        final Set<Long> expectedLikesFilm1 = new HashSet<>();
        expectedLikesFilm1.add((long) user1Id);
        expectedLikesFilm1.add((long) user2Id);
        expectedLikesFilm1.add((long) user3Id);

        final Set<Long> expectedLikesFilm2 = new HashSet<>();
        expectedLikesFilm2.add((long) user4Id);
        expectedLikesFilm2.add((long) user5Id);

        final Set<Long> actualFilm1Likes = filmController.getFilmById(film1.getId()).getLikes();
        final Set<Long> actualFilm2Likes = filmController.getFilmById(film2.getId()).getLikes();

        assertEquals(expectedLikesFilm1, actualFilm1Likes);
        assertEquals(expectedLikesFilm2, actualFilm2Likes);

        final int count = 2;
        final List<Film> expectedPopularFilms = new ArrayList<>();
        expectedPopularFilms.add(film1);
        expectedPopularFilms.add(film2);

        final List<Film> actualPopularFilms = filmController.getPopularFilms(count);
        assertEquals(expectedPopularFilms, actualPopularFilms);

        filmController.removeLike(film1Id, user1Id);
        expectedLikesFilm1.remove((long) user1Id);
        assertEquals(expectedLikesFilm1, filmController.getFilmById(film1.getId()).getLikes());
    }

    @Test
    public void shouldNotAddLikesWhenFilmIdIsIncorrect() {
        int filmId = 555;
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> filmController.addLike(filmId, user1.getId()));
        String expectedMessage = "Фильм с id \"" + filmId + "\" не существует!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddLikeWhenUserIdIsIncorrect() {
        int userId = 555;
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> filmController.addLike(film1.getId(), userId));
        String expectedMessage = "Пользователь с id \"" + userId + "\" не существует!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}
