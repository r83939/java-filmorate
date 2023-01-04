package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
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
    public User addUser(@Valid @RequestBody User user) throws EntityAlreadyExistException {
        User createdUser = userService.createUser(user);
        log.trace("Добавлен пользователь: " + createdUser);
        return createdUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updateUser) throws UnknownUserException, EntityAlreadyExistException {
        User updatedUser = userService.updateUser(updateUser);
        log.trace("Изменен пользователь: " + updatedUser);
        return updateUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity addFriend(@PathVariable Long id,
                                    @PathVariable Long friendId) throws UnknownUserException, EntityAlreadyExistException {
        userService.addFriend(id, friendId);
        log.trace(String.format("Пользователь с ID: %d добавлен в друзья пользователя с ID: %d", friendId, id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity deleteFriend(@PathVariable Long id,
                          @PathVariable Long friendId) throws UnknownUserException, UserIsNotFriendException {
        userService.deleteFriend(id, friendId);
        log.trace(String.format("Пользователь с ID: %d удален из друзей пользователя с ID: %d", friendId, id));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable Long id) throws UnknownUserException {
        if (userService.getUserById(id) == null) {
            throw new UnknownUserException("Пользователь с ID " + id+ " не существует.");
        }
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id,
                                       @PathVariable Long otherId) throws UnknownUserException {
        return userService.getCommonFriends(id, otherId);
    }
}
