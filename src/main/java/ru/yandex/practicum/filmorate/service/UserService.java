package ru.yandex.practicum.filmorate.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserHaveNotFriendsException;
import ru.yandex.practicum.filmorate.exception.UserIsNotFriendException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public User createUser(User user) {
        user.setId(setCounterId());
        inMemoryUserStorage.createUser(user);
        return user;
    }

    public User updateUser(User user) {
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        User updatedUser = inMemoryUserStorage.updateUser(user);
        return updatedUser;
    }

    public User getUserById(long id) {
        return inMemoryUserStorage.getUserById(id);
    }

    public User getUserByEmail(String email) {
        return inMemoryUserStorage.getUserByEmail(email);
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public Long addFriend(long userId, long friendId) throws EntityAlreadyExistException {
        User user = inMemoryUserStorage.getUserById(userId);
        if (!user.addFriend(friendId)) {
            throw new EntityAlreadyExistException("Пользователь с ID: " + friendId + " уже является другом пользователю с ID: " + userId);
        }
        inMemoryUserStorage.updateUser(user);
        return friendId;
    }

    public Long deleteFriend(long userId, long friendId) throws  UserIsNotFriendException {
        User user = inMemoryUserStorage.getUserById(userId);
        if (!user.deleteFriend(friendId)) {
            throw new UserIsNotFriendException("Пользователь с ID: " + friendId + " не является другом пользователю с ID: " + userId);
        }
        inMemoryUserStorage.updateUser(user);
        return friendId;
    }

    public List<User> getAllFriends(long userId) {
        List<User> users = new ArrayList<>();
        for (long id : inMemoryUserStorage.getUserById(userId).getFriends()) {
            users.add(inMemoryUserStorage.getUserById(id));
        }
        return users;
    }

    public List<User> getCommonFriends(long id, long otherId) throws UserHaveNotFriendsException {
        List<User> userFriends = getAllFriends(id);
        List<User> otherUserFriends = getAllFriends(otherId);
        if (userFriends.isEmpty()) {
            throw new UserHaveNotFriendsException("У пользователя с ID: " + id + " нет друзей");
        }
        if (otherUserFriends.isEmpty()) {
            throw new UserHaveNotFriendsException("У пользователя с ID: " + otherId + " нет друзей");
        }
        return userFriends.stream()
                .filter(getAllFriends(otherId)::contains)
                .collect(Collectors.toList());
    }

}
