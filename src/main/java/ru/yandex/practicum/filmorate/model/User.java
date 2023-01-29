package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validator.Login;

import java.time.LocalDate;
import java.util.*;
import javax.validation.constraints.*;

@Getter
@Setter
public class User {
    private long id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Login
    private String login;
    private String name;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Incorrect date birthday")
    private LocalDate birthday;

    private Map<Long,Boolean> friends; // Запросы дружбы отправленные пользователем, и подтвержденные они или нет

    public User() {
        this.id = 0;
        this.friends = new HashMap<>();
    }

    public Map<Long, Boolean> getFriends() {
        return friends;
    }

    public boolean addFriend(Long id) {
        return  friends.put(id, true);
    }

    public boolean deleteFriend(Long id) {
        return  friends.remove(id);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id == user.id && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
