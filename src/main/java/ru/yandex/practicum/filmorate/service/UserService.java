package ru.yandex.practicum.filmorate.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.swing.plaf.basic.BasicButtonUI;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public User createUser(User user) {
        user.setId(setCounterId());
        inMemoryUserStorage.createUser(user);
        return user;
    }

    public User updateUser(User user) throws UnknownUserException {
           // User u = getUserById(user.getId());
            if (getUserById(user.getId()) == null){
            throw new UnknownUserException("Нет пользователя с ID " + user.getId());
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }

        User updatedUser = inMemoryUserStorage.updateUser(user);
        return updatedUser;
    }

    public User getUserById(long id) throws UnknownUserException {
        User user = inMemoryUserStorage.getAllUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new UnknownUserException(String.format("Фильм с ID: {} не найден", id)));
        return user;
    }

    public User getUserByEmail(String email) {
        return inMemoryUserStorage.getUserByEmail(email);
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public Long addFriend(long userId, long friendId) throws EntityAlreadyExistException, UnknownUserException {
        User user = getUserById(userId);
        if (!user.addFriend(friendId, false)) {
            throw new EntityAlreadyExistException("Пользователь с ID: " + friendId + " уже является другом пользователю с ID: " + userId);
        }
        inMemoryUserStorage.updateUser(user);
        return friendId;
    }

    public Long friendConfirmation(long userId, long friendId) throws UnknownUserException, EntityAlreadyExistException {
        User user = getUserById(userId);
        if (!user.addFriend(friendId, false)) {
            throw new EntityAlreadyExistException("Пользователь с ID: " + friendId + " уже является другом пользователю с ID: " + userId);
        }
        inMemoryUserStorage.updateUser(user);
        return friendId;
    }

    public Long deleteFriend(long userId, long friendId) throws UserIsNotFriendException, UnknownUserException {
        User user = getUserById(userId);
        if (!user.deleteFriend(friendId)) {
            throw new UserIsNotFriendException("Пользователь с ID: " + friendId + " не является другом пользователю с ID: " + userId);
        }
        inMemoryUserStorage.updateUser(user);
        return friendId;
    }

    public List<User> getAllFriends(long userId) throws UnknownUserException {
        List<User> users = new ArrayList<>();
        for (Map.Entry<Long, Boolean> entry : getUserById(userId).getFriends().entrySet()) {
            users.add(getUserById(entry.getKey()));
        }
        return users;
    }

    public List<User> getCommonFriends(long id, long otherId) throws UserHaveNotFriendsException, UnknownUserException {
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
