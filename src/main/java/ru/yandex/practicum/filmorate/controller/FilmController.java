package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UnknownFilmException;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film newFilm) throws EntityAlreadyExistException, UnknownFilmException {
        if (filmService.getFilmByName(newFilm.getName()) != null) {
            throw new EntityAlreadyExistException("Фильм с таким же названием уже был добавлен раннее");
        }
        Film createdFilm = filmService.createFilm(newFilm);
        log.trace("Добавлен фильм: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updateFilm) throws UnknownFilmException {
        Film updatedFilm = filmService.updateFilm(updateFilm);
        log.trace("Обновлен фильм: {}", updatedFilm);
        return updatedFilm;
    }

    @DeleteMapping("/films/{id}")
    public Optional<Film> deleteFilm(@PathVariable long id) throws UnknownFilmException {
        if (filmService.getFilmById(id)==null) {
            throw new UnknownFilmException("Фильм с ID " + id + " не существует.");
        }
        Optional<Film> deletedFilm = filmService.deleteFilm(id);
        log.trace("Удален фильм: {}", deletedFilm);
        return deletedFilm;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Long addLike(@PathVariable Long id,
                          @PathVariable Long userId) throws UnknownUserException, UnknownFilmException, EntityAlreadyExistException {
        if (filmService.getFilmById(id) == null) {
            throw new UnknownFilmException("Фильм с ID " + id+ " не существует.");
        }
        if (userService.getUserById(userId) == null) {
            throw new UnknownUserException("Пользователь с ID " + id+ " не существует.");
        }
        Long friendUserId =  filmService.addLike(id, userId);
        log.trace("Пользователь с ID: {} отметил лайком фильм с ID: {}", userId, id);
        return userId;
    }
    @DeleteMapping("/films/{id}/like/{userId}")
    public Long deleteLike(@PathVariable Long id,
                        @PathVariable Long userId) throws UnknownUserException, UnknownFilmException, EntityAlreadyExistException {
        if (filmService.getFilmById(id) == null) {
            throw new UnknownFilmException("Фильм с ID " + id+ " не существует.");
        }
        if (userService.getUserById(userId) == null) {
            throw new UnknownUserException("Пользователь с ID " + id+ " не существует.");
        }
        Long friendUserId =  filmService.deleteLike(id, userId);
        log.trace("Пользователь с ID: {} удалил лайк фильма с ID: {}", userId, id);
        return userId;
    }

    @GetMapping("/films/popular?count={count}")
    public List<Film> getTopFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        if (count <= 0) {
            throw new IllegalArgumentException();
        }
        List<Film> topFilms = filmService.getTopFilms(count);
        return topFilms;

    }


}
