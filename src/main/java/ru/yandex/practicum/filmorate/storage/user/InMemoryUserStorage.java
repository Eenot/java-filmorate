package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.SmthNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private static int userId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        if (users.containsKey(user.getId())) {
            log.error("Пользователь с id {} уже существует!", user.getId());
            throw new ValidationException("Пользователь с id " + user.getId() + " уже существует!");
        }
        validateUser(user);
        user.setId(++userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUserId(userId);
        validateUser(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(int userId) {
        checkUserId(userId);
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
       return new ArrayList<>(users.values());
    }

    @Override
    public User addFriend(int userId, int friendId) {
        checkUserId(userId);
        checkUserId(friendId);
        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);

        user1.getFriends().add(friendId);
        user2.getFriends().add(userId);
        log.info("Пользователь с id={} добавлен в список друзей пользователя с id={}", friendId, userId);
        return user1;
    }

    @Override
    public User removeFriend(int userId, int friendId) {
        checkUserId(userId);
        checkUserId(friendId);
        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);

        if (!user1.getFriends().contains(friendId)) {
            log.info("Пользователя с id={} нет в списке друзей пользователя с id={}", friendId, userId);
            throw new SmthNotFoundException("Пользователя с id=" + friendId + " нет в списке друзей пользователя с id=" + userId);
        }

        user1.getFriends().remove(friendId);
        user2.getFriends().remove(userId);
        log.info("Пользователь с id={} добавлен в список друзей пользователя с id={}", friendId, userId);
        return user1;
    }

    @Override
    public List<Integer> getFriends(int id) {
        return new ArrayList<>(getUserById(id).getFriends());
    }

    private void checkUserId(int userId) {
        if (!users.containsKey(userId)) {
            log.error("Пользователь с id {} не существует!", userId);
            throw new SmthNotFoundException("Пользователь с id " + userId + " не существует!");
        }
    }

    private void validateUser(User user) {
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
