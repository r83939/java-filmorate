package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> deleteFilm(long id);


    List<Film> getAllFilms();

    Film addLike(long filmId, long userId);

    Film deleteLike(long filmId, long userId);




}
