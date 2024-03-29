package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreStorage genres;

    @Autowired
    public GenreService(GenreStorage genres) {
        this.genres = genres;
    }

    public Genre getGenreById(int id) {
        return genres.getGenreById(id);
    }

    public List<Genre> getAllGenres() {
        return genres.getAllGenres();
    }
}
