package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws EntityAlreadyExistException {
        if (user.getEmail()==null || user.getEmail().isBlank()) {
            //throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
        }
        if (userService.getUserByEmail(user.getEmail()) != null) {
            throw new EntityAlreadyExistException("Пользователь с указанным адресом электронной почты уже был добавлен раннее");
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        User createdUser = userService.createUser(user);
        log.trace("Добавлен пользователь: {}", createdUser);
        return createdUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updateUser) throws UnknownUserException {
        if (updateUser.getEmail()==null || updateUser.getEmail().isBlank()) {
            //throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
        }
        User updatedUser = userService.updateUser(updateUser);
        log.trace("Изменен пользователь: {}", updatedUser);
        return updateUser;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public Long addFriend(@PathVariable Long id,
                          @PathVariable Long friendId) throws UnknownUserException, EntityAlreadyExistException {
        if (userService.getUserById(id) == null) {
            throw new UnknownUserException("Пользователь с ID " + id+ " не существует.");
        }
        if (userService.getUserById(friendId) == null) {
            throw new UnknownUserException("Пользователь с ID " + id+ " не существует.");
        }
        Long friendUserId =  userService.addFriend(id, friendId);
        log.trace("Пользователь с ID: {} добавлен в друзья пользователя с ID: {}", friendUserId, id);
        return friendUserId;
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public Long deleteFriend(@PathVariable Long id,
                          @PathVariable Long friendId) throws UnknownUserException, EntityAlreadyExistException {
        if (userService.getUserById(id) == null) {
            throw new UnknownUserException("Пользователь с ID " + id+ " не существует.");
        }
        if (userService.getUserById(friendId) == null) {
            throw new UnknownUserException("Пользователь с ID " + id+ " не существует.");
        }
        Long friendUserId =  userService. addFriend(id, friendId);
        log.trace("Пользователь с ID: {} добавлен в друзья пользователя с ID: {}", friendUserId, id);
        return friendUserId;
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getAllFriends(@PathVariable Long id) throws UnknownUserException {
        if (userService.getUserById(id) == null) {
            throw new UnknownUserException("Пользователь с ID " + id+ " не существует.");
        }
        return userService.getAllFriends(id);

    }




}
