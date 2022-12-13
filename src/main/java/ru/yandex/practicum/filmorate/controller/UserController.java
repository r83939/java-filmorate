package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int counterId;
    private Map<Integer, User> users;

    private int setCounterId() {
        counterId++; // Инкремент счетчика id
        return counterId;
    }

    public UserController() {
        counterId = 0;
        users = new TreeMap<>();
    }

    @GetMapping
    public List<User> getUsers() {
        List<User> usersList = new ArrayList<>();
        for (Map.Entry entry : users.entrySet()) {
            usersList.add((User) entry.getValue());
        }
        return usersList;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws EntityAlreadyExistException {
        for (Map.Entry entry : users.entrySet()) {
            if (((User)entry.getValue()).getEmail().equals(user.getEmail())) {
                throw new EntityAlreadyExistException("Пользователь с указанным адресом электронной почты уже был добавлен раннее");
            }
        }
        int id = setCounterId();
        user.setId(id);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(id, user);
        log.trace("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updateUser) throws UnknownUserException {
        if (users.containsKey(updateUser.getId())) {
            users.put(updateUser.getId(), updateUser);
            log.trace("Изменен пользователь: {}", updateUser);
            return updateUser;
        }
        else {
            throw new UnknownUserException("Пользователь с указанным id не был найден");
        }
    }
}
