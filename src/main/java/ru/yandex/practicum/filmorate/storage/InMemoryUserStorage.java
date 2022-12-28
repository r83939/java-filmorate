package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class InMemoryUserStorage implements UserStorage{

    private Map<String, User> users; // key = email

    public InMemoryUserStorage() {
        users = new TreeMap<>();
    }

    public Map<String, User> getUsers(){
        return users;
    }


    @Override
    public User createUser(User user) {
        users.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User getUserById(long id) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return users.get(email);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        for (Map.Entry<String, User> entry : users.entrySet()) {
            userList.add(entry.getValue());
        }
        return userList;
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
