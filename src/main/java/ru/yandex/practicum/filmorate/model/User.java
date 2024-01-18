package ru.yandex.practicum.filmorate.model;

import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {

    @Builder.Default
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();

    public void addFriend(int friendId) {
        friends.add((long) friendId);
    }

    public void removeFriend(int friendId) {
        friends.remove((long) friendId);
    }
}
