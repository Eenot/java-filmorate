package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public User addFriend(int userId, int friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public User removeFriend(int userId, int friendId) {
        return userStorage.removeFriend(userId, friendId);
    }

    public List<User> getUsersFriends(int userId) {
        return userStorage.getFriends(userId).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(int userId, int otherId) {
        Set<Integer> friends = new HashSet<>(userStorage.getFriends(userId));
        Set<Integer> otherFriends = new HashSet<>(userStorage.getFriends(otherId));

        friends.retainAll(otherFriends);
        return friends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
