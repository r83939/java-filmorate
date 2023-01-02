package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NoLikeException;
import ru.yandex.practicum.filmorate.exception.UnknownFilmException;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Optional<Film> getFilm(@PathVariable long id) throws UnknownFilmException {
        Optional<Film> film = filmService.getFilmById(id);
        if (film.isPresent()) {
            return film;
        }
        else {
            throw new UnknownFilmException("Нет фильм с ID:" + id);
        }
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film newFilm) throws EntityAlreadyExistException, UnknownFilmException {
        if (filmService.getFilmByName(newFilm.getName()).isPresent()) {
            throw new EntityAlreadyExistException("Фильм с таким же названием уже был добавлен раннее");
        }
        Film createdFilm = filmService.createFilm(newFilm);
        log.trace("Добавлен фильм: " + createdFilm);
        return createdFilm;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film updateFilm) throws UnknownFilmException {
        if (!filmService.getFilmById(updateFilm.getId()).isPresent()) {
            throw new UnknownFilmException("Фильм с ID " + updateFilm.getId() + " не существует.");
        }
        Film updatedFilm = filmService.updateFilm(updateFilm);
        log.trace("Обновлен фильм: " + updatedFilm);
        return updatedFilm;
    }

    @DeleteMapping("/films/{id}")
    public Optional<Film> deleteFilm(@PathVariable long id) throws UnknownFilmException {
        if (filmService.getFilmById(id).isPresent()) {
            throw new UnknownFilmException("Фильм с ID " + id + " не существует.");
        }
        Optional<Film> deletedFilm = filmService.deleteFilm(id);
        log.trace("Удален фильм: {}", deletedFilm);
        return deletedFilm;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Long addLike(@PathVariable Long id,
                          @PathVariable Long userId) throws UnknownUserException, UnknownFilmException, EntityAlreadyExistException {
        if (filmService.getFilmById(id).isPresent()) {
            throw new UnknownFilmException("Фильм с ID " + id+ " не существует.");
        }
        if (userService.getUserById(userId) == null) {
            throw new UnknownUserException("Пользователь с ID " + id+ " не существует.");
        }
        Long friendUserId =  filmService.addLike(id, userId);
        log.trace(String.format("Пользователь с ID: %d отметил лайком фильм с ID: %d", userId, id));
        return userId;
    }
    @DeleteMapping("/films/{id}/like/{userId}")
    public Long deleteLike(@PathVariable Long id,
                        @PathVariable Long userId) throws UnknownUserException, UnknownFilmException, NoLikeException {
        if (!(filmService.getFilmById(id).isPresent())) {
            throw new UnknownFilmException("Фильм с ID " + id + " не существует.");
        }
        if (userService.getUserById(userId) == null) {
            throw new UnknownUserException("Пользователь с ID " + id + " не существует.");
        }
        Long friendUserId =  filmService.deleteLike(id, userId);
        log.trace(String.format("Пользователь с ID: %d удалил лайк фильма с ID: %d ", userId, id));
        return userId;
    }

    @GetMapping("/films/popular")
    public List<Film> getTopFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count должен быть целым числом больше 0, получено " + count);
        }
        List<Film> topFilms = filmService.getTopFilms(count);
        return topFilms;
    }
}
