package ru.yandex.practicum.filmorate.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService  {
    private final InMemoryUserStorage inMemoryUserStorage;
    private long counterId;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage ) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        counterId = 0;
    }

    public long getCounterId() {
        return counterId;
    }
    private long setCounterId() {
        counterId++; // Инкремент счетчика id
        return counterId;
    }

    public User createUser(User user) throws EntityAlreadyExistException {
        if (getUserByEmail(user.getEmail()).isPresent()) {
            throw new EntityAlreadyExistException("Пользователь с указанным адресом электронной почты уже был добавлен раннее");
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        user.setId(setCounterId());
        inMemoryUserStorage.createUser(user);
        return user;
    }

    public User updateUser(User user) throws UnknownUserException, EntityAlreadyExistException {
        if (getUserById(user.getId()) == null) {
            throw new UnknownUserException("Нет пользователя с ID " + user.getId());
        }
        if (getUserByEmail(user.getEmail()).isPresent() && (getUserByEmail(user.getEmail()).get().getId() != user.getId())) {
            throw new EntityAlreadyExistException(String.format("Пользователю нельзя назначить такой email: %s, он уже используется.", user.getEmail()));
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        User updatedUser = inMemoryUserStorage.updateUser(user);
        return updatedUser;
    }

    public User getUserById(long id) throws UnknownUserException {
        if (inMemoryUserStorage.getUserById(id) != null) {
            return inMemoryUserStorage.getUserById(id);
        }
        else {
            throw new UnknownUserException("Не найден пользователь с ID: " + id);
        }
    }

    public Optional<User> getUserByEmail(String email) {
        return inMemoryUserStorage.getUserByEmail(email);
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public void addFriend(long userId, long friendId) throws EntityAlreadyExistException, UnknownUserException {
        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);
        if (user1 == null) {
            throw new UnknownUserException("Пользователь с ID " + userId + " не существует.");
        }
        if (user2 == null) {
            throw new UnknownUserException("Пользователь с ID " + friendId + " не существует.");
        }

        if (!user1.addFriend(friendId, false)) {
            throw new EntityAlreadyExistException(String.format("Пользователь с ID: %d уже является другом пользователю с ID: ",friendId, userId));
        }
        if (!user2.addFriend(userId, false)) {
            throw new EntityAlreadyExistException(String.format("Пользователь с ID: %d уже является другом пользователю с ID: ", userId, friendId));
        }
        inMemoryUserStorage.updateUser(user1);
        inMemoryUserStorage.updateUser(user2);
    }

    public void deleteFriend(long userId, long friendId) throws UserIsNotFriendException, UnknownUserException {
        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);
        if (user1 == null) {
            throw new UnknownUserException("Пользователь с ID " + userId + " не существует.");
        }
        if (user2 == null) {
            throw new UnknownUserException("Пользователь с ID " + friendId + " не существует.");
        }
        if (!user1.deleteFriend(friendId)) {
            throw new UserIsNotFriendException(String.format("У пользователя ID: %d  нет друга с ID: %d", friendId, userId));
        }
        if (!user2.deleteFriend(userId)) {
            throw new UserIsNotFriendException(String.format("У пользователя ID: %d  нет друга с ID: %d" , userId, friendId));
        }
        inMemoryUserStorage.updateUser(user1);
        inMemoryUserStorage.updateUser(user2);
    }

    public List<User> getAllFriends(long userId) throws UnknownUserException {
        if (getUserById(userId) == null) {
            throw new UnknownUserException("Пользователь с ID " + userId  + " не существует.");
        }
        return inMemoryUserStorage.getAllFriends(userId);
    }

    public List<User> getCommonFriends(long id, long otherId) throws UnknownUserException {
        if (getUserById(id) == null) {
            throw new UnknownUserException("Пользователь с ID " + id + " не существует.");
        }
        if (getUserById(otherId) == null) {
            throw new UnknownUserException("Пользователь с ID " + otherId + " не существует.");
        }
        return inMemoryUserStorage.getCommonFriends(id, otherId);
    }
}
