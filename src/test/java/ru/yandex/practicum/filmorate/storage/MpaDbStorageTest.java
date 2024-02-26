package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.SmthNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class MpaDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private MpaStorage mpaStorage;
    private final Mpa mpa = new Mpa(3, "PG-13");
    private final List<Mpa> mpasList = List.of(
            new Mpa(1, "G"),
            new Mpa(2, "PG"),
            new Mpa(3, "PG-13"),
            new Mpa(4, "R"),
            new Mpa(5, "NC-17")
    );

    @BeforeEach
    public void beforeEach() {
        mpaStorage = new MpaDbStorage(jdbcTemplate);
    }

    @Test
    public void shouldGetMpaById() {
        Mpa savedMpa = mpaStorage.getMpaById(3);

        assertThat(savedMpa)
                .isNotNull()
                .isEqualTo(mpa);
    }

    @Test
    public void shouldGetAllMpas() {
        List<Mpa> savedMpas = mpaStorage.getAllMpas();

        assertThat(savedMpas)
                .isNotNull()
                .isEqualTo(mpasList);
    }

    @Test
    public void shouldThrowExceptionWhenIdIsIncorrect() {
        final SmthNotFoundException exception = assertThrows(
                SmthNotFoundException.class,
                () -> mpaStorage.getMpaById(111)
        );

        assertEquals(exception.getMessage(), "Тип рейтинга с id 111 не существует!");
    }
}
