package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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
        log.info("Количество пользователей: {}", userService.getAllUsers().size());
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Получен POST-запрос: {}", user);
        userService.validateUser(user);
        User response = userService.createUser(user);
        log.info("Добавлен пользователь: {}", user.toString());
        return response;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Получен PUT-запрос: {}", user);
        userService.validateUser(user);
        User response = userService.updateUser(user);
        log.info("Обновлён пользователь: {}", user.toString());
        return response;
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable("id") Integer userId) {
        log.info("Получен GET-запрос: пользователь с id \"{}\"", userId);
        User response = userService.getUserById(userId);
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
}
