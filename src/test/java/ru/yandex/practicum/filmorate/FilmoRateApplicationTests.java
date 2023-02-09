package ru.yandex.practicum.filmorate;




import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmoRateApplicationTests {
    @Autowired
    private final UserDbStorage userStorage;
    @Autowired
    private final FilmDbStorage filmStorage;

    @Test
    void getUserById() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user1 = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        userStorage.createUser(user1);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(Long.valueOf(1)));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void getUserByEmail() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user1 = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        userStorage.createUser(user1);

        Optional<User> userOptional = userStorage.getUserByEmail("ivan@mail.ru");
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "ivan@mail.ru")
                );
    }

    @Test
    void createUser() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user1 = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        userStorage.createUser(user1);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(Long.valueOf(1)));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("email", "ivan@mail.ru")
                                .hasFieldOrPropertyWithValue("login","ivan" )
                                .hasFieldOrPropertyWithValue("birthday",LocalDate.parse("2000-01-20", formatter))
                                .hasFieldOrPropertyWithValue("friends", new ArrayList<>())
                );
    }

    @Test
    void updateUser() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user1 = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        User updateUser = new User(1, "ivan@mail.ru", "ivan1", "ivan ivanov", LocalDate.parse("2001-01-20", formatter), new ArrayList<>());
        userStorage.createUser(updateUser);
        User updatedUser = userStorage.updateUser(updateUser);
        assertThat(updatedUser.getId()).isEqualTo(1L);
        assertThat(updatedUser.getEmail()).isEqualTo("ivan@mail.ru");
        assertThat(updatedUser.getLogin()).isEqualTo("ivan1");
        assertThat(updatedUser.getName()).isEqualTo("ivan ivanov");
        assertThat(updatedUser.getBirthday()).isEqualTo(LocalDate.parse("2001-01-20"));
    }

    @Test
    void getAllUsers() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user1 = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        User user2 = new User(0, "petr@mail.ru", "petr", "petr", LocalDate.parse("1995-02-10", formatter), new ArrayList<>());
        userStorage.createUser(user1);
        userStorage.createUser(user2);
        List<User> users = userStorage.getAllUsers();
        Assertions.assertThat(users).hasSize(2);
    }

    @Test
    void deleteUser() {
        Assertions.assertThat(userStorage.getAllUsers()).hasSize(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user1 = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        User createdUser = userStorage.createUser(user1);
        Assertions.assertThat(userStorage.getAllUsers()).hasSize(1);
        User deletedUser = userStorage.deleteUser(createdUser);
        Assertions.assertThat(deletedUser.getId()).isEqualTo(createdUser.getId());
        Assertions.assertThat(userStorage.getAllUsers()).hasSize(0);
    }


    @Test
    void addFriend() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        User friend = new User(0, "petr@mail.ru", "petr", "petr", LocalDate.parse("1995-02-10", formatter), new ArrayList<>());
        User createdUser = userStorage.createUser(user);
        User createdFriend = userStorage.createUser(friend);
        Assertions.assertThat(createdUser.getFriends()).hasSize(0);
        userStorage.addFriend(createdUser.getId(), createdFriend.getId());

        Optional<User> userWithFriend = Optional.ofNullable(userStorage.getUserById(createdUser.getId()));
        Assertions.assertThat(userWithFriend.get().getFriends()).hasSize(1);
        Assertions.assertThat(userWithFriend.get().getFriends().get(0)).isEqualTo(createdFriend.getId());
    }

    @Test
    void deleteFriend() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        User friend = new User(0, "petr@mail.ru", "petr", "petr", LocalDate.parse("1995-02-10", formatter), new ArrayList<>());
        User createdUser = userStorage.createUser(user);
        User createdFriend = userStorage.createUser(friend);
        Assertions.assertThat(createdUser.getFriends()).hasSize(0);
        userStorage.addFriend(createdUser.getId(), createdFriend.getId());

        Optional<User> userWithFriend = Optional.ofNullable(userStorage.getUserById(createdUser.getId()));
        Assertions.assertThat(userWithFriend.get().getFriends()).hasSize(1);
        Assertions.assertThat(userWithFriend.get().getFriends().get(0)).isEqualTo(createdFriend.getId());

        userStorage.deleteFriend(userWithFriend.get().getId(), createdFriend.getId());
        Assertions.assertThat(userStorage.getUserById(createdUser.getId()).getFriends()).hasSize(0);

    }

    @Test
    void getAllFriends() {
        Assertions.assertThat(userStorage.getAllUsers()).hasSize(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user1 = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        User user2 = new User(0, "petr@mail.ru", "petr", "petr", LocalDate.parse("1995-02-10", formatter), new ArrayList<>());
        userStorage.createUser(user1);
        Assertions.assertThat(userStorage.getAllUsers()).hasSize(1);
        userStorage.createUser(user2);
        Assertions.assertThat(userStorage.getAllUsers()).hasSize(2);
    }

    @Test
    void getCommonFriends() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user1 = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        User user2 = new User(0, "petr@mail.ru", "petr", "petr", LocalDate.parse("1995-02-10", formatter), new ArrayList<>());
        User user3 = new User(0, "sergey@mail.ru", "sergey", "sergey", LocalDate.parse("1990-03-12", formatter), new ArrayList<>());
        User createdUser1 = userStorage.createUser(user1);
        User createdUser2 = userStorage.createUser(user2);
        User createdUser3 = userStorage.createUser(user3); // общий друг

        userStorage.addFriend(createdUser1.getId(), createdUser3.getId());
        userStorage.addFriend(createdUser2.getId(), createdUser3.getId());

        List<User> commonFriends = userStorage.getCommonFriends(createdUser1.getId(),createdUser2.getId());
        Assertions.assertThat(commonFriends).hasSize(1);
        Assertions.assertThat(commonFriends.get(0).getId()).isEqualTo(createdUser3.getId());

    }

    @Test
    void isUserExist() {
        Assertions.assertThat(userStorage.getAllUsers()).hasSize(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user1 = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        User createdUser1 = userStorage.createUser(user1);
        Assertions.assertThat(userStorage.getAllUsers()).hasSize(1);
        Assertions.assertThat(userStorage.isUserExist(createdUser1.getId())).isTrue();
    }



    @Test
    void createFilm() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Film film = new Film(0, "Spy Game", "Film", LocalDate.parse("2001-11-21", formatter), 127, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(List.of(new Genre(4,"Триллер"))), new HashSet<>());
        Film createdFilm = filmStorage.createFilm(film);
        assertThat(createdFilm)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Spy Game")
                .hasFieldOrPropertyWithValue("description","Film" )
                .hasFieldOrPropertyWithValue("releaseDate",LocalDate.parse("2001-11-21", formatter))
                .hasFieldOrPropertyWithValue("duration", 127);
    }

    @Test
    void getAllFilms() {
        Assertions.assertThat(filmStorage.getAllFilms()).hasSize(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Film film1 = new Film(0, "Spy Game", "Film", LocalDate.parse("2001-11-21", formatter), 127, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(List.of(new Genre(4,"Триллер"))), new HashSet<>());
        Film film2 = new Film(0, "Avatar: The Way of Water", "Film", LocalDate.parse("2022-09-10", formatter), 192, 0, new Mpa(1,"G"), new ArrayList<Genre>(List.of(new Genre(6,"Боевик"))), new HashSet<>());
        Film film3 = new Film(0, "The Usual Suspects", "Film", LocalDate.parse("1995-01-25", formatter), 106, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(List.of(new Genre(4,"Триллер"))), new HashSet<>());
        Film createdFilm1 = filmStorage.createFilm(film1);
        Film createdFilm2 = filmStorage.createFilm(film2);
        Film createdFilm3 = filmStorage.createFilm(film3);
        Assertions.assertThat(filmStorage.getAllFilms()).hasSize(3);
    }

    @Test
    void getAllGenres() {
        List<Genre> genres = filmStorage.getAllGenres();
        Assertions.assertThat(genres).hasSize(6);
    }

    @Test
    void getAllMpa() {
        List<Mpa> mpaList = filmStorage.getAllMpa();
        Assertions.assertThat(mpaList).hasSize(5);
    }

    @Test
    void getMpaById() {
        Mpa mpa = filmStorage.getMpaById(1);
        Assertions.assertThat(mpa.getName()).isEqualTo("G");
    }

    @Test
    void getGenreById() {
        Genre genre = filmStorage.getGenreById(1);
        Assertions.assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    void getFilmById() {
        Assertions.assertThat(filmStorage.getAllFilms()).hasSize(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Film film1 = new Film(0, "Spy Game", "Film", LocalDate.parse("2001-11-21", formatter), 127, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(List.of(new Genre(4,"Триллер"))), new HashSet<>());
        Film film2 = new Film(0, "Avatar: The Way of Water", "Film", LocalDate.parse("2022-09-10", formatter), 192, 0, new Mpa(1,"G"), new ArrayList<Genre>(List.of(new Genre(6,"Боевик"))), new HashSet<>());
        Film createdFilm1 = filmStorage.createFilm(film1);
        Film createdFilm2 = filmStorage.createFilm(film2);
        Film gotFilm = filmStorage.getFilmById(2);
        Assertions.assertThat(createdFilm2.getId()).isEqualTo(gotFilm.getId());
        Assertions.assertThat(createdFilm2).isEqualTo(gotFilm);
    }

    @Test
    void updateFilm() {
        Assertions.assertThat(filmStorage.getAllFilms()).hasSize(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Film film1 = new Film(0, "Spy Game", "Film", LocalDate.parse("2001-11-21", formatter), 127, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(List.of(new Genre(4,"Триллер"))), new HashSet<>());
        Film createdFilm1 = filmStorage.createFilm(film1);


    }

    @Test
    void deleteFilm() {
        Assertions.assertThat(filmStorage.getAllFilms()).hasSize(0);
        Assertions.assertThat(filmStorage.getAllFilms()).hasSize(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Film film1 = new Film(0, "Spy Game", "Film", LocalDate.parse("2001-11-21", formatter), 127, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(), new HashSet<>());
        Film createdFilm1 = filmStorage.createFilm(film1);
        Assertions.assertThat(filmStorage.getAllFilms()).hasSize(1);
        Optional<Film> deletedFilm = filmStorage.deleteFilm(1);
        Assertions.assertThat(filmStorage.getAllFilms()).hasSize(0);
        Assertions.assertThat(deletedFilm.get().getId()).isEqualTo(createdFilm1.getId());
    }

    @Test
    void addLike() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        User createdUser = userStorage.createUser(user);

        Film film = new Film(0, "Spy Game", "Film", LocalDate.parse("2001-11-21", formatter), 127, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(List.of(new Genre(4,"Триллер"))), new HashSet<>());
        Film createdFilm = filmStorage.createFilm(film);
        Assertions.assertThat(createdFilm.getRate()).isEqualTo(0L);

        filmStorage.addLike(createdFilm.getId(), createdUser.getId());
        Film filmWithLike = filmStorage.getFilmById(createdFilm.getId());
        Assertions.assertThat(filmWithLike.getRate()).isEqualTo(1L);
    }

    @Test
    void deleteLike() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        User createdUser = userStorage.createUser(user);

        Film film = new Film(0, "Spy Game", "Film", LocalDate.parse("2001-11-21", formatter), 127, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(List.of(new Genre(4,"Триллер"))), new HashSet<>());
        Film createdFilm = filmStorage.createFilm(film);
        Assertions.assertThat(createdFilm.getRate()).isEqualTo(0L);

        filmStorage.addLike(createdFilm.getId(), createdUser.getId());
        Film filmWithLike = filmStorage.getFilmById(createdFilm.getId());
        Assertions.assertThat(filmWithLike.getRate()).isEqualTo(1L);

        filmStorage.deleteLike(createdFilm.getId(), createdUser.getId());
        Film filmWithOutLike = filmStorage.getFilmById(createdFilm.getId());
        Assertions.assertThat(filmWithOutLike.getRate()).isEqualTo(0L);
    }

    @Test
    void getTopFilms() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User user1 = new User(0, "ivan@mail.ru", "ivan", "ivan", LocalDate.parse("2000-01-20", formatter), new ArrayList<>());
        User user2 = new User(0, "petr@mail.ru", "petr", "petr", LocalDate.parse("1995-02-10", formatter), new ArrayList<>());
        User user3 = new User(0, "sergey@mail.ru", "sergey", "sergey", LocalDate.parse("1990-03-12", formatter), new ArrayList<>());
        User user4 = new User(0, "elena@mail.ru", "elena", "elena", LocalDate.parse("1996-01-20", formatter), new ArrayList<>());
        User user5 = new User(0, "olga@mail.ru", "olga", "olga", LocalDate.parse("1998-02-10", formatter), new ArrayList<>());
        User user6 = new User(0, "natalya@mail.ru", "natalya", "natalya", LocalDate.parse("1990-04-11", formatter), new ArrayList<>());
        User createdUser1 = userStorage.createUser(user1);
        User createdUser2 = userStorage.createUser(user2);
        User createdUser3 = userStorage.createUser(user3);
        User createdUser4 = userStorage.createUser(user4);
        User createdUser5 = userStorage.createUser(user5);
        User createdUser6 = userStorage.createUser(user6);

        Film film1 = new Film(0, "Spy Game", "Film", LocalDate.parse("2001-11-21", formatter), 127, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(List.of(new Genre(4,"Триллер"))), new HashSet<>());
        Film film2 = new Film(0, "Avatar: The Way of Water", "Film", LocalDate.parse("2022-09-10", formatter), 192, 0, new Mpa(1,"G"), new ArrayList<Genre>(List.of(new Genre(6,"Боевик"))), new HashSet<>());
        Film film3 = new Film(0, "The Usual Suspects", "Film", LocalDate.parse("1995-01-25", formatter), 106, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(List.of(new Genre(4,"Триллер"))), new HashSet<>());
        Film film4 = new Film(0, "Jack Ryan", "Action", LocalDate.parse("2018-01-25", formatter), 60, 0, new Mpa(5,"NC-17"), new ArrayList<Genre>(List.of(new Genre(4,"Триллер"))), new HashSet<>());
        Film createdFilm1 = filmStorage.createFilm(film1);
        Film createdFilm2 = filmStorage.createFilm(film2);
        Film createdFilm3 = filmStorage.createFilm(film3);
        Film createdFilm4 = filmStorage.createFilm(film4);

        filmStorage.addLike(createdFilm1.getId(), createdUser1.getId());
        filmStorage.addLike(createdFilm1.getId(), createdUser2.getId());
        filmStorage.addLike(createdFilm1.getId(), createdUser3.getId());
        filmStorage.addLike(createdFilm1.getId(), createdUser4.getId());
        filmStorage.addLike(createdFilm1.getId(), createdUser5.getId());
        filmStorage.addLike(createdFilm1.getId(), createdUser6.getId());

        filmStorage.addLike(createdFilm2.getId(), createdUser1.getId());
        filmStorage.addLike(createdFilm2.getId(), createdUser2.getId());
        filmStorage.addLike(createdFilm2.getId(), createdUser3.getId());
        filmStorage.addLike(createdFilm2.getId(), createdUser4.getId());
        filmStorage.addLike(createdFilm2.getId(), createdUser5.getId());

        filmStorage.addLike(createdFilm3.getId(), createdUser1.getId());
        filmStorage.addLike(createdFilm3.getId(), createdUser2.getId());
        filmStorage.addLike(createdFilm3.getId(), createdUser3.getId());
        filmStorage.addLike(createdFilm3.getId(), createdUser4.getId());

        filmStorage.addLike(createdFilm4.getId(), createdUser1.getId());
        filmStorage.addLike(createdFilm4.getId(), createdUser2.getId());
        filmStorage.addLike(createdFilm4.getId(), createdUser3.getId());

        List<Film> topFilms = filmStorage.getTopFilms(1); // топ из одного фильма
        Assertions.assertThat(topFilms).hasSize(1);
        Assertions.assertThat(topFilms.get(0).getId()).isEqualTo(1); // Наиболее популярный фильм с id 1

        List<Film> topFilms4 = filmStorage.getTopFilms(4); // топ из 4 фильмов
        Assertions.assertThat(topFilms4).hasSize(4);
        Assertions.assertThat(topFilms4.get(0).getId()).isEqualTo(1); // Наиболее популярный фильм с id 1
    }

}
