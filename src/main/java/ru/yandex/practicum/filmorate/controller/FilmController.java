package ru.yandex.practicum.filmorate.controller;



import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UnknownFilmException;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int counterId;
    private Map<Integer, Film> films;


    public FilmController() {
        counterId = 0;
        films = new TreeMap<>();
    }

    private int setCounterId() {
        counterId++;
        return counterId; // Инкремент счетчика id
    }

    @GetMapping
    public List<Film> getFilms() {
        return getFilmsList();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film newFilm) throws EntityAlreadyExistException {
        for (Map.Entry entry : films.entrySet()) {
            if(((Film)entry.getValue()).getName().equals(newFilm.getName())) {
                throw new EntityAlreadyExistException("Фильм с таким названием уже был добавлен раннее");
            }
        }
        int id = setCounterId();
        newFilm.setId(id);
        films.put(id, newFilm);
        log.trace("Добавлен фильм: {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updateFilm) throws UnknownFilmException {
        if (films.containsKey(updateFilm.getId())) {
            films.put(updateFilm.getId(), updateFilm);
            log.trace("Изменен фильм: {}", updateFilm);
            return updateFilm;
        }
        else {
            throw new UnknownFilmException("Фильм с указанным id не был найден");
        }
    }

    private List<Film> getFilmsList() { // служебный метод получения списка фильмов
        List<Film> filmsList = new ArrayList<>();
        for (Map.Entry entry : films.entrySet()) {
            filmsList.add((Film) entry.getValue());
        }
        return filmsList;
    }



}
