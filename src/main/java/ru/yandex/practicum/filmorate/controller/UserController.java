package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
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

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) throws InvalidParameterException, UnknownUserException {
        if (id <= 0) {
            throw new InvalidParameterException("id должен быть больше 0");
        }
        return userService.getUserById(id);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws EntityAlreadyExistException, UnknownUserException {
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
                          @PathVariable Long friendId) throws UnknownUserException, EntityAlreadyExistException, UserIsNotFriendException {
        if (userService.getUserById(id) == null) {
            throw new UnknownUserException("Пользователь с ID " + id+ " не существует.");
        }
        if (userService.getUserById(friendId) == null) {
            throw new UnknownUserException("Пользователь с ID " + id+ " не существует.");
        }
        Long friendUserId =  userService.deleteFriend(id, friendId);
        log.trace("Пользователь с ID: {} удален из друзей пользователя с ID: {}", friendUserId, id);
        return friendUserId;
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getAllFriends(@PathVariable Long id) throws UnknownUserException {
        if (userService.getUserById(id) == null) {
            throw new UnknownUserException("Пользователь с ID " + id+ " не существует.");
        }
        return userService.getAllFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id,
                                       @PathVariable Long otherId) throws UnknownUserException, UserHaveNotFriendsException {
        if (userService.getUserById(id) == null) {
            throw new UnknownUserException("Пользователь с ID " + id + " не существует.");
        }
        if (userService.getUserById(otherId) == null) {
            throw new UnknownUserException("Пользователь с ID " + otherId + " не существует.");
        }
        return userService.getCommonFriends(id, otherId);
    }
}
