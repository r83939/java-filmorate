package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UnknownFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Optional;

@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    private long counterId;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {

        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
        counterId = 0;
    }

    public long getCounterId() {
        return counterId;
    }

    private long setCounterId() {
        counterId++; // Инкремент счетчика id
        return counterId;
    }

    public Film createFilm(Film film) {
        film.setId(setCounterId());
        inMemoryFilmStorage.createFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        Film updatedFilm = inMemoryFilmStorage.updateFilm(film);
        return updatedFilm;
    }

    public Optional<Film> getFilmById(long filmId) throws UnknownFilmException {
        return Optional.ofNullable(inMemoryFilmStorage.getAllFilms().stream()
                .filter(f -> f.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new UnknownFilmException(String.format("Фильм с ID: {} не найден", filmId))));
    }

    public Long addLike(long filmId, long userId) {

    }



}
