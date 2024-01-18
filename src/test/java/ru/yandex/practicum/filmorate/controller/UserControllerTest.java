package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private static UserController userController;
    private static User user1, user2, user3, user4, user5, user6;

    @BeforeAll
    public static void beforeAll() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));

        user1 = User.builder().id(1).email("kirill@email.ru").login("kirillNew").name("Кирилл").birthday(LocalDate.of(1990, 1, 10)).build();
        user2 = User.builder().id(2).email("denis@email.ru").login("denisNew").name("Денис").birthday(LocalDate.of(1991, 2, 11)).build();
        user3 = User.builder().id(3).email("viktor@email.ru").login("viktorNew").name("Виктор").birthday(LocalDate.of(1992, 3, 12)).build();
        user4 = User.builder().id(4).email("stas@email.ru").login("stasNew").name("Стас").birthday(LocalDate.of(1993, 4, 13)).build();
        user5 = User.builder().id(5).email("igor@email.ru").login("igorNew").name("Игорь").birthday(LocalDate.of(1994, 5, 14)).build();

        userController.createUser(user1);
        userController.createUser(user2);
        userController.createUser(user3);
        userController.createUser(user4);
        userController.createUser(user5);
    }

    @BeforeEach
    public void beforeEach() {
        user6 = User.builder().email("Test@email.ru").login("Test").name("Тестер").birthday(LocalDate.of(1994, 5, 14)).build();
    }

    @Test
    public void shouldNotAddUserWhenLoginIsEmpty() {
        user6.setLogin("");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user6));
        String expectedMessage = "Логин не может быть пустым или содержать пробелы";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenLoginContainsSpaces() {
        user6.setLogin("   ");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user6));
        String expectedMessage = "Логин не может быть пустым или содержать пробелы";
        String actualMessage = exception.getMessage();
        System.out.println(userController.getUsers());
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenEmailIsIncorrect() {
        user6.setEmail("testemail.ru");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user6));
        String expectedMessage = "Электронная почта не может быть пустой и должна содержать @";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenEmailIsEmpty() {
        user6.setEmail("");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user6));
        String expectedMessage = "Электронная почта не может быть пустой и должна содержать @";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenBirthdayIsIncorrect() {
        user6.setBirthday(LocalDate.of(2030,1,1));
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user6));
        String expectedMessage = "Дата рождения пользователя некорректна(поле пустое или дата позже текущего момента)";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenBirthdayIsEmpty() {
        user6.setBirthday(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user6));
        String expectedMessage = "Дата рождения пользователя некорректна(поле пустое или дата позже текущего момента)";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldAddUserWhenNameIsEmpty() {
        user6.setName("");
        userController.createUser(user6);
        assertEquals(userController.getUserById(6).getName(), userController.getUserById(6).getLogin());
    }

    @Test
    public void shouldUpdateUser() {
        final User updatedUser = user1;
        updatedUser.setEmail("updatedEmail@email.ru");
        updatedUser.setLogin("updatedLogin");

        userController.updateUser(updatedUser);
        User expectedUser = updatedUser;
        User actualUser = userController.getUserById(user1.getId());
        assertEquals(expectedUser, actualUser);
        userController.updateUser(user1);
    }

    @Test
    public void shouldNotUpdateUserWithIncorrectId() {
        User updatedUser = user1;
        updatedUser.setId(555);
        updatedUser.setName("newName");
        updatedUser.setEmail("newTest@yandex.ru");
        updatedUser.setLogin("newLogin");
        updatedUser.setBirthday(LocalDate.of(2000,8,8));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userController.updateUser(updatedUser));
        String expectedMessage = "Пользователь с id " + updatedUser.getId() + " не существует!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldGetAllUsers() {
        List<User> expectedUserList = new ArrayList<>();
        expectedUserList.add(user1);
        expectedUserList.add(user2);
        expectedUserList.add(user3);
        expectedUserList.add(user4);
        expectedUserList.add(user5);

        List<User> actualUserList = userController.getUsers();
        assertEquals(expectedUserList, actualUserList);
    }

    @Test
    public void shouldGetUserWhenIdIsCorrect() {
        User expectedUser = user1;
        User actualUser = userController.getUserById(1);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void shouldNotGetUserWhenIdIsIncorrect() {
        int userId = 555;
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userController.getUserById(userId));
        String expectedMessage = "Пользователь с id " + userId + " не существует!";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenUserAlreadyExists() {
        User newUser = user1;
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(newUser));
        String expectedMessage = "Пользователь с id " + newUser.getId() + " уже существует!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void shouldAddFriendAndGetMutualFriends() {
        int userId = user1.getId();
        int friend1Id = user2.getId();
        int friend2Id = user3.getId();
        int friend3Id = user4.getId();
        int friend4Id = user5.getId();

        userController.addFriend(userId, friend1Id);
        userController.addFriend(userId, friend2Id);
        userController.addFriend(userId, friend3Id);
        userController.addFriend(userId, friend4Id);

        Set<Long> expectedFriendSet = new HashSet<>();
        expectedFriendSet.add((long) friend1Id);
        expectedFriendSet.add((long) friend2Id);
        expectedFriendSet.add((long) friend3Id);
        expectedFriendSet.add((long) friend4Id);

        Set<Long> actualFriendSet = userController.getUserById(userId).getFriends();

        List<User> expectedList = new ArrayList<>();
        expectedList.add(user2);
        expectedList.add(user3);
        expectedList.add(user4);
        expectedList.add(user5);

        List<User> actualList = userController.getUserFriends(userId);

        List<User> expectedMutualFriends = List.of(user1);
        List<User> actualMutualFriends = userController.getMutualFriends(user2.getId(), user3.getId());

        assertEquals(expectedFriendSet, actualFriendSet);
        assertEquals(expectedList, actualList);
        assertEquals(expectedMutualFriends, actualMutualFriends);

        userController.removeFriend(user1.getId(), user2.getId());
        expectedList.remove(user2);
        actualList = userController.getUserFriends(userId);
        assertEquals(expectedList, actualList);
    }

    @Test
    public void shouldNotAddFriendWhenFriendsIdIsIncorrect() {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userController.addFriend(user2.getId(), 555));
        String expectedMessage = "Пользователь с id \"555\" не существует!";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddFriendWhenUsersIdIsIncorrect() {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userController.addFriend(555, user2.getId()));
        String expectedMessage = "Пользователь с id \"555\" не существует!";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

}
