package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    private int counterId;
    private List<Film> films;

    public InMemoryFilmStorage() {films = new ArrayList<>();}

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
        Optional<Film> deleteFilm = films.stream()
                .filter(f-> f.getId()==id)
                .findFirst();
        films.remove(deleteFilm);
        return deleteFilm;
    }


    @Override
    public List<Film> getAllFilms() {
        return films;
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
