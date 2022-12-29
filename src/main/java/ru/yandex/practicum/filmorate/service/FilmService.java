package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UnknownFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<Film> getFilmByName(String filmName) throws UnknownFilmException {
        return Optional.ofNullable(inMemoryFilmStorage.getAllFilms().stream()
                .filter(f->f.getName().equals(filmName))
                .findFirst()
                .orElseThrow(()-> new UnknownFilmException(String.format("Фильм с названием %s не найден", filmName))));
    }

    public Optional<Film> deleteFilm(long id) {
       return inMemoryFilmStorage.deleteFilm(id);
    }

    public Long addLike(long filmId, long userId) throws UnknownFilmException, EntityAlreadyExistException {
        Film film = new Film();
        for (Film f : inMemoryFilmStorage.getAllFilms()) {
            if (f.getId() == filmId) {
               film = f;
               break;
            }
        }
        if (!film.addLike(userId)) {
            throw new EntityAlreadyExistException("Пользователь с ID: " + userId + " уже уже поставил лайк фильму с ID: " + filmId);
        }
        inMemoryFilmStorage.updateFilm(film);
        return userId;
    }

    public Long deleteLike(long filmId, long userId) {
        Film film = new Film();
        for (Film f : inMemoryFilmStorage.getAllFilms()) {
            if (f.getId() == filmId) {
                film = f;
                break;
            }
        }
        inMemoryFilmStorage.updateFilm(film);
        return userId;
    }

    public List<Film> getTopFilms(int count) {
        return inMemoryFilmStorage.getAllFilms().stream().sorted((f0, f1) -> {
                    int comp = f0.getLikes().size() > f1.getLikes().size() ? +1 : f0.getLikes().size() < f1.getLikes().size() ? -1 : 0;
                    return comp;
        }).limit(count).collect(Collectors.toList());
    }

}