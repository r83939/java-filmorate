package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UnknownFilmException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users;

    public InMemoryUserStorage() {
        users = new TreeMap<>();
    }

    public Map<Long, User> getUsers(){
        return users;
    }

    public User getUserById(long id) {
        return getUsers().get(id);
    }

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.remove(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User deleteUser(User user) { // Пока удаление пользователя не предусмотрено в ТЗ
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            userList.add(entry.getValue());
        }
        return userList;
    }
}
