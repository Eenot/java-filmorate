package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

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


}
