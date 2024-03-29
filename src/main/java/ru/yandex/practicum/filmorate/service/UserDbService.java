package ru.yandex.practicum.filmorate.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.exception.UserIsNotFriendException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserDbService {
    private final UserDbStorage userDbStorage;

    @Autowired
    public UserDbService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public User getUserById(long id) throws UnknownUserException {
        if (userDbStorage.getUserById(id) != null) {
            return userDbStorage.getUserById(id);
        }
        else {
            throw new UnknownUserException("Не найден пользователь с ID: " + id);
        }
    }

    public Optional<User> getUserByEmail(String email) {
        return userDbStorage.getUserByEmail(email);
    }

    public User createUser(User user) throws EntityAlreadyExistException {
        if (getUserByEmail(user.getEmail()).isPresent()) {
            throw new EntityAlreadyExistException("Пользователь с указанным адресом электронной почты уже был добавлен раннее");
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        return userDbStorage.createUser(user);

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
        User updatedUser = userDbStorage.updateUser(user);
        return updatedUser;
    }

    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
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

        if (userDbStorage.isFriend(userId, friendId )) {
            throw new EntityAlreadyExistException(String.format("Пользователь с ID: %d уже является другом пользователю с ID: %d",friendId, userId));
        }
        userDbStorage.addFriend(userId, friendId);
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
        if (!userDbStorage.isFriend(userId, friendId )) {
            throw new UserIsNotFriendException(String.format("У пользователя ID: %d  нет друга с ID: %d", userId, friendId));
        }
        userDbStorage.deleteFriend(userId, friendId);
    }


    public List<User> getAllFriends(Long userId) throws UnknownUserException {
        if (getUserById(userId) == null) {
            throw new UnknownUserException("Пользователь с ID " + userId  + " не существует.");
        }
        return userDbStorage.getAllFriends(userId);
    }

    public List<User> getCommonFriends(Long id, Long otherId) throws UnknownUserException {
        if (getUserById(id) == null) {
            throw new UnknownUserException("Пользователь с ID " + id + " не существует.");
        }
        if (getUserById(otherId) == null) {
            throw new UnknownUserException("Пользователь с ID " + otherId + " не существует.");
        }
        return userDbStorage.getCommonFriends(id, otherId);
    }

    public boolean isUserExist(Long userId) {
        return userDbStorage.isUserExist(userId);
    }
}
