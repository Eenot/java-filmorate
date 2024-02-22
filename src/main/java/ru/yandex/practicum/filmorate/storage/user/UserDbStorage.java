package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.exception.SmthNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        int userId = addUserToDb(user);
        user.setId(userId);
        String sqlQuery = "INSERT INTO relationship (user_id, friend_id) VALUES (?,?)";
        if (!user.getFriends().isEmpty()) {
            for (int id : user.getFriends()) {
                jdbcTemplate.update(sqlQuery, userId, id);
            }
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUserId(user.getId());
        String sqlQuery = "UPDATE users SET user_id=?, email=?, login=?, name=?, birthday=?";

        jdbcTemplate.update(
                sqlQuery,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());

        String sqlQuery2 = "DELETE FROM relationship WHERE user_id=?";
        jdbcTemplate.update(sqlQuery2, user.getId());

        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
            String sqlQuery3 = "INSERT INTO relationship (user_id, friend_id) VALUES (?,?)";
            user.getFriends().forEach(id -> jdbcTemplate.update(sqlQuery3, user.getId(), id));
        }

        return user;
    }

    @Override
    public User getUserById(int userId) {
        checkUserId(userId);
        String sqlQuery = " SELECT * FROM users WHERE user_id=?";
        return jdbcTemplate.queryForObject(sqlQuery, this::makeUser, userId);
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    @Override
    public User addFriend(int userId, int friendId) {
        checkUserId(userId);
        checkUserId(friendId);

        if (userId == friendId) {
            log.info("Нельзя добавить самого себя в друзья!");
            throw new ValidationException("Нельзя добавить самого себя в друзья!");
        }

        String sqlQuery = "INSERT INTO relationship (user_id, friend_id) VALUES (?,?)";
        try {
            jdbcTemplate.update(sqlQuery, userId, friendId);
        } catch (DuplicateKeyException e) {
            log.info("Нельзя добавить в друзья одного и того же человека дважды!");
            throw new DuplicateDataException("Нельзя добавить в друзья одного и того же человека дважды!");
        }
        return getUserById(userId);
    }

    @Override
    public User removeFriend(int userId, int friendId) {
        checkUserId(userId);
        checkUserId(friendId);
        String sqlQuery = "DELETE FROM relationship WHERE user_id = ? AND friend_id = ?";
        if (jdbcTemplate.update(sqlQuery, userId, friendId) == 0) {
            log.info("Пользователя с id {} нет в друзьях у пользователя с id {}", userId, friendId);
            throw new SmthNotFoundException("Пользователя с id " + userId + " нет в друзьях у пользователя с id " + friendId);
        }
        return getUserById(userId);
    }

    @Override
    public List<Integer> getFriends(int id) {
        checkUserId(id);
        String sqlQuery = "SELECT friend_id FROM relationship WHERE user_id=?";
        return jdbcTemplate.query(sqlQuery, (rs1, rowNum1) -> rs1.getInt("friend_id"), id);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();

        String sqlQuery = "SELECT friend_id FROM relationship WHERE user_id=?";
        user.getFriends().addAll(jdbcTemplate.query(
                sqlQuery,
                (rs1, rowNum1) -> rs1.getInt("friend_id"),
                user.getId())
        );
        return user;
    }

    private void checkUserId(int userId) {
        if (!databaseContainsUser(userId)) {
            log.error("Пользователь с id {} не существует!", userId);
            throw new SmthNotFoundException("Пользователь с id " + userId + " не существует!");
        }
    }

    private boolean databaseContainsUser(int userId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id=?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::makeUser, userId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private int addUserToDb(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        return simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
    }
}
