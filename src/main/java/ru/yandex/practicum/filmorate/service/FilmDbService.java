package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.util.List;
import java.util.Optional;

@Service
public class FilmDbService {
    private final FilmDbStorage filmStorage;

    private final UserDbService userService;

    @Autowired
    public FilmDbService(FilmDbStorage filmStorage, UserDbService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }


    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Mpa getMpaById(long id) throws UnknownMpaException {
        if (filmStorage.getMpaById(id) != null) {
            return filmStorage.getMpaById(id);
        }
        else {
            throw new UnknownMpaException("Не найден MPA с ID: " + id);
        }
    }

    public Genre getGenreById(long id) throws UnknownGenreException {
        if (filmStorage.getGenreById(id) != null) {
            return filmStorage.getGenreById(id);
        }
        else {
            throw new UnknownGenreException("Не найден MPA с ID: " + id);
        }
    }

    public Film getFilmById(long id) throws UnknownFilmException {
        if (filmStorage.getFilmById(id) == null) {
            throw new UnknownFilmException("Фильм с ID: " + id + "не найден");
        }
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film newFilm) {
        return filmStorage.createFilm(newFilm);
    }

    public Film updateFilm(Film updateFilm) throws UpdateFilmException {
        Film film = filmStorage.updateFilm(updateFilm);
        if (film == null) {
            throw new UpdateFilmException("Произошла ошибка при обновлении фильма с ID:" + updateFilm.getId());
        }
        else {
            return film;
        }
    }

    public Film deleteFilm(long deleteId) throws UnknownFilmException {
        if (filmStorage.deleteFilm(deleteId).isPresent()) {
            return filmStorage.deleteFilm(deleteId).get();
        }
        else {
            throw new UnknownFilmException("Произошла ошибка при удалении фильма с ID:" + deleteId);
        }
    }

    public void addLike(Long filmId, Long userId) throws UnknownFilmException, UnknownUserException {
        if (!userService.isUserExist(userId)) {
            throw new UnknownUserException("Нет пользователя c ID:" + userId);
        };
        if (!filmStorage.isFilmExist(filmId)) {
            throw new UnknownFilmException("Нет фильма c ID:" + filmId);
        };
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) throws UnknownUserException, UnknownFilmException {
        if (!userService.isUserExist(userId)) {
            throw new UnknownUserException("Нет пользователя c ID:" + userId);
        };
        if (!filmStorage.isFilmExist(filmId)) {
            throw new UnknownFilmException("Нет фильма c ID:" + filmId);
        };
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
    }
}
