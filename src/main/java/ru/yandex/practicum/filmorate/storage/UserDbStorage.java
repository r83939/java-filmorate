package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class UserDbStorage implements UserStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }

    @Override
    public User getUserById(Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        if(userRows.next()) {
            log.info("Найден пользователь: {} {}", userRows.getString("id"), userRows.getString("nickname"));
            // вы заполните данные пользователя в следующем уроке
            User user = new User(
                    userRows.getLong("id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate(),
                    getUserFriends(id));
            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return null;
        }
    }

    public Map<Long, Boolean> getUserFriends(Long userId) {
        Map<Long, Boolean> userFriends = new HashMap<>();
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet("select * from userfriends where user_id = ?", userId);
        if(friendsRows.next()) {
            userFriends.put(friendsRows.getLong("friend_id"), friendsRows.getBoolean("friendship_confirm"));
        }
        return userFriends;

    }




    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User addFriend(long userId, int friendId) {
        return null;
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        return null;
    }

    @Override
    public List<User> getAllFriends(int userId) {
        return null;
    }
}
