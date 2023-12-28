package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.InMemoryUserManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private static User user;
    private static UserController controller;

    @BeforeEach
    public void beforeEach() {
        controller = new UserController(new InMemoryUserManager());
        user = User.builder()
                .email("test@yandex.ru")
                .login("test")
                .name("testName")
                .birthday(LocalDate.of(2001,4,4))
                .build();
    }

    @Test
    public void shouldAddUser() {
        controller.createUser(user);
        List<User> expectedUsers = List.of(user);
        List<User> savedUsers = controller.getUsers();
        assertEquals(expectedUsers,savedUsers);
        assertEquals(1, controller.getUsers().size());
    }

    @Test
    public void shouldNotAddUserWhenLoginIsEmpty() {
        user.setLogin("");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        String expectedMessage = "Логин не может быть пустым или содержать пробелы";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenLoginContainsSpaces() {
        user.setLogin("   ");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        String expectedMessage = "Логин не может быть пустым или содержать пробелы";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenEmailIsIncorrect() {
        user.setEmail("testemail.ru");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        String expectedMessage = "Электронная почта не может быть пустой и должна содержать @";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenEmailIsEmpty() {
        user.setEmail("");
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        String expectedMessage = "Электронная почта не может быть пустой и должна содержать @";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenBirthdayIsIncorrect() {
        user.setBirthday(LocalDate.of(2030,1,1));
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        String expectedMessage = "Дата рождения пользователя некорректна(поле пустое или дата позже текущего момента)";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenBirthdayIsEmpty() {
        user.setBirthday(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        String expectedMessage = "Дата рождения пользователя некорректна(поле пустое или дата позже текущего момента)";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldAddUserWhenNameIsEmpty() {
        user.setName("");
        controller.createUser(user);
        assertEquals(controller.getUsers().get(0).getName(), controller.getUsers().get(0).getLogin());
    }

    @Test
    public void shouldUpdateUser() {
        controller.createUser(user);

        User updatedUser = user;
        updatedUser.setName("newName");
        updatedUser.setEmail("newTest@yandex.ru");
        updatedUser.setLogin("newLogin");
        updatedUser.setBirthday(LocalDate.of(2000,8,8));
        controller.updateUser(updatedUser);

        assertEquals(updatedUser.toString(), controller.getUsers().get(0).toString());
    }

    @Test
    public void shouldNotUpdateUserWithIncorrectId() {
        controller.createUser(user);

        User updatedUser = user;
        updatedUser.setId(555);
        updatedUser.setName("newName");
        updatedUser.setEmail("newTest@yandex.ru");
        updatedUser.setLogin("newLogin");
        updatedUser.setBirthday(LocalDate.of(2000,8,8));

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.updateUser(updatedUser));
        String expectedMessage = "Пользователь с id " + updatedUser.getId() + " не существует";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

}
