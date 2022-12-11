package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int counterId;
    private Map<String, Film> films;


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
        List<Film> filmsList = new ArrayList<>();
        for (Map.Entry entry : films.entrySet()) {
            filmsList.add((Film) entry.getValue());
        }
        return filmsList;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
       return null;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return null;

    }




}
