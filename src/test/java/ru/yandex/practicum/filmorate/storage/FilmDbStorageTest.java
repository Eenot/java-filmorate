package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.SmthNotFoundException;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    private FilmStorage filmStorage;
    private Film film;
    private Film film2;
    private User user;

    @BeforeEach
    public void beforeEach() {
        filmStorage = new FilmDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
        film = Film.builder()
                .name("Джентльмены")
                .description("Фильм Гая Ричи")
                .releaseDate(LocalDate.of(2020,1,24))
                .duration(113)
                .mpa(new Mpa(4, "R"))
                .build();
        film2 = Film.builder()
                .name("Великий Гэтсби")
                .description("Фильм Лурмана База")
                .releaseDate(LocalDate.of(2013, 5, 1))
                .duration(143)
                .mpa(new Mpa(3, "PG-13"))
                .build();
        user = User.builder()
                .email("viktor@email.ru")
                .login("viktorNew")
                .name("Виктор")
                .birthday(LocalDate.of(1992, 3, 12))
                .build();

        filmStorage.createFilm(film);
        filmStorage.createFilm(film2);
        userStorage.createUser(user);
    }

    @Test
    public void shouldAddFilm() {
        Film savedFilm = filmStorage.getFilmById(film.getId());

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void shouldThrowExceptionWhenIdIsIncorrect() {
        final SmthNotFoundException exception = assertThrows(SmthNotFoundException.class, () -> filmStorage.getFilmById(111));

        assertEquals(exception.getMessage(), "Фильм с id 111 не существует!");
    }

    @Test
    public void shouldUpdateFilm() {
        film2.setId(1);
        filmStorage.createFilm(film);
        filmStorage.updateFilm(film2);
        Film savedFilm = filmStorage.getFilmById(1);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film2);
    }

    @Test
    public void shouldNotUpdateFilmWhenIdIsIncorrect() {
        film2.setId(111);

        final SmthNotFoundException exception = assertThrows(
                SmthNotFoundException.class,
                () -> filmStorage.updateFilm(film2)
        );

        assertEquals(exception.getMessage(), "Фильм с id 111 не существует!");
    }

    @Test
    public void shouldAddLike() {
        filmStorage.addLike(film.getId(), user.getId());

        Film savedFilm = filmStorage.getFilmById(film.getId());

        assertNotNull(savedFilm);
        assertThat(savedFilm.getLikes()).isNotNull().isEqualTo(Set.of(1));
    }

    @Test
    public void shouldThrowExceptionWhenUserTryToLikeTwice() {
        filmStorage.addLike(film.getId(), user.getId());

        final DuplicateDataException exception = assertThrows(
                DuplicateDataException.class,
                () -> filmStorage.addLike(film.getId(), user.getId())
        );

        assertEquals(exception.getMessage(), "Нельзя оценить один и тот же фильм дважды!");
    }

    @Test
    public void shouldRemoveLike() {
        filmStorage.addLike(film.getId(), user.getId());
        filmStorage.removeLike(film.getId(), user.getId());

        Film savedFilm = filmStorage.getFilmById(film.getId());

        assertNotNull(savedFilm);
        assertTrue(savedFilm.getLikes().isEmpty());
    }

    @Test
    public void shouldThrowExceptionWhenTryingToRemoveNotExistedLike() {
        final SmthNotFoundException exception = assertThrows(
                SmthNotFoundException.class,
                () -> filmStorage.removeLike(film.getId(), user.getId())
        );

        assertEquals(exception.getMessage(), "Пользователь с id 1 не ставил оценку фильму с id 1");
    }

    @Test
    public void shouldGetFilms() {
        List<Film> savedFilms = filmStorage.getAllFilms();

        assertThat(savedFilms)
                .isNotNull()
                .isEqualTo(List.of(film,film2));
    }

    @Test
    public void shouldGetFilmById() {
        Film savedFilm = filmStorage.getFilmById(film.getId());

        assertThat(savedFilm)
                .isNotNull()
                .isEqualTo(film);
    }
}
