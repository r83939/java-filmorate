package ru.yandex.practicum.filmorate;




import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;

    private final FilmDbStorage filmStorage;

    @BeforeAll
    public static void beforeAll() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");// Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
        LocalDate date = LocalDate.parse("2005-nov-12", formatter);
        User user = new User(0, "ivan", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        User user2 = new User(0, "petr", "petr", "petr", LocalDate.parse("1995-02-10", formatter), new ArrayList<>());
        User user3 = new User(0, "sergey", "sergey", "sergey", LocalDate.parse("1980-03-07", formatter), new ArrayList<>());
        Film film = new Film(0, "Spy Game", "Film", LocalDate.parse("2001-11-21", formatter), 127, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(List.of(new Genre(4,"Триллер"))), new HashSet<>());
        Film film1 = new Film(0, "Avatar: The Way of Water", "Film", LocalDate.parse("2022-09-10", formatter), 192, 0, new Mpa(1,"G"), new ArrayList<Genre>(List.of(new Genre(6,"Боевик"))), new HashSet<>());
        Film film2 = new Film(0, "The Usual Suspects", "Film", LocalDate.parse("1995-01-25", formatter), 106, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(List.of(new Genre(4,"Триллер"))), new HashSet<>());
    }


    @Test
    void getUserById() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(Long.valueOf(1)));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void getUserByEmail() {
    }

    @Test
    void createUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void addFriend() {
    }

    @Test
    void deleteFriend() {
    }

    @Test
    void getAllFriends() {
    }

    @Test
    void getCommonFriends() {
    }

    @Test
    void isUserExist() {
    }


    @Test
    void createFilm() {
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void getAllFilms() {
    }

    @Test
    void getAllGenres() {
    }

    @Test
    void getAllMpa() {
    }

    @Test
    void getMpaById() {
    }

    @Test
    void getGenreById() {
    }

    @Test
    void getFilmById() {
    }

    @Test
    void testCreateFilm() {
    }

    @Test
    void updateFilm() {
    }

    @Test
    void deleteFilm() {
    }

    @Test
    void addLike() {
    }

    @Test
    void deleteLike() {
    }

    @Test
    void getTopFilms() {
    }

}
