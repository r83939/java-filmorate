package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserAlreadyExistException;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int counterId;
    private Map<String, User> users;

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
        if (users.containsKey(user.getEmail())) {
            throw new EntityAlreadyExistException("Пользователь с указанным адресом электронной почты уже был добавлен раннее");
        }
        int id = setCounterId();
        user.setId(id);
        users.put(user.getEmail(), user);
        log.trace("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user)  {
        if (users.containsKey(user.getEmail())) {
            users.put(user.getEmail(), user);
            log.trace("Изменен пользователь: {}", user);
        }
        else {
            int id = setCounterId();
            user.setId(id);
            users.put(user.getEmail(), user);
            log.trace("Добавлен пользователь: {}", user);
        }
        return user;
    }
}
