package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder
public class Film {

    @Builder.Default
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    private final Set<Long> likes = new HashSet<>();

    public void addLike(int userId) {
        this.likes.add((long) userId);
    }

    public void removeLike(int userId) {
        this.likes.remove((long) userId);
    }
}
