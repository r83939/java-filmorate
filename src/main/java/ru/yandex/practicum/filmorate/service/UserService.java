package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService  {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage ) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addFriend(int userId, int friendId) {
        if (inMemoryUserStorage.getUserById(friendId) == null) {
            return null; // нужно бросить исключение
        }
        User user = inMemoryUserStorage.getUserById(userId);
        if (inMemoryUserStorage.getUserById(userId) != null) {
            user.getFriends().add((long) friendId);
            return user;
        }
        return null;
    }

    public User deleteFriend(int userId, int friendId) {
        if (inMemoryUserStorage.getUserById(friendId) == null) {
            return null; // нужно бросить исключение
        }
        User user = inMemoryUserStorage.getUserById(userId);
        if (inMemoryUserStorage.getUserById(userId) != null) {
            user.getFriends().remove((long) friendId);
            return user;
        }
        return null;
    }

    public List<User> getAllFriends(int userId) {
        List<User> users = new ArrayList<>();
        for (long id : inMemoryUserStorage.getUserById(userId).getFriends()) {
            users.add(inMemoryUserStorage.getUserById(id));
        }
        return users;
    }




}
