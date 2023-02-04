package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public class FilmDbStorage implements FilmStorage{
    @Override
    public Film createFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public Optional<Film> deleteFilm(long id) {
        return Optional.empty();
    }

    @Override
    public List<Film> getAllFilms() {
        return null;
    }

    @Override
    public Film addLike(long filmId, long userId) {
        return null;
    }

    @Override
    public Film deleteLike(long filmId, long userId) {
        return null;
    }
}
