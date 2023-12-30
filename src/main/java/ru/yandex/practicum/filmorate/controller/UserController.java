package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserManager;
import javax.validation.constraints.NotNull;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserManager manager;

    public UserController(UserManager manager) {
        this.manager = manager;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Количество пользователей: {}", manager.getUsers().size());
        return manager.getUsers();
    }

    @PostMapping
    public User createUser(@NotNull @RequestBody User user) {
        log.info("Получен POST-запрос: {}", user);
        User respone = manager.createUser(user);
        log.info("Добавлен пользователь: {}", user.toString());
        return respone;
    }

    @PutMapping
    public User updateUser(@NotNull @RequestBody User user) {
        log.info("Получен PUT-запрос: {}", user);
        User response = manager.updateUser(user);
        log.info("Обновлён пользователь: {}", user.toString());
        return response;
    }
}
