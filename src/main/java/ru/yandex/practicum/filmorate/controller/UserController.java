package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Количество пользователей: {}", userService.getUserStorage().getAllUsers().size());
        return userService.getUserStorage().getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Получен POST-запрос: {}", user);
        validateUser(user);
        User response = userService.getUserStorage().createUser(user);
        log.info("Добавлен пользователь: {}", user.toString());
        return response;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Получен PUT-запрос: {}", user);
        validateUser(user);
        User response = userService.getUserStorage().updateUser(user);
        log.info("Обновлён пользователь: {}", user.toString());
        return response;
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable("id") Integer userId) {
        log.info("Получен GET-запрос: пользователь с id \"{}\"", userId);
        User response = userService.getUserStorage().getUserById(userId);
        log.info("Пользователь с id \"{}\" : \"{}\"", userId, response.toString());
        return response;
    }

    @PutMapping("{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") int userId,
                               @PathVariable("friendId") int friendId) {
        log.info("Получен PUT-запрос: пользователь \"{}\" добавил друга \"{}\"", userId, friendId);
        return userService.addFriend(userId,friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public User removeFriend(@PathVariable("id") int userId,
                                  @PathVariable("friendId") int friendId) {
        log.info("Получен DELETE-запрос: пользователь \"{}\" удалил друга \"{}\"", userId, friendId);
        return userService.removeFriend(userId, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") int userId) {
        log.info("Получен GET-запрос: список друзей пользователя \"{}\"", userId);
        List<User> response = userService.getUsersFriends(userId);
        log.info("Друзья пользователя \"{}\": \"{}\"", userId, response);
        return response;
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable("id") int userId,
                                       @PathVariable("otherId") int otherId) {
        log.info("Получен GET-запрос: общие друзья пользователей \"{}\" и \"{}\"", userId, otherId);
        List<User> response = userService.getMutualFriends(userId, otherId);
        log.info("Общие друзья пользователей \"{}\" и \"{}\": \"{}\"", userId, otherId, response);
        return response;
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
