package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;

@Service
public class UserDbService {
    private final UserDbStorage userDbStorage;

    @Autowired
    public UserDbService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public User getUserById(long id) {
        return userDbStorage.getUserById(id);
    }

    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public User getUserByEmail(String email) {
        return null;
    }

    public User updateUser(User updateUser) {
        return null;
    }

    public Long addFriend(Long id, Long friendId) {
        return null;
    }

    public List<User> getAllFriends(Long id) {
        return null;
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return null;
    }

    public User createUser(User user) {
        return null;
    }
}
