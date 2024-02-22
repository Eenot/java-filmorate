package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    User getUserById(int userId);

    List<User> getAllUsers();

    User addFriend(int userId, int friendId);

    User removeFriend(int userId, int friendId);

    List<Integer> getFriends(int id);
}
