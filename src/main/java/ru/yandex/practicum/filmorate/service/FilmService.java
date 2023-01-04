package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NoLikeException;
import ru.yandex.practicum.filmorate.exception.UnknownFilmException;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final UserService userService;
    private long counterId;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, UserService userService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.userService = userService;
        counterId = 0;
    }

    public long getCounterId() {
        return counterId;
    }

    private long setCounterId() {
        counterId++; // Инкремент счетчика id
        return counterId;
    }

    public Film createFilm(Film film) throws  EntityAlreadyExistException {
        if (inMemoryFilmStorage.getFilmByName(film.getName()).isPresent()) {
            throw new EntityAlreadyExistException("Уже есть фильм с названием: " + film.getId());
        }
        else {
            film.setId(setCounterId());
            inMemoryFilmStorage.createFilm(film);
            return film;
        }
    }

    public Film updateFilm(Film film) throws UnknownFilmException {
        Film currentFilm =  getFilmById(film.getId()); // проверяем, существует фильм, который хотим обновить
        Film updatedFilm = inMemoryFilmStorage.updateFilm(film);
        return updatedFilm;
    }

    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    public Film getFilmById(long filmId) throws UnknownFilmException {
        if (inMemoryFilmStorage.getFilmById(filmId).isPresent()) {
            return inMemoryFilmStorage.getFilmById(filmId).get();
        }
        else {
            throw new UnknownFilmException("Нет фильма с ID:" + filmId);
        }
    }

    public Film getFilmByName(String filmName) throws UnknownFilmException {
        if (inMemoryFilmStorage.getFilmByName(filmName).isPresent()) {
            return inMemoryFilmStorage.getFilmByName(filmName).get();
        }
        else {
            throw new UnknownFilmException("Нет фильма c названием:" + filmName);
        }
    }

    public Film deleteFilm(long id) throws UnknownFilmException {
        Film deletedFilm =  getFilmById(id); // проверяем существует ли фильм, который хотим удалить
        if (inMemoryFilmStorage.deleteFilm(id).isPresent()) {
            return inMemoryFilmStorage.deleteFilm(id).get();
        }
        else {
            throw new UnknownFilmException("Произошла ошибка при удалении фильма с ID:" + id);
        }
    }

    public void addLike(long filmId, long userId) throws EntityAlreadyExistException, UnknownFilmException, UnknownUserException {
        Film currentFilm =  getFilmById(filmId); // проверяем наличие фильма и получаем его
        User currentUser = userService.getUserById(userId); // проверяем наличие пользователя и получаем его

        if (!currentFilm.addLike(userId)) {
            throw new EntityAlreadyExistException(String.format("Пользователь с ID: %d  уже уже поставил лайк фильму с ID: %d " + userId, filmId));
        }
        inMemoryFilmStorage.updateFilm(currentFilm);
    }

    public void deleteLike(long filmId, long userId) throws NoLikeException, UnknownFilmException, UnknownUserException {
        Film currentFilm =  getFilmById(filmId); // проверяем наличие фильма и получаем его
        User currentUser = userService.getUserById(userId); // проверяем наличие пользователя и получаем его

        if (!currentFilm.deleteLike(userId)) {
            throw new NoLikeException(String.format("Пользователь с ID: %d не ставил лайк фильму с ID: %d", userId, filmId));
        }
        inMemoryFilmStorage.updateFilm(currentFilm);
    }

    public List<Film> getTopFilms(int count) {
        return inMemoryFilmStorage.getTopFilms(count);
    }
}
