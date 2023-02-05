package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "insert into users(email, login, name, birthday) " +
                "values (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update users set email=?, login=?, name=?, birthday=? where user_id=?";
        int numberOfRowsAffected = jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public User deleteUser(User deleteUser) {
        User user = getUserById(deleteUser.getId());
        if (user != null) {
            String sqlQuery = "delete from users where user_id = ?";
            int numberOfRowAffected = jdbcTemplate.update(sqlQuery, deleteUser.getId());
            if (numberOfRowAffected > 0) {
                return user;
            } else return null;
        } else return null;
    }

    public User getUserById (Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);
        if (userRows.next()) {
            log.info("Найден пользователь: {} {}", userRows.getString("user_id"), userRows.getString("name"));
            User user = new User(
                        userRows.getLong("user_id"),
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

    public Optional<User> getUserByEmail (String email) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where email email = ?", email);
        if (userRows.next()) {
            log.info("Найден пользователь: {} {}", userRows.getString("user_id"), userRows.getString("name"));
            Optional<User> user = Optional.of(new User(
                    userRows.getLong("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate(),
                    getUserFriends(userRows.getLong("user_id"))));
            return user;
        } else {
            log.info("Пользователь с emal {} не найден.", email);
            return null;
        }
    }

    public User deleteUserById (Long id) {
        User user = getUserById(id);
        if (user != null) {
            String sqlQuery = "delete from users where user_id = ?";
            int numberOfRowAffected = jdbcTemplate.update(sqlQuery, id);
            if (numberOfRowAffected > 0) {
                return user;
            } else return null;
        } else return null;
    }

    public Map<Long, Boolean> getUserFriends (Long userId) {
        Map<Long, Boolean> userFriends = new HashMap<>();
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet("select * from userfriends where user_id = ?", userId);
        if (friendsRows.next()) {
            userFriends.put(friendsRows.getLong("friend_id"), friendsRows.getBoolean("friendship_confirm"));
        }
        return userFriends;
    }

    @Override
    public List<User> getAllUsers () {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql,
                (rs, rowNum) ->
                        new User(
                                rs.getLong("user_id"),
                                rs.getString("email"),
                                rs.getString("login"),
                                rs.getString("name"),
                                rs.getDate("birthday").toLocalDate(),
                                getUserFriends(rs.getLong("user_id"))));
    }

    public boolean addFriend ( long userId, int friendId) {
        String sqlQuery = "insert into userfriends (user_id, friend_id, friendship_confirm) " +
                "values (?, ?, ?)";
        return jdbcTemplate.update(sqlQuery,
                    userId,
                    friendId,
                    "FALSE") > 0;
    }

    public int confirmFriendShip ( long userId, int friendId) {
        String sqlQuery = "UPDATE USERFRIENDS SET friendship_confirm=? WHERE USER_ID=? AND FRIEND_ID=? ";
        int numberOfRowsAffected = jdbcTemplate.update(sqlQuery,
                    "TRUE",
                    userId,
                    friendId);
        return numberOfRowsAffected;
    }


    public List<User> getAllFriends ( long userId){
        List<User> users = new ArrayList<>();
        if (getUserById(userId).getFriends() != null) {
            for (Map.Entry<Long, Boolean> entry : getUserById(userId).getFriends().entrySet()) {
                users.add(getUserById(entry.getKey()));
            }
        }
        return users;
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        List<User> commonFriends = new ArrayList<>();
        String sql = "SELECT * FROM USERS WHERE USER_ID IN (SELECT FRIEND_ID FROM USERFRIENDS WHERE  USER_ID=? AND  FRIEND_ID IN (SELECT FRIEND_ID FROM USERFRIENDS WHERE USER_ID=?))";
        return jdbcTemplate.query(sql,
                (rs, rowNum) ->
                        new User(
                                rs.getLong("user_id"),
                                rs.getString("email"),
                                rs.getString("login"),
                                rs.getString("name"),
                                rs.getDate("birthday").toLocalDate(),
                                getUserFriends(rs.getLong("user_id"))),
                new Object[] { id, otherId });
    }
}
