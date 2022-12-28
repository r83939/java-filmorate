package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class InMemoryUserStorage implements UserStorage{
    private int counterId;
    private Map<String, User> users; // key = email

    public InMemoryUserStorage() {
        counterId = 0;
        users = new TreeMap<>();
    }

    private int setCounterId() {
        counterId++; // Инкремент счетчика id
        return counterId;
    }

    public Map<String, User> getUsers(){
        return users;
    }


    @Override
    public User createUser(User user) {
        if (user.getEmail()==null || user.getEmail().isBlank()) {

            //throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
        }
        if (users.containsKey(user.getEmail())) {
          //  throw new UserAlreadyExistException("Пользователь с электронной почтой " +
                  //  user.getEmail() + " уже зарегистрирован.");
        }
        users.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User getUserById(long id) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User addFriend(long userId, int friendId) {
        return null;
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        return null;
    }

    @Override
    public List<User> getAllFriends(int userId) {
        return null;
    }
}
