package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.SmthNotFoundException;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    private User user;
    private User user2;

    @BeforeEach
    public void beforeEach() {
        userStorage = new UserDbStorage(jdbcTemplate);
        user = User.builder()
                .email("stas@email.ru")
                .login("stasNew")
                .name("Стас")
                .birthday(LocalDate.of(1993, 4, 13))
                .build();
        user2 = User.builder()
                .email("igor@email.ru")
                .login("igorNew")
                .name("Игорь")
                .birthday(LocalDate.of(1994, 5, 14))
                .build();
    }

    @Test
    public void shouldCreateUser() {
        userStorage.createUser(user);

        User savedUser = userStorage.getUserById(user.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void shouldThrowExceptionWhenUserIdIsIncorrect() {
        final SmthNotFoundException exception = assertThrows(
                SmthNotFoundException.class,
                () -> userStorage.getUserById(111)
        );

        assertEquals(exception.getMessage(), "Пользователь с id 111 не существует!");
    }

    @Test
    public void shouldUpdateUser() {
        userStorage.createUser(user);
        user2.setId(1);

        userStorage.updateUser(user2);

        User savedUser = userStorage.getUserById(user2.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user2);
    }

    @Test
    public void shouldNotUpdateUserWhenIdIsIncorrect() {
          userStorage.createUser(user);
          user2.setId(111);

          final SmthNotFoundException exception = assertThrows(
                  SmthNotFoundException.class,
                  () -> userStorage.updateUser(user2)
          );

          assertEquals(exception.getMessage(), "Пользователь с id 111 не существует!");
    }

    @Test
    public void shouldGetAllUsers() {
        userStorage.createUser(user);
        userStorage.createUser(user2);

        List<User> savedUsers = userStorage.getAllUsers();

        assertThat(savedUsers)
                .isNotNull()
                .isEqualTo(List.of(user,user2));
    }

    @Test
    public void shouldGetUserById() {
        userStorage.createUser(user);

        User savedUser = userStorage.getUserById(user.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void shouldAddFriend() {
        userStorage.createUser(user);
        userStorage.createUser(user2);

        userStorage.addFriend(user.getId(), user2.getId());
        List<Integer> savedFriends = userStorage.getFriends(user.getId());

        assertThat(savedFriends)
                .isNotNull()
                .isEqualTo(List.of(2));
    }

    @Test
    public void shouldThrowExceptionWhenTryingToAddFriendTwice() {
        userStorage.createUser(user);
        userStorage.createUser(user2);

        userStorage.addFriend(user.getId(), user2.getId());

        final DuplicateDataException exception = assertThrows(
                DuplicateDataException.class,
                () -> userStorage.addFriend(user.getId(), user2.getId())
        );

        assertEquals(exception.getMessage(), "Нельзя добавить в друзья одного и того же человека дважды!");

    }

    @Test
    public void shouldThrowExceptionWhenTryingToAddSelfFriend() {
        userStorage.createUser(user);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userStorage.addFriend(user.getId(), user.getId())
        );

        assertEquals(exception.getMessage(), "Нельзя добавить самого себя в друзья!");
    }

    @Test
    public void shouldRemoveFriend() {
        userStorage.createUser(user);
        userStorage.createUser(user2);

        userStorage.addFriend(user.getId(), user2.getId());
        userStorage.removeFriend(user.getId(), user2.getId());
        List<Integer> savedFriends = userStorage.getFriends(user.getId());

        assertNotNull(savedFriends);
        assertTrue(savedFriends.isEmpty());
    }

    @Test
    public void shouldThrowExceptionWhenUserIsNotInFriends() {
        userStorage.createUser(user);
        userStorage.createUser(user2);

        final SmthNotFoundException exception = assertThrows(
                SmthNotFoundException.class,
                () -> userStorage.removeFriend(user.getId(), user2.getId())
        );

        assertEquals(exception.getMessage(), "Пользователя с id 1 нет в друзьях у пользователя с id 2");
    }

    @Test
    public void shouldGetUserFriends() {
        User user3 = User.builder()
                .email("kirill@email.ru")
                .login("kirillNew")
                .name("Кирилл")
                .birthday(LocalDate.of(2002, 5, 14))
                .build();

        userStorage.createUser(user);
        userStorage.createUser(user2);
        userStorage.createUser(user3);


        userStorage.addFriend(user.getId(), user2.getId());
        userStorage.addFriend(user.getId(), user3.getId());

        List<Integer> savedFriends = userStorage.getFriends(user.getId());

        assertThat(savedFriends)
                .isNotNull()
                .isEqualTo(List.of(2,3));
    }

}
