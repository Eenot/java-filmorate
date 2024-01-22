package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        return this.userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return this.userStorage.updateUser(user);
    }

    public User getUserById(int userId) {
        return this.userStorage.getUserById(userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(this.userStorage.getAllUsers().values());
    }

    public Set<Long> addFriend(int userId, int friendId) {
        checkUsersIds(userId, friendId);
        userStorage.getUserById(userId).addFriend(friendId);
        userStorage.getUserById(friendId).addFriend(userId);
        return userStorage.getUserById(userId).getFriends();
    }

    public Set<Long> removeFriend(int userId, int friendId) {
        checkUsersIds(userId, friendId);
        userStorage.getUserById(userId).removeFriend(friendId);
        userStorage.getUserById(friendId).removeFriend(userId);
        return userStorage.getUserById(userId).getFriends();
    }

    public List<User> getUsersFriends(int userId) {
        Set<Long> friendsIds = userStorage.getUserById(userId).getFriends();
        return userStorage.getAllUsers().values().stream()
                .filter(user -> friendsIds.contains((long) user.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(int userId, int otherId) {
        checkUsersIds(userId, otherId);
        Set<Long> userFriends = userStorage.getUserById(userId).getFriends();
        Set<Long> otherUserFriends = userStorage.getUserById(otherId).getFriends();

        Set<Long> coincidences = new HashSet<>(userFriends);
        coincidences.retainAll(otherUserFriends);

        return userStorage.getAllUsers().values().stream()
                .filter(user -> coincidences.contains((long) user.getId()))
                .collect(Collectors.toList());
    }

    private void checkUsersIds(int userId, int friendId) {
        if (!userStorage.getAllUsers().containsKey(userId)) {
            log.error("Пользователь с id {} не существует!", userId);
            throw new UserNotFoundException(String.format("Пользователь с id \"%s\" не существует!", userId));
        }
        if (!userStorage.getAllUsers().containsKey(friendId)) {
            log.error("Пользователь с id {} не существует!", friendId);
            throw new UserNotFoundException(String.format("Пользователь с id \"%s\" не существует!", friendId));
        }
    }
}
