package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
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

    public User createUser(User user) {
        validateUser(user);
        return userStorage.createUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId);
    }

    public User updateUser(User user) {
        validateUser(user);
        return userStorage.updateUser(user);
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


    public void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым или содержать пробелы");
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("Имя пользователя пустое, в качестве имени будет использован логин");
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения пользователя некорректна(поле пустое или дата позже текущего момента)");
            throw new ValidationException("Дата рождения пользователя некорректна(поле пустое или дата позже " +
                    "текущего момента)");
        }
    }
}
