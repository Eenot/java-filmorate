package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.SmthNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class GenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private GenreStorage genreStorage;

    private final Genre genre = new Genre(5, "Документальный");

    private final List<Genre> genresList = List.of(
            new Genre(1, "Комедия"),
            new Genre(2, "Драма"),
            new Genre(3, "Мультфильм"),
            new Genre(4, "Триллер"),
            new Genre(5, "Документальный"),
            new Genre(6, "Боевик")
    );

    @BeforeEach
    public void beforeEach() {
        genreStorage = new GenreDbStorage(jdbcTemplate);
    }

    @Test
    public void shouldGetGenreById() {
        Genre savedGenre = genreStorage.getGenreById(5);

        assertThat(savedGenre)
                .isNotNull()
                .isEqualTo(genre);
    }

    @Test
    public void shouldThrowExceptionWhenIdIsIncorrect() {
        final SmthNotFoundException exception = assertThrows(
                SmthNotFoundException.class,
                () -> genreStorage.getGenreById(111)
        );

        assertEquals(exception.getMessage(), "Жанр с id 111 не существует!");
    }

    @Test
    public void shouldGetAllGenres() {
        List<Genre> savedGenres = genreStorage.getAllGenres().stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toList());

        assertThat(savedGenres)
                .isNotNull()
                .isEqualTo(genresList);
    }
}
