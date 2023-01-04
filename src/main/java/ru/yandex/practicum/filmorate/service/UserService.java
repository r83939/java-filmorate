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

    public User createUser(User user) {
        user.setId(setCounterId());
        inMemoryUserStorage.createUser(user);
        return user;
    }

    public User updateUser(User user) throws UnknownUserException {
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
        if (inMemoryUserStorage.getUserById(id) != null) {
            return inMemoryUserStorage.getUserById(id);
        }
        else {
            throw new UnknownUserException("Не найден пользователь с ID: " + id);
        }
    }

    public Optional<User> getUserByEmail(String email) throws UnknownUserException {
        return inMemoryUserStorage.getAllUsers().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public boolean addFriend(long userId, long friendId) throws EntityAlreadyExistException, UnknownUserException {
        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);
        if (!user1.addFriend(friendId)) {
            throw new EntityAlreadyExistException(String.format("Пользователь с ID: %d уже является другом пользователю с ID: ",friendId, userId));
        }
        if (!user2.addFriend(userId)) {
            throw new EntityAlreadyExistException(String.format("Пользователь с ID: %d уже является другом пользователю с ID: ", userId, friendId));
        }
        inMemoryUserStorage.updateUser(user1);
        inMemoryUserStorage.updateUser(user2);
        return true;
    }

    public boolean deleteFriend(long userId, long friendId) throws UserIsNotFriendException, UnknownUserException {
        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);
        if (!user1.deleteFriend(friendId)) {
            throw new UserIsNotFriendException(String.format("У пользователя ID: %d  нет друга с ID: %d", friendId, userId));
        }
        if (!user2.deleteFriend(userId)) {
            throw new UserIsNotFriendException(String.format("У пользователя ID: %d  нет друга с ID: %d" , userId, friendId));
        }
        inMemoryUserStorage.updateUser(user1);
        inMemoryUserStorage.updateUser(user2);
        return true;
    }

    public List<User> getAllFriends(long userId) throws UnknownUserException {
        List<User> users = new ArrayList<>();
        if (getUserById(userId).getFriends() != null) {
            for (long id : getUserById(userId).getFriends()) {
                users.add(getUserById(id));
            }
        }
        return users;
    }

    public List<User> getCommonFriends(long id, long otherId) throws UserHaveNotFriendsException, UnknownUserException {
        List<User> commonFriends = new ArrayList<>();
        List<User> userFriends = getAllFriends(id);
        List<User> otherUserFriends = getAllFriends(otherId);
        if (commonFriends != null && otherUserFriends != null) {
            commonFriends =  userFriends.stream()
                    .filter(otherUserFriends::contains)
                    .collect(Collectors.toList());
        }
        return commonFriends;
    }
}
